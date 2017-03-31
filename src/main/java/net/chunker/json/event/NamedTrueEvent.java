package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedTrueEvent extends NamedEvent {
	
	public NamedTrueEvent(String name) {
		super(name);
	}

	@Override
	public void applyTo(JsonGenerator generator) {
		if (name != null) {
			generator.write(name, true);
		} else {
			generator.write(true);
		}		
	}

}
