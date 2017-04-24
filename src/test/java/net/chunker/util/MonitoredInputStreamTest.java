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
		long expectedProgress = 0;
		try (MonitoredInputStream in = getInputStream()) {
			while (in.read() != -1) {
				assertEquals(++expectedProgress, in.getProgress());
			}
		}
	}

	@Test
	public void bufferedRead() throws IOException {
		final int size = 2;
		long expectedProgress = 0;
		byte[] b = new byte[size];
		try (MonitoredInputStream in = getInputStream()) {
			int bytesRead;
			while ((bytesRead = in.read(b, 0, size)) != -1) {
				expectedProgress += bytesRead;
				assertEquals(expectedProgress, in.getProgress());
			}
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
	public void skip_zero() throws IOException {
		final long skip = 0;
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
			in.reset();
			assertEquals(0, in.getProgress());
		}
	}
}
