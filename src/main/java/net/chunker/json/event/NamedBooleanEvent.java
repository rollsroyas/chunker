package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
@AutoProperty
public abstract class NamedBooleanEvent extends NamedEvent {

	final boolean bool;

	protected NamedBooleanEvent(String name, boolean bool) {
		super(name);
		this.bool = bool;
	}

	@Override
	public void applyTo(JsonGenerator generator) {
		if (name != null) {
			generator.write(name, bool);
		} else {
			generator.write(bool);
		}
	}

	@Override
	public boolean equals(Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

}
