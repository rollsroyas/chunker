package net.chunker.json.event;

import javax.json.stream.JsonParser.Event;

import net.chunker.json.api.JsonEvent;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public final class NamedEventFactory {
	
	private volatile static NamedEventFactory instance;

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
	
	private NamedEventFactory() {}

	/**
	 * All events are handled accept KEY_NAME, b/c that is where the currentName comes from
	 * @param jsonEvent
	 * @param currentName Nullable
	 * @return
	 */
	public NamedEvent create(JsonEvent jsonEvent, String currentName) {
		Event event = jsonEvent.getEvent();
		switch (event) {
			case START_ARRAY:
				return new NamedStartArrayEvent(currentName);
			case END_ARRAY:
				return new NamedEndArrayEvent();
			case START_OBJECT:					
				return new NamedStartObjectEvent(currentName);
			case END_OBJECT:
				return new NamedEndObjectEvent();
			case VALUE_FALSE:
				return new NamedFalseEvent(currentName);
			case VALUE_TRUE:
				return new NamedTrueEvent(currentName);
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