package net.chunker.json.event;

import javax.json.stream.JsonParser.Event;

import net.chunker.json.api.JsonEvent;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public final class NamedEventFactory {

	private static volatile NamedEventFactory instance;

	private NamedEventFactory() {
	}

	public static NamedEventFactory namedEventFactory() {
		if (instance == null) {
			synchronized (NamedEventFactory.class) {
				if (instance == null) {
					instance = new NamedEventFactory();
				}
			}
		}
		return instance;
	}

	/**
	 * All events are handled accept KEY_NAME, b/c that is where the currentName
	 * comes from
	 * 
	 * @param jsonEvent
	 *            Any event accept KEY_NAME
	 * @param currentName
	 *            Nullable, b/c arrays contain unnamed values
	 * @return NamedEvent
	 */
	public NamedEvent create(JsonEvent jsonEvent, String currentName) {
		Event event = jsonEvent.getEvent();
		switch (event) {
		case START_ARRAY:
			return new NamedStartArrayEvent(currentName);
		case START_OBJECT:
			return new NamedStartObjectEvent(currentName);
		case END_ARRAY:
		case END_OBJECT:
			return new NamedEndEvent();
		case VALUE_FALSE:
			return new NamedBooleanEvent(currentName, false);
		case VALUE_TRUE:
			return new NamedBooleanEvent(currentName, true);
		case VALUE_NULL:
			return new NamedNullEvent(currentName);
		case VALUE_NUMBER:
			return new NamedNumberEvent(currentName, jsonEvent);
		case VALUE_STRING:
			return new NamedStringEvent(currentName, jsonEvent);
		default:
			throw new IllegalStateException(event.name());
		}
	}
}