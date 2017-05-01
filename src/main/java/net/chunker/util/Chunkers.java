package net.chunker.util;

import java.util.concurrent.Callable;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

public final class Chunkers {

	@AutoProperty
	static class FinalCallable<A> implements Callable<A> {
		
		private Exception e;
		
		public FinalCallable(Exception  exception) {
			e = exception;
		}
		
		@Override
		public A call() throws Exception {
			if (e != null) {
				throw e;
			}
			return null;
		}
		
		@Override
		public boolean equals(Object o) {
			return Pojomatic.equals(this, o);
		}

		@Override
		public int hashCode() {
			return Pojomatic.hashCode(this);
		}

		@Override
		public String toString() {
			return Pojomatic.toString(this);
		}
	}
	
	private Chunkers() {
	}

	/**
	 * Code that is reused between XML and JSON implementations
	 * @param exception
	 * 			If the exception is not null, then the returned {@link Callable} will throw it,
	 * 			otherwise it will return null.
	 * @return {@link Callable}
	 */
	public static final <A> Callable<A> createFinalCallable(final Exception exception) {
		return new FinalCallable<A>(exception);
	}

}
