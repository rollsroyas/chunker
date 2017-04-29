package net.chunker.json.event;

import static org.mockito.Mockito.verify;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedStartObjectEventTest extends BaseNamedEventTest<NamedStartObjectEvent> {

	@Override
	protected String expectedToString() {
		return "NamedStartObjectEvent{name: {name}}";
	}

	/**
	 * @see BaseNamedEventTest#construct_NameNotNull()
	 */
	@Override
	protected NamedStartObjectEvent construct_NameNotNull() {
		return new NamedStartObjectEvent("name");
	}

	/**
	 * @see BaseNamedEventTest#construct_NameNull()
	 */
	@Override
	protected NamedStartObjectEvent construct_NameNull() {
		return new NamedStartObjectEvent(null);
	}

	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNotNull()
	 */
	@Override
	protected
	void verifyApplyTo_NameNotNull() {
		verify(generator).writeStartObject("name");
	}

	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNull()
	 */
	@Override
	protected
	void verifyApplyTo_NameNull() {
		verify(generator).writeStartObject();
	}

}
