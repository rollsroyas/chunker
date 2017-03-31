package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedStartObjectEvent extends NamedEvent {
	
	public NamedStartObjectEvent(String name) {
		super(name);
	}
	
	@Override
	public boolean isStart() {
		return true;
	}

	@Override
	public void applyTo(JsonGenerator generator) {
		if (name != null) {
			generator.writeStartObject(name);
		} else {
			generator.writeStartObject();
		}
	}

}
