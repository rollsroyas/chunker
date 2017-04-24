package net.chunker.xml.impl;

import static net.chunker.util.Validations.checkNotNull;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import net.chunker.util.MonitoredInputStream;
import net.chunker.xml.api.XmlChunker;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class XmlChunkerQueuePopulator {

	private final InputStream inputStream;
	private final SAXParser parser;
	private final XmlChunker chunker;

	private XmlChunkerQueuePopulator(Builder builder) {
		this.inputStream = builder.inputStream;
		this.parser = builder.parser;
		this.chunker = builder.chunker;
	}

	/**
	 * The last element in the queue will we be a Callable whose call method
	 * returns null or it will throw an exception if the sax parser threw one
	 */
	public void populate() {
		new Thread(createPopulatorRunnable()).start();
	}

	protected Runnable createPopulatorRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				MonitoredInputStream input = null;
				try (MonitoredInputStream mis = new MonitoredInputStream(inputStream)) {
					input = mis;
					parser.parse(mis, chunker);
				} catch (final Exception e) {
					chunker.setException(new IOException(
							"Error around byte #" + (input == null ? 0L : input.getProgress()), e));
				} finally {
					chunker.finish();
				}
			}
		};
	}

	public static final class Builder {
		private InputStream inputStream;
		private SAXParser parser;
		private XmlChunker chunker;

		private Builder() {
		}

		/**
		 * Strongly recommended to wrap this InputStream in a
		 * java.io.BufferedInputStream
		 * 
		 * @param inputStream
		 *            Strongly recommended to wrap this InputStream in a
		 *            java.io.BufferedInputStream
		 * @return this Builder so that one can chain the calls
		 */
		public Builder inputStream(InputStream inputStream) {
			this.inputStream = inputStream;
			return this;
		}

		public Builder parser(SAXParser parser) {
			this.parser = parser;
			return this;
		}

		public Builder chunker(XmlChunker chunker) {
			this.chunker = chunker;
			return this;
		}

		private void defaultParserIfNull() {
			if (this.parser == null) {
				// Use the default (non-validating) parser
				SAXParserFactory saxFactory = SAXParserFactory.newInstance();
				saxFactory.setNamespaceAware(true);
				try {
					this.parser = saxFactory.newSAXParser();
				} catch (ParserConfigurationException | SAXException e) {
					throw new IllegalStateException(e);
				}
			}
		}

		public void validate() {
			checkNotNull(inputStream, "inputStream cannot be null");
			checkNotNull(chunker, "chunker cannot be null");
		}

		public XmlChunkerQueuePopulator build() {
			validate();
			defaultParserIfNull();
			return new XmlChunkerQueuePopulator(this);
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}
