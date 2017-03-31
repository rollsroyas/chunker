package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedFalseEvent extends NamedEvent {
	
	public NamedFalseEvent(String name) {
		super(name);
	}

	@Override
	public void applyTo(JsonGenerator generator) {
		if (name != null) {
			generator.write(name, false);
		} else {
			generator.write(false);
		}		
	}

}
