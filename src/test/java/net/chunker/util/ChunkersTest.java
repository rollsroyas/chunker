package net.chunker.util;

import static net.chunker.util.UtilTestHelper.invokeAndAssertPrivateConstuctor;
import static org.junit.Assert.assertNull;

import java.util.concurrent.Callable;

import org.junit.Test;

public class ChunkersTest {

	@Test
	public void constructor_Private() throws Exception {
		invokeAndAssertPrivateConstuctor(Chunkers.class);
	}
	
	@Test
	public void createFinalCallable_Happy() throws Exception {
		Callable<Object> finalCallable = Chunkers.createFinalCallable(null);
		Object object = finalCallable.call();
		assertNull("Expected final callable to always return null", object);
	}

	@Test(expected=IllegalStateException.class)
	public void createFinalCallable_Exception() throws Exception {
		Callable<Object> finalCallable = Chunkers.createFinalCallable(new IllegalStateException("Expecting this test to throw this exception"));
		finalCallable.call();
	}	
}
