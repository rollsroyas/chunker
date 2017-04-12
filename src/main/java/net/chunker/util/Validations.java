package net.chunker.util;

public final class Validations {

	private Validations() {}

	/**
	 * Based on quava Preconditions.checkNotNull, but did not want to add a dependency
	 * 
	 * @param object Object to check for null
	 * @param message Message for the potential NullPointerException
	 * @return Passed in non-null object
	 * @throws NullPointerException with given message if given object is null
	 */
	public static <T> T checkNotNull(T object, String message) {
		if (object == null) {
			throw new NullPointerException(message);
		}
		return object;
	}
	
	/**
	 * @param object1 Object to check for null
	 * @param object2 Object to check for null
	 * @param message Message for the potential NullPointerException
	 * @throws NullPointerException with given message if both given objects are null
	 */
	public static void checkNotBothNull(Object object1, Object object2, String message) {
		if (object1 == null && object2 == null) {
			throw new NullPointerException(message);
		}
	}
	
	/**
	 * @param object1 Object to check for presence
	 * @param object2 Object to check for presence
	 * @param message Message for the potential IllegalStateException
	 * @throws IllegalStateException with given message if both given objects are present
	 */
	public static void checkNotBothPresent(Object object1, Object object2, String message) {
		if (object1 != null && object2 != null) {
			throw new IllegalStateException(message);
		}
	}
}
