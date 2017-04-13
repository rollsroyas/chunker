package net.chunker.json.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JsonChunkingExceptionTest {

	@Test
	public void testConstructor() {
		String message = "message";
		Throwable cause = new NullPointerException();
		JsonChunkingException toTest = new JsonChunkingException(message, cause);
		assertEquals("message does not equal expected", message, toTest.getMessage());
		assertEquals("cause does not equal expected", cause, toTest.getCause());
	}

}
