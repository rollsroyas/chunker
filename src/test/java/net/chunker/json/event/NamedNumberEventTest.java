package net.chunker.json.event;

import java.math.BigDecimal;

import javax.json.stream.JsonParser.Event;

import com.chunker.BasePojomaticTest;

import net.chunker.json.api.JsonEvent;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedNumberEventTest extends BasePojomaticTest<NamedNumberEvent> {

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
			return null;
		}

		@Override
		public String toString() {
			return "event";
		}
	};

	/**
	 * @see BasePojomaticTest#construct()
	 */
	@Override
	public NamedNumberEvent construct() {
		return new NamedNumberEvent("name", EVENT);
	}

	@Override
	public String expectedToString() {
		return "NamedNumberEvent{name: {name}, event: {event}}";
	}

}
