package net.chunker.util;

import static net.chunker.util.UtilTestHelper.invokeAndAssertPrivateConstuctor;
import static net.chunker.util.Validations.checkNotBothNull;
import static net.chunker.util.Validations.checkNotNull;
import static net.chunker.util.Validations.checkNotBothPresent;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ValidationsTest {

	@Test
	public void constructor_Private() throws Exception {
		invokeAndAssertPrivateConstuctor(Validations.class);
	}
	
	@Test
	public void checkNotNull_Null() {
		try { 
			checkNotNull(null, "message");
		} catch(NullPointerException npe) {
			assertEquals("Unexpected NPE message", "message", npe.getMessage());
		}
	}
	@Test
	public void checkNotNull_Present() {
		checkNotNull(new Object(), "message");
	}
	
	@Test
	public void checkNotBothNull_NullNull() {
		try { 
			checkNotBothNull(null, null, "message");
		} catch(NullPointerException npe) {
			assertEquals("Unexpected NPE message", "message", npe.getMessage());
		}
	}
	@Test
	public void checkNotBothNull_NullPresent() {
		checkNotBothNull(null, new Object(), "message");
	}
	@Test
	public void checkNotBothNull_PresentNull() {
		checkNotBothNull(new Object(), null, "message");
	}
	@Test
	public void checkNotBothNull_PresentPresent() {
		checkNotBothNull(new Object(), new Object(), "message");
	}
	
	@Test
	public void checkNotBothPresent_NullNull() {
		checkNotBothPresent(null, null, "message");
	}
	@Test
	public void checkNotBothPresent_NullPresent() {
		checkNotBothNull(null, new Object(), "message");
	}
	@Test
	public void checkNotBothPresent_PresentNull() {
		checkNotBothNull(new Object(), null, "message");
	}
	@Test
	public void checkNotBothPresent_PresentPresent() {
		try {
			checkNotBothNull(new Object(), new Object(), "message");
		} catch(IllegalStateException ise) {
			assertEquals("Unexpected NPE message", "message", ise.getMessage());
		}
	}

}
