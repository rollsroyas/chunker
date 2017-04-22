package net.chunker.json.impl;

import java.math.BigDecimal;

import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import net.chunker.json.api.JsonEvent;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
@AutoProperty
public class JsonEventImpl implements JsonEvent {

	private final Event event;
	private final String string;
	private final BigDecimal number;
	private final int integer;
	private final boolean integralNumber;

	public JsonEventImpl(JsonParser parser) {
		this.event = parser.next();
		if (event == Event.KEY_NAME || event == Event.VALUE_STRING) {
			this.string = parser.getString();
			this.number = null;
			this.integer = 0;
			this.integralNumber = false;
		} else if (event == Event.VALUE_NUMBER) {
			this.string = parser.getString();
			if (parser.isIntegralNumber()) {
				this.integer = parser.getInt();
				this.number = null;
				this.integralNumber = true;
			} else {
				this.integer = 0;
				this.number = parser.getBigDecimal();
				this.integralNumber = false;
			}
		} else {
			this.string = null;
			this.number = null;
			this.integer = 0;
			this.integralNumber = false;
		}
	}

	/**
	 * @see net.chunker.json.api.JsonEvent#getEvent()
	 */
	@Override
	public Event getEvent() {
		return event;
	}

	/**
	 * @see net.chunker.json.api.JsonEvent#getString()
	 */
	@Override
	public String getString() {
		return string;
	}

	/**
	 * @see net.chunker.json.api.JsonEvent#getBigDecimal()
	 */
	@Override
	public BigDecimal getBigDecimal() {
		return number;
	}

	/**
	 * @see net.chunker.json.api.JsonEvent#getInt()
	 */
	@Override
	public int getInt() {
		return integer;
	}

	/**
	 * @see net.chunker.json.api.JsonEvent#isIntegralNumber()
	 */
	@Override
	public boolean isIntegralNumber() {
		return integralNumber;
	}

	@Override
	public boolean equals(Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

}
