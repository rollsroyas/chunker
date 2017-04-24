package net.chunker.json.impl;

import static net.chunker.json.event.NamedEventFactory.namedEventFactory;
import static net.chunker.util.Validations.checkNotNull;

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
import net.chunker.util.Chunkers;
import net.chunker.util.MemoryManager;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class JsonChunkerImpl<A> implements JsonChunker {

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

	private List<NamedEvent> beforeMatchEvents;
	private List<NamedEvent> currentEvents;

	private JsonChunkerImpl(Builder<A> builder) {
		this.queue = builder.queue;
		this.matcher = builder.matcher;
		this.factory = builder.factory;
		this.chunkSize = builder.chunkSize;

		this.memoryManager = builder.memoryManager;

		this.beforeMatchEvents = new ArrayList<>();
		this.currentEvents = new ArrayList<>();

		this.exception = null;
		this.chunkCount = 0;
		this.inMatchedArrayStartObjectEventCount = 0;
		this.inMatchedArray = false;
		this.currentName = null;
	}

	/**
	 * @see JsonChunker#handleEvent(JsonEvent)
	 * @param jsonEvent
	 *            The next event to be used to create a chunk of JSON
	 */
	@Override
	public void handleEvent(JsonEvent jsonEvent) {
		final NamedEvent currentNamedEvent = ifNotKeyNameThenCreateNamedEvent(jsonEvent);
		ifPresentAndNotInMatchThenAddToBeforeMatchEvents(currentNamedEvent);
		Event event = jsonEvent.getEvent();
		switch (event) {
		case KEY_NAME:
			setCurrentName(jsonEvent);
			break;
		case START_ARRAY:
			ifFirstMatchThenAddBeforeMatchEventsToCurrentEvents();
			break;
		case START_OBJECT:
			ifInMatchAndIfChunkCountEqualsChunkSizeThenProcessChunkAndResetCurrentEvents();
			break;
		case END_OBJECT:
			ifInMatchAndIfAppropriateIncrementChunkCount();
			break;
		default:
			break;
		}
		if (notKeyName(currentNamedEvent)) {
			ifInMatchThenAddToCurrentEvent(currentNamedEvent);
			resetCurrentName();
		}
	}

	private NamedEvent ifNotKeyNameThenCreateNamedEvent(JsonEvent jsonEvent) {
		Event event = jsonEvent.getEvent();
		final NamedEvent currentNamedEvent;
		if (event != Event.KEY_NAME) {
			currentNamedEvent = namedEventFactory().create(jsonEvent, currentName);
		} else {
			currentNamedEvent = null;
		}
		return currentNamedEvent;
	}

	private void ifPresentAndNotInMatchThenAddToBeforeMatchEvents(final NamedEvent currentNamedEvent) {
		if (currentNamedEvent != null && !inMatchedArray) {
			beforeMatchEvents.add(currentNamedEvent);
		}
	}

	private void setCurrentName(JsonEvent jsonEvent) {
		currentName = jsonEvent.getString();
	}

	private void ifFirstMatchThenAddBeforeMatchEventsToCurrentEvents() {
		if (!inMatchedArray && matcher.acceptName(currentName)) {
			inMatchedArray = true;
			initializeCurrentEventsWithBeforeMatchEvents();
		}
	}

	private void initializeCurrentEventsWithBeforeMatchEvents() {
		currentEvents = new ArrayList<>(beforeMatchEvents);
	}

	private void ifInMatchAndIfChunkCountEqualsChunkSizeThenProcessChunkAndResetCurrentEvents() {
		if (inMatchedArray) {
			if (chunkCount % chunkSize == 0) {
				processCurrentEvents(false);
				initializeCurrentEventsWithBeforeMatchEvents();
			}
			inMatchedArrayStartObjectEventCount++;
		}
	}

	private void processCurrentEvents(final boolean lastChunk) {
		final boolean mustProcessBeforeMatchEvents = lastChunk && !inMatchedArray;
		if (chunkCount > 0 || mustProcessBeforeMatchEvents) {
			if (mustProcessBeforeMatchEvents) {
				initializeCurrentEventsWithBeforeMatchEvents();
			}
			String text = currentEventsToChunk(lastChunk);

			LOG.trace("Chunk text:\n{}", text);

			putChunkOnQueue(lastChunk, text);

			gcIfNecessary();
		}
	}

	private String currentEventsToChunk(final boolean lastChunk) {
		StringWriter stringWriter = new StringWriter();
		try (JsonGenerator generator = Json.createGenerator(stringWriter)) {
			applyCurrentEventsToGenerator(generator);

			if (!lastChunk) {
				writeEndForStartEventsWithoutAnEndEvent(generator);
			}
		}
		return stringWriter.toString();
	}

	private void writeEndForStartEventsWithoutAnEndEvent(JsonGenerator generator) {
		for (NamedEvent event : beforeMatchEvents) {
			if (event.isStart()) {
				generator.writeEnd();
			}
		}
	}

	private void applyCurrentEventsToGenerator(JsonGenerator generator) {
		for (NamedEvent event : currentEvents) {
			event.applyTo(generator);
		}
	}

	private void gcIfNecessary() {
		MemoryManager memMan = this.memoryManager;
		if (memMan != null) {
			memMan.gcIfNecessary();
		}
	}

	private void putChunkOnQueue(final boolean lastChunk, String text) {
		Callable<A> chunk = factory.create(text, lastChunk);
		try {
			queue.put(chunk);
		} catch (InterruptedException e) {
			Thread.currentThread()
				.interrupt();
			throw new JsonChunkingException("Unable to process: " + text, e);
		}
	}

	private void ifInMatchAndIfAppropriateIncrementChunkCount() {
		if (inMatchedArray) {
			inMatchedArrayStartObjectEventCount--;
			if (inMatchedArrayStartObjectEventCount == 0) {
				chunkCount++;
			}
		}
	}

	private boolean notKeyName(final NamedEvent currentNamedEvent) {
		return currentNamedEvent != null;

	}

	private void ifInMatchThenAddToCurrentEvent(final NamedEvent currentNamedEvent) {
		if (inMatchedArray) {
			currentEvents.add(currentNamedEvent);
		}

	}

	private void resetCurrentName() {
		currentName = null;
	}

	/**
	 * @see JsonChunker#setException(Exception)
	 * @param e
	 *            Exception to be propogated via the last Callable on the queue
	 */
	@Override
	public void setException(Exception e) {
		if (exception == null) {
			exception = e;
		} else {
			exception.addSuppressed(e);
		}
	}

	/**
	 * @see JsonChunker#finish()
	 */
	@Override
	public void finish() {
		try {
			processCurrentEvents(true);
		} catch(RuntimeException e) {
			setException(e);
		} finally {
			donePuttingChunksOnQueue();
		}
	}

	private void donePuttingChunksOnQueue() {
		try {
			Callable<A> finalCallable = Chunkers.<A>createFinalCallable(exception);
			queue.put(finalCallable);
		} catch (InterruptedException e1) {
			LOG.error("Unable to add final chunk to queue", exception);
			Thread.currentThread()
				.interrupt();
		}
	}

	public static final class Builder<A> {
		private MemoryManager memoryManager;
		private BlockingQueue<Callable<A>> queue;
		private JsonArrayMatcher matcher;
		private JsonChunkFactory<A> factory;
		int chunkSize;

		private Builder() {
			chunkSize = 1;
		}

		public Builder<A> chunkSize(int chunkSize) {
			this.chunkSize = chunkSize;
			return this;
		}

		public Builder<A> memoryManager(MemoryManager memoryManager) {
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
			checkNotNull(queue, "queue cannot be null");
			checkNotNull(matcher, "matcher cannot be null");
			checkNotNull(factory, "factory cannot be null");
		}

		public JsonChunkerImpl<A> build() {
			validate();
			return new JsonChunkerImpl<>(this);
		}
	}

	public static <A> Builder<A> builder() {
		return new Builder<>();
	}
}
