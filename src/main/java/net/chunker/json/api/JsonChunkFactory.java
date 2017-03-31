package net.chunker.json.api;

import java.util.concurrent.Callable;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public interface JsonChunkFactory<A> {
	
	/**
	 * Convert a chunk of JSON into a POJO
	 * @param chunk JSON
	 * @param last This enables special processing for the last chunk when needed
	 * @return Invoke this {@link Callable} to get the POJO
	 */
	Callable<A> create(String chunk, boolean last);
}
