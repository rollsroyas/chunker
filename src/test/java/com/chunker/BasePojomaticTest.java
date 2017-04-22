package com.chunker;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public abstract class BasePojomaticTest<T> {

	abstract public T construct();

	abstract public String expectedToString();

	@Test
	public void testEquals() {
		assertEquals(construct(), construct());
	}

	@Test
	public void testHashCode() {
		assertEquals(construct().hashCode(), construct().hashCode());
	}

	@Test
	public void testToString() {
		assertEquals(construct().toString(), expectedToString());
	}
}
