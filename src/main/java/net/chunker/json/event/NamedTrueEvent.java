package net.chunker.json.event;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class NamedTrueEvent extends NamedBooleanEvent {

	protected NamedTrueEvent(String name) {
		super(name, true);
	}
}
