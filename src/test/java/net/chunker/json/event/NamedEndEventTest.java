package net.chunker.json.event;

import com.chunker.BasePojomaticTest;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedEndEventTest extends BasePojomaticTest<NamedEndEvent> {

	/**
	 * @see BasePojomaticTest#construct()
	 */
	@Override
	public NamedEndEvent construct() {
		return new NamedEndEvent();
	}

	@Override
	public String expectedToString() {
		return "NamedEndEvent{name: {null}}";
	}
}
