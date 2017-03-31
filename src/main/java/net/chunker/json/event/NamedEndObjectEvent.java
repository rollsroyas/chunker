package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedEndObjectEvent extends NamedEvent {
	
	public NamedEndObjectEvent() {
		super(null);
	}

	@Override
	public void applyTo(JsonGenerator generator) {
		generator.writeEnd();
	}

}
