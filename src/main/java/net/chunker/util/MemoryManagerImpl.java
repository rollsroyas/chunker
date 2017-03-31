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

	public MemoryManagerImpl(final int numberOfCallsBetweenToleranceChecks, final double memoryTolerance) {
		rt = Runtime.getRuntime();
		this.maxMemory = rt.maxMemory();
		if (this.maxMemory == Long.MAX_VALUE) {
			this.gcCount = Integer.MIN_VALUE;
		}
		this.memoryTolerance = memoryTolerance;
		this.numberOfCallsBetweenToleranceChecks = numberOfCallsBetweenToleranceChecks;
		this.numberOfCallsSinceToleranceCheck = 0;
	}
	
	/**
	 * Made method final in hopes that it will get inlined
	 */
	private final boolean isMemoryUsageHigh(Runtime rt) {
		return (rt.totalMemory()-rt.freeMemory())/(double)this.maxMemory > this.memoryTolerance;
	}
	
	/**
	 * @see org.chunker.util.MemoryManager#gcIfNecessary()
	 */
	@Override
	public void gcIfNecessary() {
		this.numberOfCallsSinceToleranceCheck++;
		if (this.numberOfCallsSinceToleranceCheck > this.numberOfCallsBetweenToleranceChecks) {
			this.numberOfCallsSinceToleranceCheck = 0;
	
			if (isMemoryUsageHigh(rt) && this.gcCount >= 0) {
				synchronized(rt) {
					// double check locking, b/c it's good enough
					if (isMemoryUsageHigh(rt) && this.gcCount >= 0) {
						this.gcCount++;
						rt.gc();
						rt.runFinalization();
						rt.gc();
						
						LOG.trace("After ChunkTransformer called gc()\tUsed:\t{}\tMax:\t{}", 
								formatLong(rt.totalMemory()-rt.freeMemory()),  
								formatLong(this.maxMemory));

						
						// if that does not cause the memory to get released,
						// then don't bother doing it again
						if (isMemoryUsageHigh(rt)) {
							this.gcCount = -this.gcCount;
						}
					}													
				}						 
			}
		}
	}
	
    private static final String formatLong(final long l) {
        String s = Long.toString(Math.abs(l));
        final int sLength = s.length();
        
        StringBuffer sb = new StringBuffer(sLength+(sLength/3)+(l<0 ? 1 : 0));
        
        sb.append(s);
        
        if (sLength > 3) {
            int start =  sLength % 3;
            if(start == 0) start = 3;
            for (int i = start; i <= s.length(); i += 4) {
                sb.insert(i, ',');
            }
        }
        if (l<0) sb.insert(0, '-');
        return sb.toString();
    }
}