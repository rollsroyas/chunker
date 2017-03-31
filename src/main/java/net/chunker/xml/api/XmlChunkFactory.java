package net.chunker.xml.api;

import java.util.concurrent.Callable;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public interface XmlChunkFactory<A> {
	
	/**
	 * Convert a chunk of XML into a POJO
	 * @param chunk XML
	 * @param last This enables special processing for the last chunk when needed
	 * @return Invoke this {@link Callable} to get the POJO
	 */
	Callable<A> create(String chunk, boolean last);
}
