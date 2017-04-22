package net.chunker.json.api;

import java.math.BigDecimal;

import javax.json.stream.JsonParser.Event;

/**
 * JsonParser is mutable. The intent of this interface is to provide an
 * immutable object capturing JsonParser's state at the point in time the
 * implementation is instantiated
 * 
 * @author rollsroyas@alumni.ncsu.edu
 */
public interface JsonEvent {

	Event getEvent();

	String getString();

	BigDecimal getBigDecimal();

	int getInt();

	boolean isIntegralNumber();
}