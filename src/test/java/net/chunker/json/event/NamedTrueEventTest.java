package net.chunker.json.event;

import static org.mockito.Mockito.verify;

import com.chunker.BasePojomaticTest;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedTrueEventTest extends BaseNamedEventTest<NamedTrueEvent> {

	/**
	 * @see BasePojomaticTest#construct()
	 */
	@Override
	protected NamedTrueEvent construct() {
		return new NamedTrueEvent("name");
	}

	@Override
	protected String expectedToString() {
		return "NamedTrueEvent{name: {name}, bool: {true}}";
	}
	
	/**
	 * @see BaseNamedEventTest#construct_NameNull()
	 */
	@Override
	protected NamedTrueEvent construct_NameNull() {
		return new NamedTrueEvent(null);
	}
	
	/**
	 * @see BaseNamedEventTest#construct_NameNotNull()
	 */
	@Override
	protected NamedTrueEvent construct_NameNotNull() {
		return new NamedTrueEvent("name");
	}

	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNull()
	 */
	@Override
	protected void verifyApplyTo_NameNull() {
		verify(generator).write(true);
	}
	
	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNotNull()
	 */
	@Override
	protected void verifyApplyTo_NameNotNull() {
		verify(generator).write("name", true);
	}

}
