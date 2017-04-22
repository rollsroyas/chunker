package net.chunker.json.impl;

import java.io.InputStream;

import javax.json.Json;
import javax.json.stream.JsonParser;

import org.junit.Test;

import net.chunker.json.api.JsonChunker;
import net.chunker.json.api.JsonEvent;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class JsonChunkerQueuePopulatorBuilderTest {

	private static InputStream INPUT_STREAM = JsonChunkerQueuePopulatorBuilderTest.class
		.getResourceAsStream("/json/cd_catalog.json");
	private static JsonParser PARSER = Json.createParser(INPUT_STREAM);
	private static JsonChunker CHUNKER = new JsonChunker() {
		@Override
		public void setException(Exception e) {
		}

		@Override
		public void handleEvent(JsonEvent event) {
		}

		@Override
		public void finish() {
		}
	};

	@Test(expected = NullPointerException.class)
	public void testInputStreamAndParserNull() {
		JsonChunkerQueuePopulator.builder()
			.chunker(CHUNKER)
			.build();
	}

	@Test(expected = IllegalStateException.class)
	public void testInputStreamAndParserPresent() {
		JsonChunkerQueuePopulator.builder()
			.chunker(CHUNKER)
			.inputStream(INPUT_STREAM)
			.parser(PARSER)
			.build();
	}

	@Test(expected = NullPointerException.class)
	public void testChunkerNull() {
		JsonChunkerQueuePopulator.builder()
			.inputStream(INPUT_STREAM)
			.build();
	}
}
