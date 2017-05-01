package net.chunker.xml.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

import net.chunker.xml.api.XmlChunker;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class XmlChunkerPopulatorExceptionHandlingTest {

	private static class SimpleXmlChunker extends XmlChunker {
		
		private Exception exception;
		
		public Exception getException() {
			return exception;
		}
		
		@Override
		public void setException(Exception exception) {
			this.exception = exception;
		}
		
		@Override
		public void finish() {}
	}
	
	@Mock
	private InputStream inputStream;
	@Mock
	private SAXParser parser;
	
	private SimpleXmlChunker chunker;
	private XmlChunkerPopulator toTest;
	
	@Before
	public void before() {
		initMocks(this);
		chunker = new SimpleXmlChunker();
		toTest = XmlChunkerPopulator.builder()
					.parser(parser)
					.chunker(chunker)
					.inputStream(inputStream)
					.build();
	}
	
	@Test
	public void populate_bodyException() throws SAXException, IOException  {
		final IOException bodyException = new IOException("Body Exception");
		doThrow(bodyException).when(parser).parse(Mockito.<InputStream>any(), eq(chunker));
		
		toTest.createPopulatorRunnable().run();
		
		assertEquals(bodyException, chunker.getException().getCause());
	}

	@Test
	public void populate_closeException() throws IOException {
		final IOException closeException = new IOException("Close Exception");
		doThrow(closeException).when(inputStream).close();

		toTest.createPopulatorRunnable().run();
		
		assertEquals(closeException, chunker.getException().getCause());
	}
	
	@Test
	public void populate_bodyAndcloseException() throws SAXException, IOException {
		final IOException bodyException = new IOException("Body Exception");
		doThrow(bodyException).when(parser).parse(Mockito.<InputStream>any(), eq(chunker));
		final IOException closeException = new IOException("Close Exception");
		doThrow(closeException).when(inputStream).close();
		
		toTest.createPopulatorRunnable().run();
		
		assertEquals(bodyException, chunker.getException().getCause());
		assertEquals(closeException, chunker.getException().getCause().getSuppressed()[0]);
	}
}
