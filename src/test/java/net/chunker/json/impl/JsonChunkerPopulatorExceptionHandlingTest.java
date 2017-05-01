package net.chunker.json.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.json.stream.JsonParser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import net.chunker.json.api.JsonChunker;
import net.chunker.json.api.JsonEvent;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class JsonChunkerPopulatorExceptionHandlingTest {

	private static class SimpleJsonChunker implements JsonChunker {
		
		private Exception exception;
		
		public Exception getException() {
			return exception;
		}
		
		@Override
		public void setException(Exception exception) {
			this.exception = exception;
		}
		
		@Override
		public void handleEvent(JsonEvent event) {}
		
		@Override
		public void finish() {}
	}
	
	@Mock
	private JsonParser parser;
	
	private SimpleJsonChunker chunker;
	private JsonChunkerPopulator toTest;
	
	@Before
	public void before() {
		initMocks(this);
		chunker = new SimpleJsonChunker();
		toTest = JsonChunkerPopulator.builder()
					.parser(parser)
					.chunker(chunker)
					.build();
	}
	
	@Test
	public void populate_bodyException() throws InterruptedException {
		final RuntimeException bodyException = new RuntimeException("Body Exception");
		when(parser.hasNext()).thenThrow(bodyException);
		
		toTest.createPopulatorRunnable().run();
		
		assertEquals(bodyException, chunker.getException());
	}

	@Test
	public void populate_closeException() throws InterruptedException {
		final RuntimeException closeException = new RuntimeException("Close Exception");
		when(parser.hasNext()).thenReturn(Boolean.FALSE);
		doThrow(closeException).when(parser).close();

		toTest.createPopulatorRunnable().run();
		
		assertEquals(closeException, chunker.getException());
	}
	
	@Test
	public void populate_bodyAndcloseException() throws InterruptedException {
		final RuntimeException bodyException = new RuntimeException("Body Exception");
		final RuntimeException closeException = new RuntimeException("Close Exception");
		when(parser.hasNext()).thenThrow(bodyException);
		doThrow(closeException).when(parser).close();
		
		toTest.createPopulatorRunnable().run();
		
		assertEquals(bodyException, chunker.getException());
		assertEquals(closeException, chunker.getException().getSuppressed()[0]);
	}
}
