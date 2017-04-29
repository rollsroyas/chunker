package net.chunker.json.event;

import static org.mockito.MockitoAnnotations.initMocks;

import javax.json.stream.JsonGenerator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.chunker.BasePojomaticTest;


/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public abstract class BaseNamedEventTest<T extends NamedEvent> extends BasePojomaticTest<T> {

	@Mock
	protected JsonGenerator generator;
	
	@Before
	public void before() {
		initMocks(this);
	}
	
	protected abstract T construct_NameNotNull();
	
	protected abstract T construct_NameNull();
	
	/**
	 * @see BasePojomaticTest#construct()
	 */
	@Override
	protected T construct() {
		return construct_NameNotNull();
	}
	
	@Test
	public void applyTo_NameNull() {
		construct_NameNotNull().applyTo(generator);
		verifyApplyTo_NameNotNull();
	}

	protected abstract void verifyApplyTo_NameNotNull();
	
	@Test
	public void applyTo_NameNotNull() {
		construct_NameNull().applyTo(generator);
		verifyApplyTo_NameNull();		
	}
	
	protected abstract void verifyApplyTo_NameNull();
	
	
}
