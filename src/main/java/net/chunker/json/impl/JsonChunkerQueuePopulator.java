package net.chunker.json.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.json.Json;
import javax.json.stream.JsonParser;

import net.chunker.json.api.JsonChunker;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class JsonChunkerQueuePopulator {
	
	public static final class Builder {
		private InputStream inputStream;
		private JsonParser parser;
		private JsonChunker chunker;
		
		private Builder() {}
		
		/**
		 * Strongly recommended to wrap this InputStream in a java.io.BufferedInputStream
		 */
		public Builder inputStream(InputStream inputStream) {
			this.inputStream = inputStream;
			return this;
		}
		public Builder parser(JsonParser parser) {
			this.parser = parser;
			return this;
		}
		public Builder chunker(JsonChunker chunker) {
			this.chunker = chunker;
			return this;
		}
		private void defaultParserIfNull() {
			if (this.parser == null) {
				// Use the default (non-validating) parser
				this.parser = Json.createParser(inputStream);
			}
		}
		
		public void validate() {
			if (this.inputStream == null && this.parser == null) throw new NullPointerException("inputStream and parser cannot both be null");
			if (this.inputStream != null && this.parser != null) throw new IllegalStateException("inputStream and parser cannot both be present");
			if (this.chunker == null) throw new NullPointerException("chunker cannot be null");
		}
		
		public JsonChunkerQueuePopulator build() {
			validate();
			defaultParserIfNull();
			return new JsonChunkerQueuePopulator(parser, chunker);
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}

	private final JsonParser parser;
	private final JsonChunker chunker;
	
	private JsonChunkerQueuePopulator(JsonParser parser, JsonChunker chunker) {
		this.chunker=chunker;
		this.parser=parser;		
	}
	
	/**
	 * The last element in the queue will we be a Callable whose call method returns null 
	 * or it will throw an exception if the sax parser threw one
	 */
	public void populate() {		
		new Thread(createPopulatorRunnable()).start();
	}

	protected Runnable createPopulatorRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				try {
					try (JsonParser parser = JsonChunkerQueuePopulator.this.parser) {
						while (parser.hasNext()) {						
							chunker.handleEvent(new JsonEventImpl(parser));					
						}
					}
				} catch (final Exception e) {
					chunker.setException(new IOException("Error around byte #"+parser.getLocation().getStreamOffset(), e));
				} finally {
					chunker.finish();
				}
			}
		};
	}
}
