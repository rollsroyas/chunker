package net.chunker.json.event;

import static org.mockito.Mockito.verify;

import com.chunker.BasePojomaticTest;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedStartArrayEventTest extends BaseNamedEventTest<NamedStartArrayEvent> {

	/**
	 * @see BasePojomaticTest#expectedToString()
	 */
	@Override
	protected String expectedToString() {
		return "NamedStartArrayEvent{name: {name}}";
	}

	/**
	 * @see BaseNamedEventTest#construct_NameNotNull()
	 */
	@Override
	protected NamedStartArrayEvent construct_NameNotNull() {
		return new NamedStartArrayEvent("name");
	}

	/**
	 * @see BaseNamedEventTest#construct_NameNull()
	 */
	@Override
	protected NamedStartArrayEvent construct_NameNull() {
		return new NamedStartArrayEvent(null);
	}

	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNotNull()
	 */
	@Override
	protected
	void verifyApplyTo_NameNotNull() {
		verify(generator).writeStartArray("name");
	}

	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNull()
	 */
	@Override
	protected
	void verifyApplyTo_NameNull() {
		verify(generator).writeStartArray();
	}

}
