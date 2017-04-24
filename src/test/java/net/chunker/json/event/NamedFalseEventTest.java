package net.chunker.json.event;

import com.chunker.BasePojomaticTest;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedFalseEventTest extends BasePojomaticTest<NamedFalseEvent> {

	/**
	 * @see BasePojomaticTest#construct()
	 */
	@Override
	public NamedFalseEvent construct() {
		return new NamedFalseEvent("name");
	}

	@Override
	public String expectedToString() {
		return "NamedFalseEvent{name: {name}, bool: {false}}";
	}
}
