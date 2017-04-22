package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

import org.pojomatic.annotations.AutoProperty;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
@AutoProperty
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
