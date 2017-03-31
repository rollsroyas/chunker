package net.chunker.xml.api;


/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public interface XmlElementMatcher {
	
	/**
	 * @param namespaceURI This value will be null if there is no namespace for the element attempting to be matched
	 * @param name The name for the element attempting to be matched
	 * @return true if this is the repeated element to be chunked, otherwise false
	 */
	boolean acceptName(String namespaceURI, String name);
}
