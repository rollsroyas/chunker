package net.chunker.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class MonitoredInputStreamTest {

	private MonitoredInputStream getInputStream() {
		return new MonitoredInputStream(MonitoredInputStreamTest.class.getResourceAsStream("/json/cd_catalog.json"));
	}

	@Test
	public void read() throws IOException {
		try (MonitoredInputStream in = getInputStream()) {
			in.read();
			assertEquals(1, in.getProgress());
		}
	}

	@Test
	public void bufferedRead() throws IOException {
		final int size = 2;
		byte[] b = new byte[size];
		try (MonitoredInputStream in = getInputStream()) {
			in.read(b, 0, size);
			assertEquals(size, in.getProgress());
		}
	}

	@Test
	public void skip() throws IOException {
		final long skip = 3;
		try (MonitoredInputStream in = getInputStream()) {
			in.skip(skip);
			assertEquals(skip, in.getProgress());
		}
	}

	@Test
	public void markReadRest() throws IOException {
		try (MonitoredInputStream in = getInputStream()) {
			in.mark(10);
			in.read();
			in.reset();
			assertEquals(0, in.getProgress());
		}
	}
}
