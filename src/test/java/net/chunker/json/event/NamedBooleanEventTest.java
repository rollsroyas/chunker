package net.chunker.json.event;

import com.chunker.BasePojomaticTest;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedBooleanEventTest extends BasePojomaticTest<NamedBooleanEvent> {

	/**
	 * @see BasePojomaticTest#construct()
	 */
	@Override
	public NamedBooleanEvent construct() {
		return new NamedBooleanEvent("name", false);
	}

	@Override
	public String expectedToString() {
		return "NamedBooleanEvent{name: {name}, bool: {false}}";
	}
}
