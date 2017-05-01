package net.chunker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class MemoryManagerImpl implements MemoryManager {

	private static Logger LOG = LoggerFactory.getLogger(MemoryManagerImpl.class);

	private int gcCount;
	private long maxMemory;
	private double memoryTolerance;
	private long numberOfCallsSinceToleranceCheck;
	private final int numberOfCallsBetweenToleranceChecks;
	private final Runtime rt;

	/**
	 * @param numberOfCallsBetweenToleranceChecks
	 * 			To increase throughput, then make this number larger.
	 * 			However bigger the number the greater the risk of an {@link OutOfMemoryError}.
	 * @param memoryTolerance
	 * 			Expects a value between 0 and 1, which represents the percentage 
	 * 			of maxMemory that must be allocated before this instance
	 * 			will do the {@link Runtime} combo gc(), runFinalization(), gc()
	 */
	public MemoryManagerImpl(final int numberOfCallsBetweenToleranceChecks, final double memoryTolerance) {
		this(Runtime.getRuntime(), numberOfCallsBetweenToleranceChecks, memoryTolerance);
	}
	
	MemoryManagerImpl(final Runtime rt, final int numberOfCallsBetweenToleranceChecks, final double memoryTolerance) {
		this.rt = rt;
		this.maxMemory = rt.maxMemory();
		if (this.maxMemory == Long.MAX_VALUE) {
			this.gcCount = Integer.MIN_VALUE;
		}
		this.memoryTolerance = memoryTolerance;
		this.numberOfCallsBetweenToleranceChecks = numberOfCallsBetweenToleranceChecks;
		this.numberOfCallsSinceToleranceCheck = 0;
	}

	/**
	 * @return int The number of times this instance invoked the Runtime combo gc(), runFinalization(), gc().
	 */
	public int getGcCount() {
		return gcCount;
	}

	/**
	 * Made method final in hopes that it will get inlined
	 */
	private final boolean isMemoryUsageHigh(Runtime rt) {
		return (rt.totalMemory() - rt.freeMemory()) / (double) this.maxMemory > this.memoryTolerance;
	}

	/**
	 * @see net.chunker.util.MemoryManager#gcIfNecessary()
	 */
	@Override
	public void gcIfNecessary() {
		this.numberOfCallsSinceToleranceCheck++;
		if (this.numberOfCallsSinceToleranceCheck > this.numberOfCallsBetweenToleranceChecks) {
			this.numberOfCallsSinceToleranceCheck = 0;

			if (isMemoryUsageHigh(rt) && this.gcCount >= 0) {
				synchronized (rt) {
					// double check locking, b/c it's good enough
					if (isMemoryUsageHigh(rt) && this.gcCount >= 0) {
						gc();
					}
				}
			}
		}
	}

	private void gc() {
		final long beforeUsedMemory = rt.totalMemory() - rt.freeMemory();
		this.gcCount++;
		rt.gc();
		rt.runFinalization();
		rt.gc();
		final long afterUsedMemory = rt.totalMemory() - rt.freeMemory();

		if (LOG.isTraceEnabled()) {
			
			LOG.trace(	"After ChunkTransformer called gc()\tUsed:\t{}\tMax:\t{}",
						formatLong(afterUsedMemory), formatLong(this.maxMemory));
		}

		// if that does not cause the memory to get released,
		// then don't bother doing it again
		if (afterUsedMemory >= beforeUsedMemory) {
			this.gcCount = -this.gcCount;
		}
	}

	static final String formatLong(final long l) {
		String s = Long.toString(Math.abs(l));
		final int sLength = s.length();

		StringBuilder sb = new StringBuilder(sLength + (sLength / 3) + (l < 0 ? 1 : 0));

		sb.append(s);

		if (sLength > 3) {
			int start = sLength % 3;
			if (start == 0) {
				start = 3;
			}
			for (int i = start; i <= s.length(); i += 4) {
				sb.insert(i, ',');
			}
		}
		if (l < 0) {
			sb.insert(0, '-');
		}
		return sb.toString();
	}
}