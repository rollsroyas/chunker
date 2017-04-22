package net.chunker.json.event;

import com.chunker.BasePojomaticTest;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedStartObjectEventTest extends BasePojomaticTest<NamedStartObjectEvent> {

	/**
	 * @see BasePojomaticTest#construct()
	 */
	@Override
	public NamedStartObjectEvent construct() {
		return new NamedStartObjectEvent("name");
	}

	@Override
	public String expectedToString() {
		return "NamedStartObjectEvent{name: {name}}";
	}

}
