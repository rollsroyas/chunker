package net.chunker.json.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import javax.json.Json;
import javax.json.stream.JsonParser;

import org.junit.Test;

import net.chunker.json.api.JsonChunker;
import net.chunker.json.api.JsonEvent;
import net.chunker.json.impl.JsonChunkerPopulator.Builder;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class JsonChunkerPopulatorBuilderTest {

	private static InputStream INPUT_STREAM = JsonChunkerPopulatorBuilderTest.class
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
	
	@Test
	public void buildChunkerAndParserNotNull() {
		final Builder builder = JsonChunkerPopulator.builder()
			.chunker(CHUNKER)
			.parser(PARSER);
		builder.build();
		assertEquals(CHUNKER, builder.chunker);
		assertEquals(PARSER, builder.parser);
	}
	
	@Test
	public void buildChunkerAndInputStreamNotNull() {
		final Builder builder = JsonChunkerPopulator.builder()
			.chunker(CHUNKER)
			.inputStream(INPUT_STREAM);
		builder.build();
		assertEquals(CHUNKER, builder.chunker);
		assertEquals(INPUT_STREAM, builder.inputStream);
	}
	
	@Test(expected = NullPointerException.class)
	public void buildInputStreamAndParserNull() {
		JsonChunkerPopulator.builder()
			.chunker(CHUNKER)
			.build();
	}

	@Test(expected = IllegalStateException.class)
	public void buildInputStreamAndParserPresent() {
		JsonChunkerPopulator.builder()
			.chunker(CHUNKER)
			.inputStream(INPUT_STREAM)
			.parser(PARSER)
			.build();
	}

	@Test(expected = NullPointerException.class)
	public void buildChunkerNull() {
		JsonChunkerPopulator.builder()
			.inputStream(INPUT_STREAM)
			.build();
	}
}
