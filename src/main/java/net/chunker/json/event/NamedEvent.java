package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */

public abstract class NamedEvent {
	
	protected final String name;
	
	protected NamedEvent(String name) {
		this.name = name;
	}
	
	public abstract void applyTo(JsonGenerator generator);
	
	public boolean isStart() {
		return false;
	}

}
