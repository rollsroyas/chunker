package net.chunker.json.impl;

import static net.chunker.json.event.NamedEventFactory.namedEventFactory;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.chunker.json.api.JsonArrayMatcher;
import net.chunker.json.api.JsonChunkFactory;
import net.chunker.json.api.JsonChunker;
import net.chunker.json.api.JsonEvent;
import net.chunker.json.event.NamedEvent;
import net.chunker.util.MemoryManager;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class JsonChunkerImpl<A> implements JsonChunker {
	
	public static final class Builder<A> {
		private MemoryManager memoryManager;
		private BlockingQueue<Callable<A>> queue;
		private JsonArrayMatcher matcher;
		private JsonChunkFactory<A> factory;
		private int chunkSize = 1;

		private Builder() {}

		public Builder<A> chunkSize(int chunkSize) {
			this.chunkSize = chunkSize;
			return this;
		}
		
		public Builder<A> memoryManager (MemoryManager memoryManager) {
			this.memoryManager = memoryManager;
			return this;
		}
		
		public Builder<A> queue(BlockingQueue<Callable<A>> queue) {
			this.queue = queue;
			return this;
		}
		
		public Builder<A> factory(JsonChunkFactory<A> factory) {
			this.factory = factory;
			return this;
		}
		
		public Builder<A> matcher(JsonArrayMatcher matcher) {
			this.matcher = matcher;
			return this;
		}
		
		public void validate() {
			if (queue == null) throw new NullPointerException("queue cannot be null");
			if (matcher == null) throw new NullPointerException("matcher cannot be null");
			if (factory == null) throw new NullPointerException("factory cannot be null");
		}
		
		public JsonChunkerImpl<A> build() {
			validate();			
			return new JsonChunkerImpl<>(queue, matcher, factory, chunkSize, memoryManager);
		}
	}
	
	public static <A> Builder<A> builder() {
		return new Builder<>();
	}
	
	private static Logger LOG = LoggerFactory.getLogger(JsonChunkerImpl.class);
    
    private final BlockingQueue<Callable<A>> queue;
	private final JsonArrayMatcher matcher;
	private final JsonChunkFactory<A> factory;
	private final int chunkSize;
	private final MemoryManager memoryManager;
	
	private Exception exception;
	private int chunkCount;
	private int inMatchedArrayStartObjectEventCount;
	private boolean inMatchedArray;
	private String currentName;
	
	private List<NamedEvent> eventsBeforeMatch;
	private List<NamedEvent> currentEvents;	

	private JsonChunkerImpl(BlockingQueue<Callable<A>> queue, JsonArrayMatcher matcher, JsonChunkFactory<A> factory, int chunkSize, MemoryManager memoryManager) {
	   this.queue=queue;
	   this.matcher=matcher;
	   this.factory=factory;
	   this.chunkSize=chunkSize;
	   
	   this.memoryManager = memoryManager;
	   
	   this.eventsBeforeMatch = new ArrayList<>();
	   this.currentEvents = new ArrayList<>();
	   
	   this.exception = null;
	   this.chunkCount = 0;
	   this.inMatchedArrayStartObjectEventCount = 0;
	   this.inMatchedArray = false;
	   this.currentName = null;
	}
	
	/**
	 * @see JsonChunker#handleEvent(JsonEvent)
	 */
	@Override
	public void handleEvent(JsonEvent jsonEvent) {
		Event event = jsonEvent.getEvent();
		final NamedEvent currentNamedEvent;
		if (event != Event.KEY_NAME) {
			currentNamedEvent = namedEventFactory().create(jsonEvent, currentName);
	        if (!inMatchedArray) {       	
				eventsBeforeMatch.add(currentNamedEvent);
	        }
		} else {
			currentNamedEvent = null;
		}
		
		switch (event) {
			case KEY_NAME:
				currentName = jsonEvent.getString();
				break;
			case START_ARRAY:
				if (matcher.acceptName(currentName)) {
					inMatchedArray = true;
					currentEvents = new ArrayList<>(eventsBeforeMatch);	
				}
				break;					
			case START_OBJECT:
				if (inMatchedArray) {
					if (chunkCount % chunkSize == 0) {
						processEvents(false);
						currentEvents = new ArrayList<>(eventsBeforeMatch);
					}
					inMatchedArrayStartObjectEventCount++;
				}
				break;					
			case END_OBJECT:
				if (inMatchedArray) {
					inMatchedArrayStartObjectEventCount--;
					if (inMatchedArrayStartObjectEventCount == 0) {
						chunkCount++;
					}
				}  
				break;
			default:
				break;
		}
		if (event != Event.KEY_NAME) {
			if (inMatchedArray) {       	
	        	currentEvents.add(currentNamedEvent);
	        }
	        currentName = null;
		}
	}

	private void processEvents(final boolean lastChunk) {
		if (chunkCount > 0) {
			String text = eventsToChunk(lastChunk);
			
			LOG.trace("Chunk text:\n{}", text);
			
			putChunkOnQueue(lastChunk, text);
			
			gcIfNecessary();
		}
	}

	private void gcIfNecessary() {
		MemoryManager memMan = this.memoryManager;
		if (memMan != null) memMan.gcIfNecessary();
	}

	private void putChunkOnQueue(final boolean lastChunk, String text) {
		Callable<A> chunk = factory.create(text, lastChunk);
		try {
			queue.put(chunk);
		} catch (InterruptedException e) {
			throw new RuntimeException("Unable to process: "+text, e);
		}
	}

	private String eventsToChunk(final boolean lastChunk) {
		StringWriter stringWriter = new StringWriter();
		try (JsonGenerator generator = Json.createGenerator(stringWriter)) {
			applyEventsToGenerator(generator);
			
			if (!lastChunk) {
				writeEndForStartEventsWithoutAnEndEvent(generator);
			}
		}			
		return stringWriter.toString();
	}

	private void writeEndForStartEventsWithoutAnEndEvent(JsonGenerator generator) {
		for (NamedEvent event : eventsBeforeMatch) {
			if (event.isStart()) {
				generator.writeEnd();
			}
		}
	}

	private void applyEventsToGenerator(JsonGenerator generator) {
		for (NamedEvent event : currentEvents) {
			event.applyTo(generator);				
		}
	}

	/**
	 * @see JsonChunker#setException(Exception)
	 * Set exception to be propogated via the last Callable on the queue
	 * @param e
	 */
	@Override
	public void setException(Exception e) {
		this.exception = e;
	}

	/**
	 @see JsonChunker#finish()
	 */
	@Override
	public void finish() {
		processEvents(true);
		
		donePuttingChunksOnQueue();
	}

	private void donePuttingChunksOnQueue() {
		try {
			queue.put(new Callable<A>() {
				public A call() throws Exception {
					Exception e = exception;
					if (e != null) throw e;
					return null;
				}
			});	
		} catch (InterruptedException e1) {
			LOG.error("Unable to add final chunk to queue", e1);
		}
	}
}
