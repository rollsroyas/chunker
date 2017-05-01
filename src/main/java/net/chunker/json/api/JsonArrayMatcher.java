package net.chunker.json.api;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public interface JsonArrayMatcher {

	/**
	 * @param name
	 *            If the array is not named then this parameter will be null
	 * @return true if this is the array to chunk, otherwise false
	 */
	boolean acceptsName(String name);
}
