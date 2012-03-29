package jadex.android.xmlpullparser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javaxx.xml.XMLConstants;
import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamWriter;
import javaxx.xml.transform.Result;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class XMLPullStreamWriter implements XMLStreamWriter {

	private XmlSerializer serializer;

	// private Queue<IXMLWriterOperation> cachedOperations = new
	// LinkedList<IXMLWriterOperation>();
	private IXMLWriterOperation cachedWriterOperation;

	/** The default encoding. */
	public static String DEFAULT_ENCODING = "utf-8";

	public XMLPullStreamWriter(Result result) throws XmlPullParserException {
		throw new XmlPullParserException("Creating StreamWriter from result not supported.");
	}
	
	public XMLPullStreamWriter(OutputStream stream) throws XmlPullParserException, IllegalArgumentException,
			IllegalStateException, IOException {
		this(stream, DEFAULT_ENCODING);
	}

	public XMLPullStreamWriter(OutputStream stream, String encoding) throws XmlPullParserException,
	IllegalArgumentException, IllegalStateException, IOException {
		this(new OutputStreamWriter(stream, encoding));
	}

	public XMLPullStreamWriter(Writer stream) throws XmlPullParserException, IllegalArgumentException,
			IllegalStateException, IOException {
		XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		serializer = parserFactory.newSerializer();
		serializer.setOutput(stream);
	}

	@Override
	public void writeStartElement(String localName) throws XMLStreamException {
		writeStartElement(XMLConstants.NULL_NS_URI, localName);
	}

	@Override
	public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
		writeStartElement(null, localName, namespaceURI);
	}

	@Override
	public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
		try {
			if (prefix != null) {
				serializer.setPrefix(prefix, namespaceURI);
			}
			// cachedOperations.add(new XMLWriterStartTagOperation(namespaceURI,
			// localName));
			cachedWriterOperation = new XMLWriterStartTagOperation(namespaceURI, localName);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void writeEmptyElement(String localName) throws XMLStreamException {
		writeEmptyElement(XMLConstants.NULL_NS_URI, localName);
	}

	@Override
	public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
		writeEmptyElement(null, localName, namespaceURI);
	}

	@Override
	public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
		try {
			if (prefix != null) {
				serializer.setPrefix(prefix, namespaceURI);
			}
			serializer.startTag(namespaceURI, localName);
			serializer.endTag(namespaceURI, localName);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void writeEndElement() throws XMLStreamException {
		try {
			serializer.endTag(serializer.getNamespace(), serializer.getName());
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void writeEndDocument() throws XMLStreamException {
		try {
			serializer.endDocument();
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void close() throws XMLStreamException {
		try {
			serializer.endDocument();
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void flush() throws XMLStreamException {
		try {
			serializer.flush();
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void writeAttribute(String localName, String value) throws XMLStreamException {
		writeAttribute(XMLConstants.NULL_NS_URI, localName, value);
	}

	@Override
	public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
		writeAttribute(null, namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
			throws XMLStreamException {
		doStartElement();
		try {
			if (prefix != null) {
				serializer.setPrefix(prefix, namespaceURI);
			}
			serializer.attribute(namespaceURI, localName, value);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}

	}

	@Override
	public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
		try {
			if (!serializer.getPrefix(namespaceURI, false).equals(prefix)) {
				serializer.setPrefix(prefix, namespaceURI);
			}
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
		writeNamespace(XMLConstants.DEFAULT_NS_PREFIX, namespaceURI);
	}

	@Override
	public void writeComment(String data) throws XMLStreamException {
		doStartElement();
		try {
			serializer.comment(data);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void writeProcessingInstruction(String target) throws XMLStreamException {
		try {
			// serializer.processingInstruction(target);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
		try {
			serializer.processingInstruction(data);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void writeCData(String data) throws XMLStreamException {
		doStartElement();
		try {
			serializer.cdsect(data);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void writeDTD(String dtd) throws XMLStreamException {
		try {
			serializer.docdecl(dtd);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void writeEntityRef(String name) throws XMLStreamException {
		try {
			serializer.entityRef(name);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void writeStartDocument() throws XMLStreamException {
		try {
			serializer.startDocument(DEFAULT_ENCODING, false);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void writeStartDocument(String version) throws XMLStreamException {
		// try {
		// serializer.setProperty(XML, value)
		// serializer.startDocument(DEFAULT_ENCODING, true);
		// } catch (Exception e) {
		throw new XMLStreamException("writeStartDocument(version) unsupported!)");
		// }
	}

	@Override
	public void writeStartDocument(String encoding, String version) throws XMLStreamException {
		try {
			serializer.startDocument(encoding, false);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void writeCharacters(String text) throws XMLStreamException {
		doStartElement();
		try {
			serializer.text(text);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}

	}

	@Override
	public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
		try {
			serializer.text(new String(text).substring(start, start + len));
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public String getPrefix(String uri) throws XMLStreamException {
		return serializer.getPrefix(uri, false);
	}

	@Override
	public void setPrefix(String prefix, String uri) throws XMLStreamException {
		try {
			serializer.setPrefix(prefix, uri);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void setDefaultNamespace(String uri) throws XMLStreamException {
		try {
			serializer.setPrefix(XMLConstants.DEFAULT_NS_PREFIX, uri);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
		context.getPrefixes(XMLConstants.NULL_NS_URI);
		throw new XMLStreamException("setNamespaceContext(NamespaceContext context) unsupported!)");
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return null;
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return serializer.getProperty(name);
	}

	private void doStartElement() throws XMLStreamException {
		// if (!cachedOperations.isEmpty()) {
		if (cachedWriterOperation != null) {
			// IXMLWriterOperation op = cachedOperations.poll();
			IXMLWriterOperation op = cachedWriterOperation;
			cachedWriterOperation = null;
			try {
				op.execute(serializer);
			} catch (Exception e) {
				throw new XMLStreamException(e.getMessage());
			}
		}
	}

}
