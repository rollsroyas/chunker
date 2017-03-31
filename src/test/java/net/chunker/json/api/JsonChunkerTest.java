package net.chunker.json.api;

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
import net.chunker.json.impl.JsonChunkerQueuePopulator;

/**
 * @author rollsroyas@alumni.ncsu.edu
 *
 */
public class JsonChunkerTest {
	
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

	protected void testChunk(Integer chunkSize) throws Exception  {
		BlockingQueue<Callable<Catalog>> queue = new ArrayBlockingQueue<Callable<Catalog>>(100);
		CdChunkFactoryImpl factory = new CdChunkFactoryImpl();
		JsonArrayMatcher matcher = new JsonArrayMatcherImpl("CD");
		Builder<Catalog> builder = JsonChunkerImpl.<Catalog>builder()
				.queue(queue)
				.factory(factory)
				.matcher(matcher);
		if (chunkSize != null) builder.chunkSize(chunkSize);
		JsonChunker chunker = builder.build();
		
		InputStream inputStream = JsonChunkerTest.class.getResourceAsStream("/json/cd_catalog.json");
		JsonChunkerQueuePopulator.builder()
			.chunker(chunker)
			.inputStream(inputStream)
			.build()
			.populate();
		
		Catalog catalog;
		while ((catalog=queue.take().call()) != null) {				
			List<Cd> cds = catalog.getCd();
			if (chunkSize == null) {
				chunkSize = 1; //default size
			}
			Assert.assertTrue("lte chunk size of "+chunkSize, cds.size() <= chunkSize.intValue());
		}
	}
}
