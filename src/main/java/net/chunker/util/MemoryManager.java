package net.chunker.util;

/**
 * This may not be necessary in the newer JREs, but back in Java 5, especially
 * the IBM Impl, chunking documents would create a lot of garbage and OoMEs
 * would happen before the GC ran.
 * 
 * @author rollsroyas@alumni.ncsu.edu
 */
public interface MemoryManager {

	public abstract void gcIfNecessary();

}