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
	protected NamedEndEvent construct() {
		return new NamedEndEvent();
	}

	/**
	 * @see BasePojomaticTest#expectedToString()
	 */
	@Override
	protected String expectedToString() {
		return "NamedEndEvent{name: {null}}";
	}
}
