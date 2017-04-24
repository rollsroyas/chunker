package net.chunker.json.event;

import com.chunker.BasePojomaticTest;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedTrueEventTest extends BasePojomaticTest<NamedTrueEvent> {

	/**
	 * @see BasePojomaticTest#construct()
	 */
	@Override
	public NamedTrueEvent construct() {
		return new NamedTrueEvent("name");
	}

	@Override
	public String expectedToString() {
		return "NamedTrueEvent{name: {name}, bool: {true}}";
	}
}
