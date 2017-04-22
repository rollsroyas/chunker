package net.chunker.json.api;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;

import com.chunker.json.impl.CdChunkFactoryImpl;
import com.chunker.model.Catalog;
import com.chunker.model.Cd;

import net.chunker.json.impl.JsonArrayMatcherImpl;
import net.chunker.json.impl.JsonChunkerImpl;
import net.chunker.json.impl.JsonChunkerImpl.Builder;
import net.chunker.util.MemoryManagerImpl;
import net.chunker.json.impl.JsonChunkerQueuePopulator;

/**
 * @author rollsroyas@alumni.ncsu.edu
 *
 */
public class JsonChunkerTest {
	private static final int JSON_ARRAY_SIZE = 26;

	@Test
	public void chunkSizeDefault() throws Exception {
		testChunk();
	}

	@Test
	public void chunkSize5() throws Exception {
		testChunk(5);
	}

	protected void testChunk() throws Exception {
		testChunk(null);
	}

	protected void testChunk(Integer chunkSize) throws Exception {
		BlockingQueue<Callable<Catalog>> queue = new ArrayBlockingQueue<Callable<Catalog>>(100);
		CdChunkFactoryImpl factory = new CdChunkFactoryImpl();
		JsonArrayMatcher matcher = new JsonArrayMatcherImpl("CD");
		// setting these values low to force a GC
		MemoryManagerImpl memoryManager = new MemoryManagerImpl(3, 0.01);
		Builder<Catalog> builder = JsonChunkerImpl.<Catalog>builder()
			.queue(queue)
			.factory(factory)
			.matcher(matcher)
			.memoryManager(memoryManager);
		if (chunkSize != null)
			builder.chunkSize(chunkSize);
		JsonChunker chunker = builder.build();

		InputStream inputStream = JsonChunkerTest.class.getResourceAsStream("/json/cd_catalog.json");
		JsonChunkerQueuePopulator.builder()
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
		// Polling prevents one from potentially getting into an infinite wait
		// condition
		while ((callable = queue.poll(5, SECONDS)) != null && (catalog = callable.call()) != null) {
			List<Cd> cds = catalog.getCd();
			numChunks++;
			Assert.assertTrue("lte chunk size of " + chunkSize, cds.size() <= chunkSize.intValue());
		}
		Assert.assertEquals(((JSON_ARRAY_SIZE - 1) / chunkSize) + 1, numChunks);
	}
}
