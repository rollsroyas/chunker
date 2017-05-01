package net.chunker.xml.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class XmlElementMatcherImplTest {

	private static final String NAMESPACE_URI = "NAMESPACE_URI";
	private static final String NAME = "NAME";
	private static final String NOT_NAMESPACE_URI = "NOT_NAMESPACE_URI";
	private static final String NOT_NAME = "NOT_NAME";
	
	private XmlElementMatcherImpl toTest;

	@Test(expected=NullPointerException.class)
	public void acceptName_ConstructWithNullName() {
		toTest = new XmlElementMatcherImpl(null);
	}
	
	@Test
	public void acceptName_NsNull_NsNull_NameTrue() {
		toTest = new XmlElementMatcherImpl(NAME);
		assertTrue(toTest.acceptsName(null, NAME));
	}
	
	@Test
	public void acceptName_NsNull_NsNull_NameFalse() {
		toTest = new XmlElementMatcherImpl(NAME);
		assertFalse(toTest.acceptsName(null, NOT_NAME));
	}
	
	@Test
	public void acceptName_NsNull_NsNotNull_NameTrue() {
		toTest = new XmlElementMatcherImpl(NAME);
		assertTrue(toTest.acceptsName(NOT_NAMESPACE_URI, NAME));
	}
	
	@Test
	public void acceptName_Null_NotNull_NameFalse() {
		toTest = new XmlElementMatcherImpl(NAME);
		assertFalse(toTest.acceptsName(NOT_NAMESPACE_URI, NOT_NAME));
	}
	
	@Test
	public void acceptName_NsNotNull_NsNull_NameTrue() {
		toTest = new XmlElementMatcherImpl(NAMESPACE_URI, NAME);
		assertFalse(toTest.acceptsName(null, NAME));
	}
	
	@Test
	public void acceptName_NsNotNull_NsNull_NameFalse() {
		toTest = new XmlElementMatcherImpl(NAMESPACE_URI, NAME);
		assertFalse(toTest.acceptsName(null, NOT_NAME));
	}
	
	@Test
	public void acceptName_NsNotNull_NsNotNull_NsTrue_NameTrue() {
		toTest = new XmlElementMatcherImpl(NAMESPACE_URI, NAME);
		assertTrue(toTest.acceptsName(NAMESPACE_URI, NAME));
	}
	
	@Test
	public void acceptName_NsNotNull_NsNotNull_NsTrue_NameFalse() {
		toTest = new XmlElementMatcherImpl(NAMESPACE_URI, NAME);
		assertFalse(toTest.acceptsName(NAMESPACE_URI, NOT_NAME));
	}
	
	@Test
	public void acceptName_NsNotNull_NsNotNull_NsFalse_NameTrue() {
		toTest = new XmlElementMatcherImpl(NAMESPACE_URI, NAME);
		assertFalse(toTest.acceptsName(NOT_NAMESPACE_URI, NAME));
	}
	
	@Test
	public void acceptName_NsNotNull_NsNotNull_NsFalse_NameFalse() {
		toTest = new XmlElementMatcherImpl(NAMESPACE_URI, NAME);
		assertFalse(toTest.acceptsName(NOT_NAMESPACE_URI, NOT_NAME));
	}
}
