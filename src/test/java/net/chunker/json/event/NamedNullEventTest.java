package net.chunker.json.event;

import com.chunker.BasePojomaticTest;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedNullEventTest extends BasePojomaticTest<NamedNullEvent> {

	/**
	 * @see BasePojomaticTest#construct()
	 */
	@Override
	public  NamedNullEvent construct() {
		return new  NamedNullEvent("name");
	}
	
	@Override
	public String expectedToString() {
		return "NamedNullEvent{name: {name}}";
	}
}
