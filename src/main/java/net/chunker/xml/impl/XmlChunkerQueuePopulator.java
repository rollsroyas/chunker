package net.chunker.xml.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.chunker.util.MonitoredInputStream;
import net.chunker.xml.api.XmlChunker;

import org.xml.sax.SAXException;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class XmlChunkerQueuePopulator {
	
	public static final class Builder {
		private InputStream inputStream;
		private SAXParser saxParser;
		private XmlChunker chunker;
		
		private Builder() {}
		
		/**
		 * Strongly recommended to wrap this InputStream in a java.io.BufferedInputStream
		 */
		public Builder inputStream(InputStream inputStream) {
			this.inputStream = inputStream;
			return this;
		}
		public Builder saxParser(SAXParser saxParser) {
			this.saxParser = saxParser;
			return this;
		}
		public Builder chunker(XmlChunker chunker) {
			this.chunker = chunker;
			return this;
		}
		private void defaultSaxParserIfNull() {
			if (this.saxParser == null) {
				// Use the default (non-validating) parser
				SAXParserFactory saxFactory = SAXParserFactory.newInstance();
				saxFactory.setNamespaceAware(true);	
				try {
					this.saxParser = saxFactory.newSAXParser();
				} catch (ParserConfigurationException | SAXException e) {
					throw new IllegalStateException(e);
				}
			}
		}
		
		public void validate() {
			if (inputStream == null) throw new NullPointerException("inputStream cannot be null");
			if (chunker == null) throw new NullPointerException("chunker cannot be null");
		}
		
		public XmlChunkerQueuePopulator build() {
			validate();
			defaultSaxParserIfNull();
			return new XmlChunkerQueuePopulator(chunker, saxParser, inputStream);
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	//private static Logger LOG = LoggerFactory.getLogger(XmlChunkerQueuePopulator.class);

	private final InputStream inputStream;
	private final SAXParser saxParser;
	private final XmlChunker chunker;
	
	private XmlChunkerQueuePopulator(XmlChunker chunker, SAXParser saxParser, InputStream inputStream) {
		this.inputStream=inputStream;
		this.saxParser=saxParser;
		this.chunker=chunker;		
	}
	
	/**
	 * The last element in the queue will we be a Callable whose call method returns null 
	 * or it will throw an exception if the sax parser threw one
	 */
	public void populate() {		
		new Thread(createPopulatorRunnable()).start();
	}

	protected Runnable createPopulatorRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				MonitoredInputStream input = null;
				try (MonitoredInputStream mis = new MonitoredInputStream(inputStream)){
					input = mis;
					saxParser.parse(mis, chunker);
				} catch (final Exception e) {
					chunker.setException(new IOException("Error around byte #"+(input == null ? 0L : input.getProgress()), e));
				} finally {
					chunker.finish();
				}
			}
		};
	}
}
