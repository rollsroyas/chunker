package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
@AutoProperty
public class NamedStartObjectEvent extends NamedEvent {

	protected NamedStartObjectEvent(String name) {
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
