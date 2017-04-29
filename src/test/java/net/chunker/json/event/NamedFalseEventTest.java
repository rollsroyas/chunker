package net.chunker.json.event;

import static org.mockito.Mockito.verify;

import com.chunker.BasePojomaticTest;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedFalseEventTest extends BaseNamedEventTest<NamedFalseEvent> {

	/**
	 * @see BasePojomaticTest#expectedToString()
	 */
	@Override
	protected String expectedToString() {
		return "NamedFalseEvent{name: {name}, bool: {false}}";
	}
	
	/**
	 * @see BaseNamedEventTest#construct_NameNull()
	 */
	@Override
	protected NamedFalseEvent construct_NameNull() {
		return new NamedFalseEvent(null);
	}
	
	/**
	 * @see BaseNamedEventTest#construct_NameNotNull()
	 */
	@Override
	protected NamedFalseEvent construct_NameNotNull() {
		return new NamedFalseEvent("name");
	}

	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNull()
	 */
	@Override
	protected void verifyApplyTo_NameNull() {
		verify(generator).write(false);
	}
	
	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNotNull()
	 */
	@Override
	protected void verifyApplyTo_NameNotNull() {
		verify(generator).write("name", false);
	}
}
