package net.chunker.xml.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xml.sax.SAXException;

import com.chunker.model.Catalog;

import net.chunker.util.Chunkers;
import net.chunker.xml.api.XmlChunkFactory;
import net.chunker.xml.api.XmlElementMatcher;

public class XmlChunkerExceptionHandlingTest {

	@Mock
	private BlockingQueue<Callable<Catalog>> queue;
	@Mock
	XmlChunkFactory<Catalog> factory;
	@Mock
	XmlElementMatcher matcher;
	
	private XmlChunkerImpl<Catalog> toTest;
	
	@Before
	public void before() throws JAXBException {
		MockitoAnnotations.initMocks(this);
				
		toTest = XmlChunkerImpl.<Catalog>builder()
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
		
		doThrow(exception).when(factory).create(anyString(), eq(false));
		
		SAXException actual = null;
		try {
			toTest.doChunk(false);
		} catch (SAXException e) {
			actual = e;
		}
		
		assertEquals(exception, actual.getCause());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void doChunk_InterruptedException() throws InterruptedException, Exception {
		final InterruptedException exception = new InterruptedException("Test");
		
		doThrow(exception).when(queue).put(nullable(Callable.class));
		
		SAXException actual = null;
		try {
			toTest.doChunk(false);
		} catch (SAXException e) {
			actual = e;
		}
		
		assertEquals(exception, actual.getCause());
		assertTrue("Expect current thread to be interupted", Thread.interrupted());			
	}

}
