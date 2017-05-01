package net.chunker.xml.impl;

import static net.chunker.util.Validations.checkNotNull;

import net.chunker.xml.api.XmlElementMatcher;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class XmlElementMatcherImpl implements XmlElementMatcher {

	private final String namespaceURI;
	private final String name;

	public XmlElementMatcherImpl(String namespaceURI, String name) {
		this.namespaceURI = namespaceURI;
		this.name = checkNotNull(name, "name cannot be null");
	}

	public XmlElementMatcherImpl(String name) {
		this(null, name);
	}

	/**
	 * @see XmlElementMatcher#acceptsName(String, String)
	 */
	@Override
	public boolean acceptsName(String namespaceURI, String name) {
		return this.name.equals(name) && (this.namespaceURI == null || this.namespaceURI.equals(namespaceURI));
	}
}
