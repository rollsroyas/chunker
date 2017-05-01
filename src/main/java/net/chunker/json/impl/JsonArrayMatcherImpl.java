package net.chunker.json.impl;

import net.chunker.json.api.JsonArrayMatcher;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class JsonArrayMatcherImpl implements JsonArrayMatcher {

	private final String name;

	public JsonArrayMatcherImpl(String name) {
		this.name = name;
	}

	/**
	 * @see JsonArrayMatcher#acceptsName(String)
	 */
	@Override
	public boolean acceptsName(String name) {
		return this.name.equals(name);
	}

}
