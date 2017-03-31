package net.chunker.xml.impl;

import net.chunker.xml.api.XmlElementMatcher;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class XmlElementMatcherImpl implements XmlElementMatcher {

	private final String namespaceURI;
	private final String name;
	
	public XmlElementMatcherImpl(String namespaceURI, String name) {
		this.namespaceURI = namespaceURI;
		this.name = name;
	}
	
	public XmlElementMatcherImpl(String name) {
		this(null, name);
	}
	
	/**
	 * @see XmlElementMatcher#acceptName(String, String)
	 */
	@Override
	public boolean acceptName(String namespaceURI, String name) {
		return this.name.equals(name) && 
				(this.namespaceURI == null || this.namespaceURI.equals(namespaceURI));
	}
}
