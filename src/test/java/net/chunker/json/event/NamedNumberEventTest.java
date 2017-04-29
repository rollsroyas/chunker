package net.chunker.json.event;

import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import javax.json.stream.JsonParser.Event;

import com.chunker.BasePojomaticTest;

import net.chunker.json.api.JsonEvent;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedNumberEventTest extends BaseNamedEventTest<NamedNumberEvent> {

	private static final JsonEvent EVENT = new JsonEvent() {

		@Override
		public boolean isIntegralNumber() {
			return false;
		}

		@Override
		public String getString() {
			return null;
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
			return BigDecimal.ZERO;
		}

		@Override
		public String toString() {
			return "event";
		}
	};

	/**
	 * @see BasePojomaticTest#expectedToString()
	 */
	@Override
	protected String expectedToString() {
		return "NamedNumberEvent{name: {name}, event: {event}}";
	}

	/**
	 * @see BaseNamedEventTest#construct_NameNotNull()
	 */
	@Override
	protected NamedNumberEvent construct_NameNotNull() {
		return new NamedNumberEvent("name", EVENT);
	}

	/**
	 * @see BaseNamedEventTest#construct_NameNull()
	 */
	@Override
	protected NamedNumberEvent construct_NameNull() {
		return new NamedNumberEvent(null, EVENT);
	}

	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNotNull()
	 */
	@Override
	protected
	void verifyApplyTo_NameNotNull() {
		verify(generator).write("name", EVENT.getBigDecimal());
	}

	/**
	 * @see BaseNamedEventTest#verifyApplyTo_NameNull()
	 */
	@Override
	protected
	void verifyApplyTo_NameNull() {
		verify(generator).write( EVENT.getBigDecimal());
	}

}
