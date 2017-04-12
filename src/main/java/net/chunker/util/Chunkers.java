package net.chunker.util;

import java.util.concurrent.Callable;

public final class Chunkers {
	
	private Chunkers() {}

	public static final <A> Callable<A> createFinalCallable(final Exception exception) {
		return new Callable<A>() {
				@Override
				public A call() throws Exception {
					Exception e = exception;
					if (e != null) {
						throw e;
					}
					return null;
				}
			};	
	}

}
