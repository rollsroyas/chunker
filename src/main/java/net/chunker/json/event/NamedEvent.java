package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
@AutoProperty
public abstract class NamedEvent {
	
	protected final String name;
	
	public NamedEvent(String name) {
		this.name = name;
	}
	
	public abstract void applyTo(JsonGenerator generator);
	
	public boolean isStart() {
		return false;
	}
	
	@Override public boolean equals(Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override public String toString() {
		return Pojomatic.toString(this);
	}
}
