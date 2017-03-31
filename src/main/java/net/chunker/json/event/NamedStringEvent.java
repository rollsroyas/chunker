package net.chunker.json.event;

import javax.json.stream.JsonGenerator;

import net.chunker.json.api.JsonEvent;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedStringEvent extends NamedEvent {
	protected final String string;
	
	public NamedStringEvent(String name, JsonEvent event) {
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

}
