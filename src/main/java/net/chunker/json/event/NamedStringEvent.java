package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import net.chunker.json.api.JsonEvent;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
@AutoProperty
public class NamedStringEvent extends NamedEvent {
	protected final String string;

	protected NamedStringEvent(String name, JsonEvent event) {
		super(name);
		string = event.getString();
	}

	@Override
	public void applyTo(JsonGenerator generator) {
		if (name != null) {
			generator.write(name, string);
		} else {
			generator.write(string);
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
