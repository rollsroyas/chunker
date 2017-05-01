package net.chunker.json.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import javax.json.stream.JsonParser.Event;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.chunker.model.Catalog;

import net.chunker.json.api.JsonArrayMatcher;
import net.chunker.json.api.JsonChunkFactory;
import net.chunker.json.api.JsonEvent;
import net.chunker.util.Chunkers;

public class JsonChunkerExceptionHandlingTest {

	@Mock
	private BlockingQueue<Callable<Catalog>> queue;
	@Mock
	JsonChunkFactory<Catalog> factory;
	@Mock
	JsonArrayMatcher matcher;
	
	private JsonChunkerImpl<Catalog> toTest;
	
	@Before
	public void before() throws JAXBException {
		MockitoAnnotations.initMocks(this);
				
		toTest = JsonChunkerImpl.<Catalog>builder()
			.queue(queue)
			.matcher(matcher)
			.factory(factory)
			.build();
	}
	
	@Test
	public void finish_Exception() throws InterruptedException, Exception {
		final RuntimeException exception = new RuntimeException("Test");
		toTest.setException(exception);
		toTest.finish();
		
		verify(queue).put(Chunkers.<Catalog>createFinalCallable(exception));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void finish_InterruptedException() throws InterruptedException, Exception {
		final InterruptedException exception = new InterruptedException("Test");		
		doThrow(exception).when(queue).put(any(Callable.class));
		
		toTest.finish();
		
		assertTrue("Expect current thread to be interupted", Thread.interrupted());
	}
	
	@Test
	public void doChunk_Exception() throws InterruptedException, Exception {
		final RuntimeException exception = new RuntimeException("Test");
		
		doThrow(exception).when(factory).create(anyString(), eq(true));
		
		Exception actual = null;
		try {
			doChunk();
		} catch (Exception e) {
			actual = e;
		}
		
		assertEquals(exception, actual);
	}

	private void doChunk() {
		toTest.handleEvent(startArray());
		toTest.handleEvent(endArray());
		toTest.processCurrentEvents(true);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void doChunk_InterruptedException() throws InterruptedException, Exception {
		final InterruptedException exception = new InterruptedException("Test");
		
		doThrow(exception).when(queue).put(nullable(Callable.class));
		
		Exception actual = null;
		try {
			doChunk();
		} catch (Exception e) {
			actual = e;
		}
		
		assertEquals(exception, actual.getCause());
		assertTrue("Expect current thread to be interupted", Thread.interrupted());			
	}

	private JsonEvent startArray() {
		return new JsonEvent() {
			
			@Override
			public boolean isIntegralNumber() {
				return false;
			}
			
			@Override
			public String getString() {
				return null;
			}
			
			@Override
			public int getInt() {
				return 0;
			}
			
			@Override
			public Event getEvent() {
				return Event.START_ARRAY;
			}
			
			@Override
			public BigDecimal getBigDecimal() {
				return null;
			}
		};
	}
	
	private JsonEvent endArray() {
		return new JsonEvent() {
			
			@Override
			public boolean isIntegralNumber() {
				return false;
			}
			
			@Override
			public String getString() {
				return null;
			}
			
			@Override
			public int getInt() {
				return 0;
			}
			
			@Override
			public Event getEvent() {
				return Event.END_ARRAY;
			}
			
			@Override
			public BigDecimal getBigDecimal() {
				return null;
			}
		};
	}

}
