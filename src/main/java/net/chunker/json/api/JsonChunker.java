package net.chunker.json.api;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public interface JsonChunker {
	void handleEvent(JsonEvent event);
	void setException(Exception e);
	void finish();
}
