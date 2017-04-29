package net.chunker.json.event;

import static org.mockito.Mockito.verify;

import com.chunker.BasePojomaticTest;


/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedNullEventTest extends BaseNamedEventTest<NamedNullEvent> {

	/**
	 * @see BasePojomaticTest#expectedToString()
	 */
	@Override
	protected String expectedToString() {
		return "NamedNullEvent{name: {name}}";
	}
	
	/**
	 * @see BaseNamedEventTest#construct_NameNull()
	 */
	@Override
	protected NamedNullEvent construct_NameNull() {
		return new NamedNullEvent(null);
	}
	
	/**
	 * @see BaseNamedEventTest#construct_NameNotNull()
	 */
	@Override
	protected NamedNullEvent construct_NameNotNull() {
		return new NamedNullEvent("name");
	}

	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNull()
	 */
	protected void verifyApplyTo_NameNull() {
		verify(generator).writeNull();
	}
	
	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNotNull()
	 */
	protected void verifyApplyTo_NameNotNull() {
		verify(generator).writeNull("name");
	}
	
}
