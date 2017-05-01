package net.chunker.xml.impl;

import static net.chunker.util.Validations.checkNotNull;

import java.io.StringWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import net.chunker.util.Chunkers;
import net.chunker.util.MemoryManager;
import net.chunker.xml.api.XmlChunkFactory;
import net.chunker.xml.api.XmlChunker;
import net.chunker.xml.api.XmlElementMatcher;

/**
 * This class assumes memory is limited and therefore provides a means to break
 * large XML documents into smaller chunks which can be processed using less
 * memory. Special thanks to
 * <a>http://fgeorges.blogspot.com/2006/08/translate-sax-events-to-dom-tree.html
 * </a>
 * 
 * @author rollsroyas@alumni.ncsu.edu
 *
 * @param <A>
 *            The JAXB class that wraps the repeated element (chunk)
 */
public class XmlChunkerImpl<A> extends XmlChunker {
	private static Logger LOG = LoggerFactory.getLogger(XmlChunkerImpl.class);

	private BlockingQueue<Callable<A>> queue;
	private XmlElementMatcher matcher;
	private XmlChunkFactory<A> factory;
	private Transformer transformer;
	private MemoryManager memoryManager;
	private Document document;
	private final int chunkSize;

	private boolean firstMatch;
	private int currentChunkSize;
	private Node currentNode;
	private Element inChunkElement;

	private Exception exception;

	private XmlChunkerImpl(Builder<A> builder) {
		this.queue = builder.queue;
		this.matcher = builder.matcher;
		this.factory = builder.factory;
		this.transformer = builder.transformer;
		this.currentNode = this.document = builder.document;
		this.chunkSize = builder.chunkSize;
		this.memoryManager = builder.memoryManager;

		inChunkElement = null;

		firstMatch = true;
		currentChunkSize = 0;
	}

	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================

	/**
	 * @see net.chunker.xml.api.XmlChunker#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		// Must implement method, however this event is not needed to chunk the
		// XML
	}

	/**
	 * @see net.chunker.xml.api.XmlChunker#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
		doChunk(true);
		document = null;
	}

	/**
	 * @see net.chunker.xml.api.XmlChunker#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String namespaceURI, String sName, // simple name
			String qName, // qualified name
			Attributes attrs) throws SAXException {

		// Create the element.
		Element elem = document.createElementNS(namespaceURI, qName);

		if (matchesExpectedNamespaceAndName(namespaceURI, sName) && inChunkElement == null) {
			if (firstMatch) {
				firstMatch = false;
			} else if (currentChunkSize >= chunkSize) {
				doChunk(false);
				removeNodesForTheChunkFromCurrentNode();
			}
			inChunkElement = elem;
			currentChunkSize++;
		}

		// Add each attribute.
		copyAttributesToElement(attrs, elem);
		// Actually add it in the tree, and adjust the right place.
		currentNode.appendChild(elem);
		currentNode = elem;

	}

	private void removeNodesForTheChunkFromCurrentNode() {
		NodeList list = currentNode.getChildNodes();
		for (int i = list.getLength() - 1; i >= 0; i--) {
			Node node = list.item(i);
			if (matchesExpectedNamespaceAndName(node.getNamespaceURI(), node.getLocalName())
					|| (i > 0 && isBlank(node))) {
				currentNode.removeChild(node);
			}
		}
		currentChunkSize = 0;
	}

	private void copyAttributesToElement(Attributes attrs, Element elem) {
		final int length = attrs.getLength();
		for (int i = 0; i < length; i++) {
			String nsUri = attrs.getURI(i);
			String qname = attrs.getQName(i);
			String value = attrs.getValue(i);
			Attr attr = document.createAttributeNS(nsUri, qname);
			attr.setValue(value);
			elem.setAttributeNodeNS(attr);
		}
	}

	static boolean isBlank(final CharSequence cs) {
		final int length;
		if (cs == null || (length = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < length; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	static boolean isBlank(Node node) {
		if (node instanceof Text) {
			Text text = (Text) node;
			return isBlank(text.getWholeText());
		}
		return false;
	}

	private boolean matchesExpectedNamespaceAndName(String namespaceURI, String sName) {
		return matcher.acceptsName(namespaceURI, sName);
	}

	/**
	 * @see net.chunker.xml.api.XmlChunker#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String namespaceURI, String sName, // simple name
			String qName // qualified name
	) throws SAXException {
		if (currentNode == inChunkElement) {
			inChunkElement = null;
		}

		currentNode = currentNode.getParentNode();
	}

	/**
	 * @see net.chunker.xml.api.XmlChunker#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] buf, int offset, int len) throws SAXException {
		String str = new String(buf, offset, len);
		Text text = document.createTextNode(str);
		currentNode.appendChild(text);
	}

	void doChunk(boolean last) throws SAXException {
		String text = null;
		try {
			// Uncommenting this sleep allowed me to proves the code is reading
			// data from queue while simultaneously adding to it.
			// if (last) Thread.sleep(2000);

			text = domToString();

			LOG.trace("Chunk text:\n{}", text);

			putChunkOnQueue(text, last);

		} catch (Exception e) {
			if (e instanceof InterruptedException) {
				Thread.currentThread()
					.interrupt();
			}
			throw new SAXException("Unable to process chunk:\n" + text, e);
		} finally {
			gcIfNecessary();
		}
	}

	private void putChunkOnQueue(String text, boolean last) throws InterruptedException {
		queue.put(factory.create(text, last));
	}

	private void gcIfNecessary() {
		MemoryManager memMan = this.memoryManager;
		if (memMan != null) {
			memMan.gcIfNecessary();
		}
	}

	private String domToString() throws TransformerException {
		DOMSource source = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		return writer.toString();
	}

	/**
	 * @see net.chunker.xml.api.XmlChunker#finish()
	 */
	@Override
	public void finish() {
		try {
			Callable<A> finalCallable = Chunkers.<A>createFinalCallable(exception);
			queue.put(finalCallable);
		} catch (InterruptedException e1) {
			LOG.error("Unable to add final chunk to queue", exception);
			Thread.currentThread()
				.interrupt();
		}
	}

	/**
	 * @see net.chunker.xml.api.XmlChunker#setException(java.lang.Exception)
	 */
	@Override
	public void setException(final Exception e) {
		this.exception = e;
	}

	public static final class Builder<A> {
		MemoryManager memoryManager;
		BlockingQueue<Callable<A>> queue;
		XmlElementMatcher matcher;
		XmlChunkFactory<A> factory;
		Transformer transformer;
		Document document;
		int chunkSize;

		private Builder() {
			chunkSize = 1;
		}

		/**
		 * @param queue
		 *            Contains the chunks, Note that when the Callable returns null
		 *            then there are no more chunks to process
		 * @param factory
		 *            Creates the chunks (Callables) that go on the queue
		 * @param transformer
		 *            Converts from DOM to String
		 * @param document
		 *            Stores the DOM for the current chunk
		 * @param chunkSize
		 *            The max number of elements in a chunk
		 * @param memoryManager
		 *            This functional interface will be invoked after every chunk
		 */
		/**
		 * @param  chunkSize
		 * 		The max number of repeated objects in a chunk, defaults to 1.
		 * @return this
		 */
		public Builder<A> chunkSize(int chunkSize) {
			this.chunkSize = chunkSize;
			return this;
		}

		/**
		 * @param transformer
		 *			Defaulted if not supplied.
		 *			Converts from DOM to String.
		 * @return this
		 */
		public Builder<A> transformer(Transformer transformer) {
			this.transformer = transformer;
			return this;
		}

		/**
		 * @param document
		 * 			Defaulted if not supplied.
		 * 			Stores the DOM for the current chunk
		 * @return this
		 */
		public Builder<A> document(Document document) {
			this.document = document;
			return this;
		}

		/**
		 * @param memoryManager
		 * 		This optional functional interface, if present, will be invoked after every chunk
		 * @return this
		 */
		public Builder<A> memoryManager(MemoryManager memoryManager) {
			this.memoryManager = memoryManager;
			return this;
		}

		/**
		 * @param queue
		 * 		This required object contains the chunks.
		 *		Note that when the ({@link Callable}s) returns null
		 *      then there are no more chunks to process.
		 * @return this
		 */
		public Builder<A> queue(BlockingQueue<Callable<A>> queue) {
			this.queue = queue;
			return this;
		}

		/**
		 * @param matcher
		 * 		This required object, identifies the repeated elements to be chunked in a JSON array
		 * @return this
		 */
		public Builder<A> matcher(XmlElementMatcher matcher) {
			this.matcher = matcher;
			return this;
		}

		/**
		 * @param factory
		 * 		This required object, creates the chunks ({@link Callable}s) that go on the queue
		 * @return this
		 */
		public Builder<A> factory(XmlChunkFactory<A> factory) {
			this.factory = factory;
			return this;
		}

		private void defaultTransformerIfNull() {
			if (this.transformer == null) {
				try {
					this.transformer = TransformerFactory.newInstance()
						.newTransformer();
				} catch (TransformerConfigurationException e) {
					throw new IllegalStateException(e);
				}
			}
		}

		private void defaultDocumentIfNull() {
			if (this.document == null) {
				// Find the implementation
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				docFactory.setNamespaceAware(true);
				DocumentBuilder builder;
				try {
					builder = docFactory.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					throw new IllegalStateException(e);
				}
				DOMImplementation impl = builder.getDOMImplementation();

				// Create the document
				this.document = impl.createDocument(null, null, null);
			}
		}

		public void validate() {
			checkNotNull(queue, "queue cannot be null");
			checkNotNull(matcher, "matcher cannot be null");
			checkNotNull(factory, "factory cannot be null");
		}

		public XmlChunkerImpl<A> build() {
			validate();
			defaultTransformerIfNull();
			defaultDocumentIfNull();
			return new XmlChunkerImpl<>(this);
		}
	}

	public static <A> Builder<A> builder() {
		return new Builder<>();
	}
}
