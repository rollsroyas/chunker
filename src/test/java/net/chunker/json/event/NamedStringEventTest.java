package net.chunker.json.event;

import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import javax.json.stream.JsonParser.Event;

import net.chunker.json.api.JsonEvent;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedStringEventTest extends BaseNamedEventTest<NamedStringEvent> {

	private static final JsonEvent EVENT = new JsonEvent() {

		@Override
		public boolean isIntegralNumber() {
			return false;
		}

		@Override
		public String getString() {
			return "string";
		}

		@Override
		public int getInt() {
			return 0;
		}

		@Override
		public Event getEvent() {
			return null;
		}

		@Override
		public BigDecimal getBigDecimal() {
			return null;
		}

		@Override
		public String toString() {
			return "event";
		}
	};

	@Override
	protected String expectedToString() {
		return "NamedStringEvent{name: {name}, string: {string}}";
	}
	
	/**
	 * @see BaseNamedEventTest#construct_NameNotNull()
	 */
	@Override
	protected NamedStringEvent construct_NameNotNull() {
		return new NamedStringEvent("name", EVENT);
	}

	/**
	 * @see BaseNamedEventTest#construct_NameNull()
	 */
	@Override
	protected NamedStringEvent construct_NameNull() {
		return new NamedStringEvent(null, EVENT);
	}

	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNotNull()
	 */
	@Override
	protected
	void verifyApplyTo_NameNotNull() {
		verify(generator).write("name", EVENT.getString());
	}

	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNull()
	 */
	@Override
	protected
	void verifyApplyTo_NameNull() {
		verify(generator).write(EVENT.getString());
	}

}
