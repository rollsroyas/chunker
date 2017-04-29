package net.chunker.xml.impl;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;

import org.junit.Test;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import net.chunker.util.MemoryManager;
import net.chunker.util.MemoryManagerImpl;
import net.chunker.xml.api.XmlChunkFactory;
import net.chunker.xml.api.XmlElementMatcher;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class XmlChunkerImplBuilderTest {
	
	public static class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {
		
		@Override
		public void setFeature(String name, boolean value) throws ParserConfigurationException {
			throw new ParserConfigurationException();
		}
		
		@Override
		public void setAttribute(String name, Object value) throws IllegalArgumentException {}
		
		@Override
		public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
			throw new ParserConfigurationException();
		}
		
		@Override
		public boolean getFeature(String name) throws ParserConfigurationException {
			throw new ParserConfigurationException();
		}
		
		@Override
		public Object getAttribute(String name) throws IllegalArgumentException {
			return null;
		}
	}
	
	public static class TransformerFactoryImpl extends TransformerFactory {
		
		@Override
		public void setURIResolver(URIResolver resolver) {}
		
		@Override
		public void setFeature(String name, boolean value) throws TransformerConfigurationException {
			throw new TransformerConfigurationException();
		}
		
		@Override
		public void setErrorListener(ErrorListener listener) {}
		
		@Override
		public void setAttribute(String name, Object value) {}
		
		@Override
		public Transformer newTransformer(Source source) throws TransformerConfigurationException {
			throw new TransformerConfigurationException();
		}
		
		@Override
		public Transformer newTransformer() throws TransformerConfigurationException {
			throw new TransformerConfigurationException();
		}
		
		@Override
		public Templates newTemplates(Source source) throws TransformerConfigurationException {
			throw new TransformerConfigurationException();
		}
		
		@Override
		public URIResolver getURIResolver() {
			return null;
		}
		
		@Override
		public boolean getFeature(String name) {
			return false;
		}
		
		@Override
		public ErrorListener getErrorListener() {
			return null;
		}
		
		@Override
		public Object getAttribute(String name) {
			return null;
		}
		
		@Override
		public Source getAssociatedStylesheet(Source source, String media, String title, String charset)
				throws TransformerConfigurationException {
			throw new TransformerConfigurationException();
		}
	}
	
	private static final String TRANSFORMER_FACTORY = "javax.xml.transform.TransformerFactory";
	private static final String DOCUMENT_BUILDER_FACTORY = "javax.xml.parsers.DocumentBuilderFactory";
	
	private static XmlChunkFactory<Object> FACTORY = new XmlChunkFactory<Object>() {
		@Override
		public Callable<Object> create(String chunk, boolean last) {
			return null;
		}
	};
	private static XmlElementMatcher MATCHER = new XmlElementMatcherImpl("test");
	private static BlockingQueue<Callable<Object>> QUEUE = new ArrayBlockingQueue<>(1);

	@Test
	public void buildSetEverything() throws Exception {
		
		Document document = document();
		Transformer transformer = transformer();
		MemoryManager memoryManager = memoryManager();		
		final int chunkSize = 2;
		
		XmlChunkerImpl.Builder<Object> builder = XmlChunkerImpl.builder()
				.chunkSize(chunkSize)
				.factory(FACTORY)
				.matcher(MATCHER)
				.queue(QUEUE)
				.memoryManager(memoryManager)
				.document(document)
				.transformer(transformer);
		builder.build();
		
		assertEquals(chunkSize, builder.chunkSize);
		assertEquals(FACTORY, builder.factory);
		assertEquals(MATCHER, builder.matcher);
		assertEquals(QUEUE, builder.queue);
		assertEquals(memoryManager, builder.memoryManager);
		assertEquals(document, builder.document);
		assertEquals(transformer, builder.transformer);

		
	}

	private MemoryManagerImpl memoryManager() {
		return new MemoryManagerImpl(100, 0.75);
	}

	private Transformer transformer() throws TransformerConfigurationException, TransformerFactoryConfigurationError {
		return TransformerFactory.newInstance()
				.newTransformer();
	}

	private Document document() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		DocumentBuilder builder;
		builder = docFactory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();

		// Create the document
		Document document = impl.createDocument(null, null, null);
		return document;
	}

	@Test
	public void buildDefaultChunkSize() {
		XmlChunkerImpl.Builder<Object> builder = XmlChunkerImpl.builder();
		assertEquals(1, builder.chunkSize);
	}

	@Test(expected = NullPointerException.class)
	public void buildQueueNull() {
		XmlChunkerImpl.builder()
			.factory(FACTORY)
			.matcher(MATCHER)
			.build();
	}

	@Test(expected = NullPointerException.class)
	public void buildMatcherNull() {
		XmlChunkerImpl.builder()
			.factory(FACTORY)
			.queue(QUEUE)
			.build();
	}

	@Test(expected = NullPointerException.class)
	public void buildFactoryNull() {
		XmlChunkerImpl.builder()
			.matcher(MATCHER)
			.queue(QUEUE)
			.build();
	}
	
	@Test(expected=IllegalStateException.class)
	public void buildDefaultTransformer_TransformerConfigurationException() {
		
		TransformerFactory factory = factoryThatThrowsTransformerConfigurationException();
		
		String prevTransformerFactoryClassname = setSystemPropertyByKeyAndClassname(TRANSFORMER_FACTORY, factory.getClass().getName());
		try {
			XmlChunkerImpl.builder()
					.factory(FACTORY)
					.matcher(MATCHER)
					.queue(QUEUE)
					.build();
		} finally {
			restoreSystemPropertyByKeyAndClassname(TRANSFORMER_FACTORY, prevTransformerFactoryClassname);
		}
	}
	
	@Test(expected=IllegalStateException.class)
	public void buildDefaultDocument_ParserConfigurationException() {
		
		DocumentBuilderFactory factory = factoryThatThrowsParserConfigurationException();
		
		String prevFactoryClassname = setSystemPropertyByKeyAndClassname(DOCUMENT_BUILDER_FACTORY, factory.getClass().getName());
		try {
			XmlChunkerImpl.builder()
					.factory(FACTORY)
					.matcher(MATCHER)
					.queue(QUEUE)
					.build();
		} finally {
			restoreSystemPropertyByKeyAndClassname(DOCUMENT_BUILDER_FACTORY, prevFactoryClassname);
		}
	}

	private DocumentBuilderFactory factoryThatThrowsParserConfigurationException() {
		return new DocumentBuilderFactoryImpl();
	}

	private TransformerFactory factoryThatThrowsTransformerConfigurationException() {
		return new TransformerFactoryImpl();
	}

	private String setSystemPropertyByKeyAndClassname(String systemPropertyKey, String classname) {
		String prevClassname = System.getProperty(systemPropertyKey);	
		System.setProperty(systemPropertyKey, classname);
		return prevClassname;
	}

	private void restoreSystemPropertyByKeyAndClassname(String systemPropertyKey, String transformerFactoryClassname) {
		if (transformerFactoryClassname != null) {
			System.setProperty(systemPropertyKey, transformerFactoryClassname);
		} else {
			System.getProperties().remove(systemPropertyKey);
		}
	}
}
