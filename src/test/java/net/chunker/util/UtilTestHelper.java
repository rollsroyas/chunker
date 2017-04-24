package net.chunker.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Assert;

public final class UtilTestHelper {

	private UtilTestHelper() {}

	public static void invokeAndAssertPrivateConstuctor(Class<?> clazz)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<?> constructor = clazz.getDeclaredConstructor(new Class[0]);
		constructor.setAccessible(true);
	    constructor.newInstance(new Object[0]);
	    Assert.assertTrue("Constructor should be private", Modifier.isPrivate(constructor.getModifiers()));
	}

}
