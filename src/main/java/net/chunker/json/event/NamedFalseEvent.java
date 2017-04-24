package net.chunker.json.event;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedFalseEvent extends NamedBooleanEvent {

	protected NamedFalseEvent(String name) {
		super(name, false);
	}
}
