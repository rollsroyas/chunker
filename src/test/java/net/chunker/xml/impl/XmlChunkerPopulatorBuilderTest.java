package net.chunker.xml.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.SAXException;

import net.chunker.xml.api.XmlChunker;
import net.chunker.xml.impl.XmlChunkerPopulator.Builder;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class XmlChunkerPopulatorBuilderTest {

	private static InputStream INPUT_STREAM = XmlChunkerPopulatorBuilderTest.class
		.getResourceAsStream("/xml/cd_catalog.xml");
	private static SAXParser PARSER;
	{
		try {
			PARSER = SAXParserFactory.newInstance().newSAXParser();
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
	}
	private static XmlChunker CHUNKER = new XmlChunker() {
		@Override
		public void setException(Exception e) {
		}

		@Override
		public void finish() {
		}
	};

	@Test
	public void buildChunkerParserAndInputStreamNotNull() {
		final Builder builder = XmlChunkerPopulator.builder()
			.chunker(CHUNKER)
			.parser(PARSER)
			.inputStream(INPUT_STREAM);
		builder.build();
		assertEquals(CHUNKER, builder.chunker);
		assertEquals(PARSER, builder.parser);
	}
	
	@Test
	public void buildChunkerAndInputStreamNotNull() {
		final Builder builder = XmlChunkerPopulator.builder()
			.chunker(CHUNKER)
			.inputStream(INPUT_STREAM);
		builder.build();
		assertEquals(CHUNKER, builder.chunker);
		assertEquals(INPUT_STREAM, builder.inputStream);
	}
	
	@Test(expected = NullPointerException.class)
	public void buildInputStreamNull() {
		XmlChunkerPopulator.builder()
			.chunker(CHUNKER)
			.build();
	}

	@Test(expected = NullPointerException.class)
	public void buildChunkerNull() {
		XmlChunkerPopulator.builder()
			.inputStream(INPUT_STREAM)
			.build();
	}
}
