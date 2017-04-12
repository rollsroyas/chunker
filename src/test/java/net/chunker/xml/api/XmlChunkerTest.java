package net.chunker.xml.api;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import net.chunker.util.MemoryManagerImpl;
import net.chunker.xml.impl.XmlChunkerImpl;
import net.chunker.xml.impl.XmlChunkerImpl.Builder;
import net.chunker.xml.impl.XmlChunkerQueuePopulator;
import net.chunker.xml.impl.XmlElementMatcherImpl;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.chunker.model.Catalog;
import com.chunker.model.Cd;
import com.chunker.xml.impl.CdChunkFactoryImpl;

/**
 * As of 01Nov2016, the pom.xml was updated to run the tests with -Dfile.encoding=ISO-8859-1
 * Plus a non ISO-8859-1 char was added to the first CD element, to make 
 * sure that the code is using the XML file's encoding and not the default charset.
 * 
 * @author rollsroyas@alumni.ncsu.edu
 *
 */
public class XmlChunkerTest {
	private static final int XML_REPEATED_ELEMENTS_SIZE = 26;
	
	@Test
	public void chunkSizeDefault() throws Exception {
		testChunk();
	}
	
	@Test
	public void chunkSize5() throws Exception {
		testChunk(5);
	}
	
	protected void testChunk() throws JAXBException,
	ParserConfigurationException, SAXException, InterruptedException,
	Exception, IOException {
		testChunk(null);
	}

	protected void testChunk(Integer chunkSize) throws Exception  {
		BlockingQueue<Callable<Catalog>> queue = new ArrayBlockingQueue<Callable<Catalog>>(100);
		XmlElementMatcher matcher = new XmlElementMatcherImpl("CD");
		CdChunkFactoryImpl factory = new CdChunkFactoryImpl();
		//setting these values low to force a GC
		MemoryManagerImpl memoryManager = new MemoryManagerImpl(3, 0.01);
		Builder<Catalog> builder = XmlChunkerImpl.<Catalog>builder();
		if (chunkSize != null) builder.chunkSize(chunkSize);
		XmlChunker chunker = builder
				.queue(queue)
				.matcher(matcher)
				.factory(factory)
				.memoryManager(memoryManager)
				.build();
		
		InputStream inputStream = XmlChunkerTest.class.getResourceAsStream("/xml/cd_catalog.xml");
		XmlChunkerQueuePopulator.builder()
			.chunker(chunker)
			.inputStream(inputStream)
			.build()
			.populate();
		
		if (chunkSize == null) {
			chunkSize = 1; //default size
		}
		
		int numChunks = 0;
		Callable<Catalog> callable;
		Catalog catalog;
		// while ((catalog=queue.take().call()) != null) { 
		// Polling prevents one from potentially getting into an infinite wait condition 
		while ((callable=queue.poll(2, SECONDS)) != null && (catalog = callable.call()) != null) {
			List<Cd> cds = catalog.getCd();
			numChunks++;
			Assert.assertTrue("lte chunk size of "+chunkSize, cds.size() <= chunkSize.intValue());
		}
		Assert.assertEquals(((XML_REPEATED_ELEMENTS_SIZE-1) / chunkSize)+1, numChunks);
	}
}
