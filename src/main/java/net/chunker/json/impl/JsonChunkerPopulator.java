package net.chunker.json.impl;

import static net.chunker.util.Validations.checkNotBothNull;
import static net.chunker.util.Validations.checkNotBothPresent;
import static net.chunker.util.Validations.checkNotNull;

import java.io.InputStream;

import javax.json.Json;
import javax.json.stream.JsonParser;

import net.chunker.json.api.JsonChunker;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class JsonChunkerPopulator {

	private final JsonParser parser;
	private final JsonChunker chunker;

	private JsonChunkerPopulator(Builder builder) {
		this.chunker = builder.chunker;
		this.parser = builder.parser;
	}

	/**
	 * Populates the JsonChunker in a new background thread.
	 * 
	 * The last element in the queue will we be a Callable whose call method
	 * returns null or it will throw an exception if the sax parser threw one
	 */
	public void populate() {
		new Thread(createPopulatorRunnable()).start();
	}

	protected Runnable createPopulatorRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				try (JsonParser jsonParser = parser) {
					while (jsonParser.hasNext()) {
						chunker.handleEvent(new JsonEventImpl(jsonParser));
					}
				} catch (final Exception e) {
					chunker.setException(e);
				} finally {
					chunker.finish();
				}
			}
		};
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		InputStream inputStream;
		JsonParser parser;
		JsonChunker chunker;

		private Builder() {
		}

		/**
		 * @param inputStream
		 * 			An inputStream or parser is required, but do not supply both.
		 *			Strongly recommended to wrap this InputStream in a
		 *			java.io.BufferedInputStream.
		 * @return this
		 */
		public Builder inputStream(InputStream inputStream) {
			this.inputStream = inputStream;
			return this;
		}

		/**
		 * @param parser
		 * 			An inputStream or parser is required, but do not supply both.
		 * @return this
		 */
		public Builder parser(JsonParser parser) {
			this.parser = parser;
			return this;
		}

		/**
		 * @param chunker
		 * 			This required object gathers the JSON events and creates chunks
		 * @return this
		 */
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
			checkNotBothNull(inputStream, parser, "inputStream and parser cannot both be null");
			checkNotBothPresent(inputStream, parser, "inputStream and parser cannot both be present");
			checkNotNull(chunker, "chunker cannot be null");
		}

		public JsonChunkerPopulator build() {
			validate();
			defaultParserIfNull();
			return new JsonChunkerPopulator(this);
		}
	}
}
