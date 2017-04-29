package net.chunker.xml.api;

import org.xml.sax.helpers.DefaultHandler;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public abstract class XmlChunker extends DefaultHandler {

	/**
	 * Implementations of this class tend to get populated in the background,
	 * so this method must be invoked or one risks infinite wait. 
	 * Please call it from a finally block.
	 */
	public abstract void finish();

	/**
	 * @param e
	 *   An exception that occurred while chunking.  Setting it here with the expectation
	 *   that the implementation class will bubble it up to the calling code;
	 */
	public abstract void setException(Exception e);

}