package net.chunker.util;

import static net.chunker.util.MemoryManagerImpl.formatLong;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class MemoryManagerImplTest {

	@Test
	public void formatLong_Zero() {
		assertEquals("0", formatLong(0));
	}
	@Test
	public void formatLong_NoComma() {
		assertEquals("1", formatLong(1));
	}
	@Test
	public void formatLong_Comma() {
		assertEquals("1,000", formatLong(1000));
	}
	@Test
	public void formatLong_Negative_NoComma() {
		assertEquals("-1", formatLong(-1));
	}
	@Test
	public void formatLong_Negative_Comma() {
		assertEquals("-1,000", formatLong(-1000));
	}
	@Test
	public void maxMemoryNotSupportedByJre() {
		Runtime rt = mock(Runtime.class);
		when(rt.maxMemory()).thenReturn(Long.MAX_VALUE);
		MemoryManagerImpl toTest = new MemoryManagerImpl(rt, 1, 0.0);
		toTest.gcIfNecessary();
		assertTrue("Expecting negative count which means gc will not be programtically invoked", toTest.getGcCount() < 0);
	}
	
}