package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedEndArrayEvent extends NamedEvent {
	
	public NamedEndArrayEvent() {
		super(null);
	}

	@Override
	public void applyTo(JsonGenerator generator) {
		generator.writeEnd();
	}

}
