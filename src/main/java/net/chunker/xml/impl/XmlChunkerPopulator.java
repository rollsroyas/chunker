package net.chunker.xml.impl;

import static net.chunker.util.Validations.checkNotBothPresent;
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
public class XmlChunkerPopulator {

	private final InputStream inputStream;
	private final SAXParser parser;
	private final XmlChunker chunker;

	private XmlChunkerPopulator(Builder builder) {
		this.inputStream = builder.inputStream;
		this.parser = builder.parser;
		this.chunker = builder.chunker;
	}

	/**
	 * Populates the XmlChunker in a new background thread.
	 * 
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
		InputStream inputStream;
		SAXParserFactory parserFactory;
		SAXParser parser;
		XmlChunker chunker;

		private Builder() {
		}

		/**
		 * @param inputStream
		 * 			Required object.
		 *			Strongly recommended to wrap this InputStream in a
		 *			java.io.BufferedInputStream
		 * @return this
		 */
		public Builder inputStream(InputStream inputStream) {
			this.inputStream = inputStream;
			return this;
		}

		/**
		 * @param parserFactory
		 * 			A parser or parserFactory is required, but do not supply both.
		 * @return this
		 */
		public Builder parserFactory(SAXParserFactory parserFactory) {
			this.parserFactory = parserFactory;
			return this;
		}
		
		/**
		 * @param parser
		 * 			A parser or parserFactory is required, but do not supply both.
		 * @return this
		 */
		public Builder parser(SAXParser parser) {
			this.parser = parser;
			return this;
		}

		/**
		 * @param chunker
		 * 			This required object gathers the SAX events and creates chunks
		 * @return this
		 */
		public Builder chunker(XmlChunker chunker) {
			this.chunker = chunker;
			return this;
		}
		
		private void defaultParserFactoryIfNull() {
			if (this.parserFactory == null) {
				// Use the default (non-validating) parser
				this.parserFactory = SAXParserFactory.newInstance();
				parserFactory.setNamespaceAware(true);
			}
		}

		private void defaultParserIfNull() {
			if (this.parser == null) {
				try {
					this.parser = this.parserFactory.newSAXParser();
				} catch (ParserConfigurationException | SAXException e) {
					throw new IllegalStateException(e);
				}
			}
		}

		public void validate() {
			checkNotNull(inputStream, "inputStream cannot be null");
			checkNotNull(chunker, "chunker cannot be null");
			checkNotBothPresent(parser, parserFactory, "parser and parserFactory cannot both be present");
		}

		public XmlChunkerPopulator build() {
			validate();
			defaultParserFactoryIfNull();
			defaultParserIfNull();
			return new XmlChunkerPopulator(this);
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}
