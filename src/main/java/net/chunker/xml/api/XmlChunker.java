package net.chunker.xml.api;

import org.xml.sax.helpers.DefaultHandler;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public abstract class XmlChunker extends DefaultHandler {

	public abstract void finish();

	public abstract void setException(Exception e);

}