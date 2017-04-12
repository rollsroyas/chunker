package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
@AutoProperty
public class NamedEndEvent extends NamedEvent {

	protected NamedEndEvent() {
		super(null);
	}

	@Override
	public void applyTo(JsonGenerator generator) {
		generator.writeEnd();
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
