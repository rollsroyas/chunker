package net.chunker.json.event;

import com.chunker.BasePojomaticTest;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedStartArrayEventTest extends BasePojomaticTest<NamedStartArrayEvent> {
	
	/**
	 * @see BasePojomaticTest#construct()
	 */
	@Override
	public NamedStartArrayEvent construct() {
		return new NamedStartArrayEvent("name");
	}
	
	@Override
	public String expectedToString() {
		return "NamedStartArrayEvent{name: {name}}";
	}

}
