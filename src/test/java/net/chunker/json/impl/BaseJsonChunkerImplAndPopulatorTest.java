package net.chunker.json.impl;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import net.chunker.json.api.JsonArrayMatcher;
import net.chunker.json.api.JsonChunkFactory;
import net.chunker.json.api.JsonChunker;
import net.chunker.json.impl.JsonChunkerImpl.Builder;
import net.chunker.util.MemoryManagerImpl;

public abstract class BaseJsonChunkerImplAndPopulatorTest<A> {
	private static final int JSON_ARRAY_SIZE = 26;

	@Test
	public void chunkSizeDefault() throws Exception {
		testChunk();
	}

	@Test
	public void chunkSize5() throws Exception {
		testChunk(5);
	}
	
	@Test
	public void unmatched() throws Exception {
		testChunk(5, true);
	}

	protected void testChunk() throws Exception {
		testChunk(null);
	}
	
	protected void testChunk(Integer chunkSize) throws Exception {
		testChunk(chunkSize, false);
	}

	protected void testChunk(Integer chunkSize, boolean unmatched) throws Exception {
		
		JsonChunkFactory<A> factory = createFactory();
		
		BlockingQueue<Callable<A>> queue = new ArrayBlockingQueue<Callable<A>>(100);
		
		JsonArrayMatcher matcher = new JsonArrayMatcherImpl(unmatched ? "UNMATCHED" : "CDS");
		Builder<A> builder = JsonChunkerImpl.<A>builder()
			.queue(queue)
			.factory(factory)
			.matcher(matcher);
		if (chunkSize != null) {
			// Showing that memoryManager is not required
			// Setting memoryTolerance low to force a GC, probably .8 would be a good default
			MemoryManagerImpl memoryManager = new MemoryManagerImpl(3, 0.01);
			builder.memoryManager(memoryManager);
	
			builder.chunkSize(chunkSize);
		}
		JsonChunker chunker = builder.build();

		InputStream inputStream = getInputStream();
		JsonChunkerPopulator.builder()
			.chunker(chunker)
			.inputStream(inputStream)
			.build()
			.populate();

		if (chunkSize == null) {
			chunkSize = 1; // default size
		}

		int numChunks = 0;
		Callable<A> callable;
		A parent;
		// while ((catalog=queue.take().call()) != null) {
		// Polling prevents a potential infinite wait condition
		while ((callable = queue.poll(5, SECONDS)) != null && (parent = callable.call()) != null) {
			int size = getSize(parent);
			if (unmatched) {
				assertEquals("Expecting unmatched chunk size to equal total size", JSON_ARRAY_SIZE, size);
			} else {
				assertTrue("Expecting lte chunk size of " + chunkSize, size <= chunkSize.intValue());
			}
			numChunks++;
		}
		if (unmatched) {
			assertEquals("Expecting unmatched to have one chunk", 1, numChunks);
		} else {
			assertEquals("Unexpected number of chunks for matched", ((JSON_ARRAY_SIZE - 1) / chunkSize) + 1, numChunks);
		}
	}
	
	abstract protected InputStream getInputStream();
	
	abstract protected int getSize(A catalog);

	abstract protected JsonChunkFactory<A> createFactory() throws JAXBException;
}