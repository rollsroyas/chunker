package net.chunker.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Special Thanks to
 * <a>http://stackoverflow.com/questions/11659396/using-a-jprogressbar-with-
 * saxparser</a> A class that monitors the read progress of an input stream.
 * Note that this version does not have any of the AWT/Swing features from the
 * original
 *
 * @author Hermia Yeung "Sheepy"
 * @author rollsroyas@alumni.ncsu.edu
 * @since 2012-04-05 18:42
 */
public class MonitoredInputStream extends FilterInputStream {
	private static final int END_OF_STREAM = -1;

	private volatile long mark;
	private volatile long location;

	/**
	 * Creates a MonitoredInputStream over an underlying input stream. Default
	 * threshold is 16KB, small threshold may impact performance impact on
	 * larger streams.
	 * 
	 * @param in
	 *            Underlying input stream, should be non-null because of no
	 *            public setter
	 */
	public MonitoredInputStream(InputStream in) {
		super(in);
		mark = 0;
		location = 0;
	}

	public long getProgress() {
		return location;
	}

	@Override
	public int read() throws IOException {
		final int i = super.read();
		if (i != END_OF_STREAM) {
			location++;
		}
		return i;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		final int i = super.read(b, off, len);
		if (i > 0) {
			location += i;
		}
		return i;
	}

	@Override
	public long skip(long n) throws IOException {
		final long i = super.skip(n);
		if (i > 0) {
			location += i;
		}
		return i;
	}

	@Override
	public void mark(int readlimit) {
		super.mark(readlimit);
		mark = location;
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		if (location != mark) {
			location = mark;
		}
	}
}
