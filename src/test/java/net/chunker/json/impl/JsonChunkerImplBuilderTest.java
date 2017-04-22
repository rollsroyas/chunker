package net.chunker.json.impl;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.junit.Test;

import net.chunker.json.api.JsonArrayMatcher;
import net.chunker.json.api.JsonChunkFactory;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class JsonChunkerImplBuilderTest {

	private static JsonChunkFactory<Object> FACTORY = new JsonChunkFactory<Object>() {
		@Override
		public Callable<Object> create(String chunk, boolean last) {
			return null;
		}
	};
	private static JsonArrayMatcher MATCHER = new JsonArrayMatcherImpl("test");
	private static BlockingQueue<Callable<Object>> QUEUE = new ArrayBlockingQueue<>(1);

	@Test
	public void testDefaultChunkSize() {
		JsonChunkerImpl.Builder<Object> builder = JsonChunkerImpl.builder();
		assertEquals(1, builder.chunkSize);
	}

	@Test(expected = NullPointerException.class)
	public void testQueueNull() {
		JsonChunkerImpl.builder()
			.factory(FACTORY)
			.matcher(MATCHER)
			.build();
	}

	@Test(expected = NullPointerException.class)
	public void testMatcherNull() {
		JsonChunkerImpl.builder()
			.factory(FACTORY)
			.queue(QUEUE)
			.build();
	}

	@Test(expected = NullPointerException.class)
	public void testFactoryNull() {
		JsonChunkerImpl.builder()
			.matcher(MATCHER)
			.queue(QUEUE)
			.build();
	}
}
