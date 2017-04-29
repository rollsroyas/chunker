package net.chunker.json.impl;

import java.math.BigDecimal;

import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;

import com.chunker.BasePojomaticTest;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class JsonEventImplTest extends BasePojomaticTest<JsonEventImpl> {

	private static final JsonParser PARSER = new JsonParser() {

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
		public Event next() {
			return Event.KEY_NAME;
		}

		@Override
		public BigDecimal getBigDecimal() {
			return null;
		}

		@Override
		public String toString() {
			return "event";
		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public long getLong() {
			return 0;
		}

		@Override
		public JsonLocation getLocation() {
			return null;
		}

		@Override
		public void close() {
		}
	};

	/**
	 * @see BasePojomaticTest#construct()
	 */
	@Override
	protected JsonEventImpl construct() {
		return new JsonEventImpl(PARSER);
	}

	@Override
	protected String expectedToString() {
		return "JsonEventImpl{event: {KEY_NAME}, string: {string}, number: {null}, integer: {0}, integralNumber: {false}}";
	}

}
