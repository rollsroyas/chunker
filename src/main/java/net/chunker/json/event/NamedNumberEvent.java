package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import net.chunker.json.api.JsonEvent;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
@AutoProperty
public class NamedNumberEvent extends NamedEvent {

	private final JsonEvent event;

	protected NamedNumberEvent(String name, JsonEvent event) {
		super(name);
		this.event = event;
	}

	@Override
	public void applyTo(JsonGenerator generator) {

		if (name != null) {
			if (event.isIntegralNumber()) {
				generator.write(name, event.getInt());
			} else {
				generator.write(name, event.getBigDecimal());
			}
		} else {
			if (event.isIntegralNumber()) {
				generator.write(event.getInt());
			} else {
				generator.write(event.getBigDecimal());
			}
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
