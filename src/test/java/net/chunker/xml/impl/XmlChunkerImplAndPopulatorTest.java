package net.chunker.xml.impl;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.chunker.model.Catalog;
import com.chunker.model.Cd;
import com.chunker.xml.impl.CdChunkFactoryImpl;

import net.chunker.util.MemoryManagerImpl;
import net.chunker.xml.api.XmlChunker;
import net.chunker.xml.api.XmlElementMatcher;
import net.chunker.xml.impl.XmlChunkerImpl.Builder;

/**
 * As of 01Nov2016, the pom.xml was updated to run the tests with
 * -Dfile.encoding=ISO-8859-1 Plus a non ISO-8859-1 char was added to the first
 * CD element, to make sure that the code is using the XML file's encoding and
 * not the default charset.
 * 
 * @author rollsroyas@alumni.ncsu.edu
 *
 */
public class XmlChunkerImplAndPopulatorTest {
	private static final int XML_REPEATED_ELEMENTS_SIZE = 26;

	@Test
	public void isBlankCharSeq_Empty() throws Exception {
		assertTrue("Expected empty string to be blank", XmlChunkerImpl.isBlank(""));
	}
	
	@Test
	public void isBlankCharSeq_Null() throws Exception {
		assertTrue("Expected empty string to be blank", XmlChunkerImpl.isBlank((String)null));
	}
	
	@Test
	public void isBlankCharSeq_Space() throws Exception {
		assertTrue("Expected empty string to be blank", XmlChunkerImpl.isBlank(" "));
	}
	
	@Test
	public void isBlankCharSeq_MultipleWs() throws Exception {
		assertTrue("Expected empty string to be blank", XmlChunkerImpl.isBlank(" \t"));
	}
	
	@Test
	public void isBlankCharSeq_NotBlank() throws Exception {
		assertFalse("Expected empty string to be blank", XmlChunkerImpl.isBlank("NotBlank"));
	}
	
	@Test
	public void isBlankText_Empty() throws Exception {		
		assertTrue("Expected empty string to be blank", XmlChunkerImpl.isBlank(text("")));
	}

	private Text text(String wholeText) {
		Text text = Mockito.mock(Text.class);
		Mockito.when(text.getWholeText()).thenReturn(wholeText);
		return text;
	}
	
	@Test
	public void isBlankText_Null() throws Exception {
		assertTrue("Expected empty string to be blank", XmlChunkerImpl.isBlank(text(null)));
	}
	
	@Test
	public void isBlankText_Space() throws Exception {
		assertTrue("Expected empty string to be blank", XmlChunkerImpl.isBlank(text(" ")));
	}
	
	@Test
	public void isBlankText_MultipleWs() throws Exception {
		assertTrue("Expected empty string to be blank", XmlChunkerImpl.isBlank(text(" \t")));
	}
	
	@Test
	public void isBlankText_NotBlank() throws Exception {
		assertFalse("Expected empty string to be blank", XmlChunkerImpl.isBlank(text("NotBlank")));
	}
	
	@Test
	public void isBlankNonText() throws Exception {
		assertFalse("Expected non-Text node to return false", XmlChunkerImpl.isBlank(Mockito.mock(Node.class)));
	}
	
	@Test
	public void chunkSizeDefault() throws Exception {
		testChunk();
	}

	@Test
	public void chunkSize5() throws Exception {
		testChunk(5);
	}

	protected void testChunk() throws JAXBException, ParserConfigurationException, SAXException, InterruptedException,
			Exception, IOException {
		testChunk(null);
	}

	protected void testChunk(Integer chunkSize) throws Exception {
		BlockingQueue<Callable<Catalog>> queue = new ArrayBlockingQueue<Callable<Catalog>>(100);
		XmlElementMatcher matcher = new XmlElementMatcherImpl("CD");
		CdChunkFactoryImpl factory = new CdChunkFactoryImpl();
		// setting these values low to force a GC
		MemoryManagerImpl memoryManager = new MemoryManagerImpl(3, 0.01);
		Builder<Catalog> builder = XmlChunkerImpl.<Catalog>builder();
		if (chunkSize != null) {
			builder.chunkSize(chunkSize);
		}
		XmlChunker chunker = builder.queue(queue)
			.matcher(matcher)
			.factory(factory)
			.memoryManager(memoryManager)
			.build();

		InputStream inputStream = XmlChunkerImplAndPopulatorTest.class.getResourceAsStream("/xml/cd_catalog.xml");
		XmlChunkerPopulator.builder()
			.chunker(chunker)
			.inputStream(inputStream)
			.build()
			.populate();

		if (chunkSize == null) {
			chunkSize = 1; // default size
		}

		int numChunks = 0;
		Callable<Catalog> callable;
		Catalog catalog;
		// while ((catalog=queue.take().call()) != null) {
		// Polling prevents a potential infinite wait condition
		while ((callable = queue.poll(5, SECONDS)) != null && (catalog = callable.call()) != null) {
			List<Cd> cds = catalog.getCds();
			numChunks++;
			assertTrue("lte chunk size of " + chunkSize, cds.size() <= chunkSize.intValue());
		}
		assertEquals(((XML_REPEATED_ELEMENTS_SIZE - 1) / chunkSize) + 1, numChunks);
	}
}
