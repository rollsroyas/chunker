package net.chunker.xml.impl;

import java.io.InputStream;

import org.junit.Test;

import net.chunker.xml.api.XmlChunker;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class XmlChunkerQueuePopulatorBuilderTest {

	private static InputStream INPUT_STREAM = XmlChunkerQueuePopulatorBuilderTest.class
		.getResourceAsStream("/xml/cd_catalog.xml");
	// private static SAXParser PARSER;
	// {
	// try {
	// PARSER = SAXParserFactory.newInstance().newSAXParser();
	// } catch (ParserConfigurationException | SAXException e) {
	// e.printStackTrace();
	// }
	// }
	private static XmlChunker CHUNKER = new XmlChunker() {
		@Override
		public void setException(Exception e) {
		}

		@Override
		public void finish() {
		}
	};

	@Test(expected = NullPointerException.class)
	public void testInputStreamNull() {
		XmlChunkerQueuePopulator.builder()
			.chunker(CHUNKER)
			.build();
	}

	@Test(expected = NullPointerException.class)
	public void testChunkerNull() {
		XmlChunkerQueuePopulator.builder()
			.inputStream(INPUT_STREAM)
			.build();
	}
}
