package net.chunker.xml.impl;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.junit.Test;

import net.chunker.xml.api.XmlChunkFactory;
import net.chunker.xml.api.XmlElementMatcher;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class XmlChunkerImplBuilderTest {

	private static XmlChunkFactory<Object> FACTORY = new XmlChunkFactory<Object>() {
		@Override
		public Callable<Object> create(String chunk, boolean last) {
			return null;
		}
	};
	private static XmlElementMatcher MATCHER = new XmlElementMatcherImpl("test");
	private static BlockingQueue<Callable<Object>> QUEUE = new ArrayBlockingQueue<>(1);

	@Test
	public void testDefaultChunkSize() {
		XmlChunkerImpl.Builder<Object> builder = XmlChunkerImpl.builder();
		assertEquals(1, builder.chunkSize);
	}

	@Test(expected = NullPointerException.class)
	public void testQueueNull() {
		XmlChunkerImpl.builder()
			.factory(FACTORY)
			.matcher(MATCHER)
			.build();
	}

	@Test(expected = NullPointerException.class)
	public void testMatcherNull() {
		XmlChunkerImpl.builder()
			.factory(FACTORY)
			.queue(QUEUE)
			.build();
	}

	@Test(expected = NullPointerException.class)
	public void testFactoryNull() {
		XmlChunkerImpl.builder()
			.matcher(MATCHER)
			.queue(QUEUE)
			.build();
	}
}
