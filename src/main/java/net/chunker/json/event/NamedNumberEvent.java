package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

import net.chunker.json.api.JsonEvent;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedNumberEvent extends NamedEvent {
	
	private final JsonEvent event;
	
	public NamedNumberEvent(String name, JsonEvent event) {
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

}
