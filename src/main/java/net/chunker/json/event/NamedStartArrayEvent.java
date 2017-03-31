package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedStartArrayEvent extends NamedEvent {
	
	public NamedStartArrayEvent(String name) {
		super(name);
	}
	
	@Override
	public boolean isStart() {
		return true;
	}

	@Override
	public void applyTo(JsonGenerator generator) {
		if (name != null) {
			generator.writeStartArray(name);
		} else {
			generator.writeStartArray();
		}
	}

}
