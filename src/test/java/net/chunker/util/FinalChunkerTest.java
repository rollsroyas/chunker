package net.chunker.util;

import java.util.concurrent.Callable;

import com.chunker.BasePojomaticTest;

public class FinalChunkerTest extends BasePojomaticTest<Callable<Object>> {

	private static final Exception EXCEPTION = new Exception("Test");

	@Override
	protected Callable<Object> construct() {
		return Chunkers.<Object>createFinalCallable(EXCEPTION);
	}

	@Override
	protected String expectedToString() {
		return "FinalCallable{e: {java.lang.Exception: Test}}";
	}

}
