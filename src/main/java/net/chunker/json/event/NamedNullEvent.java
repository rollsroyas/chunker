package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedNullEvent extends NamedEvent {
	
	public NamedNullEvent(String name) {
		super(name);
	}

	@Override
	public void applyTo(JsonGenerator generator) {
		if (name != null) {
			generator.writeNull(name);
		} else {
			generator.writeNull();
		}		
	}
}
