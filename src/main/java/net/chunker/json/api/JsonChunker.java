package net.chunker.json.api;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public interface JsonChunker {
	/**
	 * Expecting this method to get invoked multiple times, until every event from the JSON input has
	 * been passed to this chunker.  Usually this method is invoked by a background thread.
	 * @param event
	 *   This event contains information needed to reconstruct a chunk of JSON
	 */
	void handleEvent(JsonEvent event);

	/**
	 * @param e
	 *   An exception that occurred while chunking.  Setting it here with the expectation
	 *   that the implementation class will bubble it up to the calling code;
	 */
	void setException(Exception e);

	/**
	 * Implementations of this class tend to get populated in the background,
	 * so this method must be invoked or one risks infinite wait. 
	 * Please call it from a finally block.
	 */
	void finish();
}
