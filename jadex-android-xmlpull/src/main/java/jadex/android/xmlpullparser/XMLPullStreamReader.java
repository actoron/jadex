package jadex.android.xmlpullparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javaxx.xml.XMLConstants;
import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.Location;
import javaxx.xml.stream.XMLReporter;
import javaxx.xml.stream.XMLStreamConstants;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * The XMLPullStreamReader implements the StaX
 * {@link javax.xml.stream.XMLStreamReader} interface, but uses the Android XML
 * Pull Parser.
 */
public class XMLPullStreamReader implements XMLStreamReader {

	private XmlPullParser parser;
	private XmlPullParserFactory factory;
	private boolean hasNext;
	private Reader inputReader;
	private XMLReporter reporter;

	/**
	 * The following Tag Types are not supported by Android Pull Parser.
	 * 
	 */
	public static final int ATTRIBUTE = 100;
	public static final int NAMESPACE = 101;
	public static final int NOTATION_DECLARATION = 102;
	public static final int ENTITY_DECLARATION = 103;

	/**
	 * StaX Tag Types.
	 * Array Index is Pull Tag Type.
	 */
	private final static int[] TRANSLATE_EVENTS_FROM_PULL = {
			XMLStreamConstants.START_DOCUMENT, XMLStreamConstants.END_DOCUMENT,
			XMLStreamConstants.START_ELEMENT, XMLStreamConstants.END_ELEMENT,
			XMLStreamConstants.CHARACTERS, XMLStreamConstants.CDATA,
			XMLStreamConstants.ENTITY_REFERENCE, XMLStreamConstants.SPACE,
			XMLStreamConstants.PROCESSING_INSTRUCTION,
			XMLStreamConstants.COMMENT, XMLStreamConstants.DTD };

	/**
	 * Pull Tag Types.
	 * Array Index is StaX Tag Type.
	 */
	private final static int[] TRANSLATE_EVENTS_TO_PULL = {
			XmlPullParser.START_TAG, XmlPullParser.END_TAG,
			XmlPullParser.PROCESSING_INSTRUCTION, XmlPullParser.TEXT,
			XmlPullParser.COMMENT, XmlPullParser.IGNORABLE_WHITESPACE,
			XmlPullParser.START_DOCUMENT, XmlPullParser.END_DOCUMENT,
			XmlPullParser.ENTITY_REF, ATTRIBUTE, XmlPullParser.DOCDECL,
			XmlPullParser.CDSECT, NAMESPACE, NOTATION_DECLARATION,
			ENTITY_DECLARATION };

	/**
	 * Create an XMLPullStreamReader for a given InputStream, using the default
	 * encoding.
	 * 
	 * @param stream
	 * @throws XmlPullParserException
	 * @throws UnsupportedEncodingException
	 */
	public XMLPullStreamReader(InputStream stream)
			throws XmlPullParserException, UnsupportedEncodingException {
		this(stream, XMLPullStreamWriter.DEFAULT_ENCODING);
	}

	/**
	 * Create an XMLPullStreamReader for a given input Stream with specified
	 * encoding.
	 * 
	 * @param stream
	 * @param encoding
	 * @throws XmlPullParserException
	 * @throws UnsupportedEncodingException
	 */
	public XMLPullStreamReader(InputStream stream, String encoding)
			throws XmlPullParserException, UnsupportedEncodingException {
		this(new InputStreamReader(stream, encoding));
	}

	/**
	 * Create an XMLPullStreamReader for a given Reader.
	 * 
	 * @param reader
	 * @throws XmlPullParserException
	 */
	public XMLPullStreamReader(Reader reader) throws XmlPullParserException {
		factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		parser = factory.newPullParser();
		parser.setInput(reader);
		hasNext = true;
		inputReader = reader;
	}

	/**
	 * Delegates to XMLPullParser.setFeature()
	 * @param feature
	 * @param value
	 * @throws IllegalArgumentException
	 */
	public void setFeature(String feature, boolean value)
			throws IllegalArgumentException {
		try {
			parser.setFeature(feature, value);
		} catch (XmlPullParserException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Sets the XMLReporter for this Reader.
	 * Currently, this is not used. 
	 * @param reporter
	 */
	public void setReporter(XMLReporter reporter) {
		this.reporter = reporter;
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return parser.getProperty(name);
	}

	@Override
	public int next() throws XMLStreamException {
		int next = -1;
		try {
			next = translateEventFromPullParser(parser.next());
		} catch (XmlPullParserException e) {
			throw new XMLStreamException(e.getMessage());
		} catch (IOException e) {
			throw new XMLStreamException(e.getMessage());
		}
		return next;
	}

	@Override
	public void require(int type, String namespaceURI, String localName)
			throws XMLStreamException {
		type = translateEventToPullParser(type);
		try {
			parser.require(type, namespaceURI, localName);
		} catch (XmlPullParserException e) {
			throw new XMLStreamException(e.getMessage());
		} catch (IOException e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public String getElementText() throws XMLStreamException {
		return parser.getText();
	}

	@Override
	public int nextTag() throws XMLStreamException {
		try {
			return translateEventFromPullParser(parser.nextTag());
		} catch (XmlPullParserException e) {
			throw new XMLStreamException(e.getMessage());
		} catch (IOException e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public boolean hasNext() throws XMLStreamException {
		return hasNext;
	}

	@Override
	public void close() throws XMLStreamException {
		hasNext = false;
		try {
			inputReader.close();
		} catch (IOException e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public String getNamespaceURI(String prefix) {
		return parser.getNamespace(prefix);
	}

	@Override
	public boolean isStartElement() {
		try {
			return (parser.getEventType() == XmlPullParser.START_TAG);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isEndElement() {
		try {
			return (parser.getEventType() == XmlPullParser.END_TAG);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isCharacters() {
		try {
			return (parser.getEventType() == XmlPullParser.TEXT);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isWhiteSpace() {
		try {
			return (parser.getEventType() == XmlPullParser.IGNORABLE_WHITESPACE);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String getAttributeValue(String namespaceURI, String localName) {
		return parser.getAttributeValue(namespaceURI, localName);
	}

	@Override
	public int getAttributeCount() {
		return parser.getAttributeCount();
	}

	@Override
	public QName getAttributeName(int index) {
		return new QName(parser.getNamespace(), parser.getAttributeName(index),
				parser.getAttributePrefix(index));
	}

	@Override
	public String getAttributeNamespace(int index) {
		return parser.getAttributeNamespace(index);
	}

	@Override
	public String getAttributeLocalName(int index) {
		return parser.getAttributeName(index);
	}

	@Override
	public String getAttributePrefix(int index) {
		return parser.getAttributePrefix(index);
	}

	@Override
	public String getAttributeType(int index) {
		return parser.getAttributeType(index);
	}

	@Override
	public String getAttributeValue(int index) {
		return parser.getAttributeValue(index);
	}

	@Override
	public boolean isAttributeSpecified(int index) {
		return parser.isAttributeDefault(index);
	}

	@Override
	public int getNamespaceCount() {
		try {
			return parser.getNamespaceCount(parser.getDepth());
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public String getNamespacePrefix(int index) {
		try {
			return parser.getNamespacePrefix(index);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return XMLConstants.DEFAULT_NS_PREFIX;
		}
	}

	@Override
	public String getNamespaceURI(int index) {
		try {
			return parser.getNamespaceUri(index);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return XMLConstants.NULL_NS_URI;
		}
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		NamespaceContextImpl context = new NamespaceContextImpl();
		int nsStart;
		try {
			nsStart = parser.getNamespaceCount(parser.getDepth() - 1);
			int nsEnd = parser.getNamespaceCount(parser.getDepth());
			for (int i = nsStart; i < nsEnd; i++) {
				String prefix = parser.getNamespacePrefix(i);
				String ns = parser.getNamespaceUri(i);
				context.addNamespace(prefix, ns);
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return context;
	}

	@Override
	public int getEventType() {
		try {
			return translateEventFromPullParser(parser.getEventType());
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public String getText() {
		return parser.getText();
	}

	@Override
	public char[] getTextCharacters() {
		return parser.getTextCharacters(new int[2]);
	}

	@Override
	public int getTextCharacters(int sourceStart, char[] target,
			int targetStart, int length) throws XMLStreamException {
		char[] chars = parser.getTextCharacters(new int[2]);
		int i = 0;
		for (; i < length; i++) {
			target[i + targetStart] = chars[i + sourceStart];
		}
		return i;
	}

	@Override
	public int getTextStart() {
		throw new Error(this.getClass().getName()
				+ ": getTextStart() is not supported.");
	}

	@Override
	public int getTextLength() {
		return parser.getText().length();
	}

	@Override
	public String getEncoding() {
		return parser.getInputEncoding();
	}

	@Override
	public boolean hasText() {
		int eventType = -1;
		try {
			eventType = parser.getEventType();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return (eventType == XmlPullParser.TEXT
				|| eventType == XmlPullParser.COMMENT
				|| eventType == XmlPullParser.ENTITY_REF
				|| eventType == XmlPullParser.IGNORABLE_WHITESPACE || eventType == XmlPullParser.DOCDECL);
	}

	@Override
	public Location getLocation() {
		return new XMLPullLocation(parser.getLineNumber(),
				parser.getColumnNumber(), 0, null, null);
	}

	@Override
	public QName getName() {
		return new QName(parser.getNamespace(), parser.getName(),
				parser.getPrefix());
	}

	@Override
	public String getLocalName() {
		return parser.getName();
	}

	@Override
	public boolean hasName() {
		int eventType = -1;
		try {
			eventType = parser.getEventType();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return (eventType == XmlPullParser.START_TAG || eventType == XmlPullParser.END_TAG);
	}

	@Override
	public String getNamespaceURI() {
		return parser.getNamespace();
	}

	@Override
	public String getPrefix() {
		return parser.getPrefix();
	}

	@Override
	public String getVersion() {
		throw new MethodNotImplementedError(this.getClass().getName()
				+ ": getVersion() is not supported.");
	}

	@Override
	public boolean isStandalone() {
		throw new MethodNotImplementedError(this.getClass().getName()
				+ ": isStandalone() is not supported.");
	}

	@Override
	public boolean standaloneSet() {
		throw new MethodNotImplementedError(this.getClass().getName()
				+ ": standaloneSet() is not supported.");
	}

	@Override
	public String getCharacterEncodingScheme() {
		throw new MethodNotImplementedError(this.getClass().getName()
				+ ": getCharacterEncodingScheme() is not supported.");
	}

	@Override
	public String getPITarget() {
		throw new MethodNotImplementedError(this.getClass().getName()
				+ ": getPITarget() is not supported.");
	}

	@Override
	public String getPIData() {
		throw new Error(this.getClass().getName()
				+ ": getPIData() is not supported.");
	}

	private int translateEventFromPullParser(int next)
			throws XMLStreamException {
		if (next < TRANSLATE_EVENTS_FROM_PULL.length) {
			if (next == XmlPullParser.END_DOCUMENT) {
				hasNext = false;
			}
			next = TRANSLATE_EVENTS_FROM_PULL[next];
			return next;
		} else {
			throw new XMLStreamException("Unknown token type: " + next);
		}
	}

	private int translateEventToPullParser(int next) throws XMLStreamException {
		if (next < TRANSLATE_EVENTS_TO_PULL.length) {
			next = TRANSLATE_EVENTS_TO_PULL[next];
			return next;
		} else {
			throw new XMLStreamException("Unknown token type: " + next);
		}
	}

	private class NamespaceContextImpl implements NamespaceContext {

		private Map<String, List<String>> namespaceToPrefixes = new HashMap<String, List<String>>();
		private Map<String, String> prefixToNS = new HashMap<String, String>();

		public void addNamespace(String prefix, String namespace) {
			List<String> list = namespaceToPrefixes.get(namespace);
			if (list == null) {
				list = new ArrayList<String>();
				namespaceToPrefixes.put(namespace, list);
			}
			list.add(prefix);

			prefixToNS.put(prefix, namespace);
		}

		@Override
		public String getNamespaceURI(String prefix) {
			return prefixToNS.get(prefix);
		}

		@Override
		public String getPrefix(String namespaceURI) {
			if (namespaceURI.equals(XMLConstants.NULL_NS_URI)) {
				return XMLConstants.DEFAULT_NS_PREFIX;
			} else {
				List<String> list = namespaceToPrefixes.get(namespaceURI);
				return list != null ? list.get(0) : null;
			}
		}

		@Override
		public Iterator getPrefixes(String namespaceURI) {
			return namespaceToPrefixes.get(namespaceURI).iterator();
		}
	}

	private class XMLPullLocation implements Location {

		private String systemId;
		private String publicId;
		private int charOffset;
		private int column;
		private int line;

		public XMLPullLocation(int line, int column, int charOffset,
				String publicId, String systemId) {
			this.line = line;
			this.column = column;
			this.charOffset = charOffset;
			this.publicId = publicId;
			this.systemId = systemId;
		}

		@Override
		public int getLineNumber() {
			return line;
		}

		@Override
		public int getColumnNumber() {
			return column;
		}

		@Override
		public int getCharacterOffset() {
			return charOffset;
		}

		@Override
		public String getPublicId() {
			return publicId;
		}

		@Override
		public String getSystemId() {
			return systemId;
		}

	}

}
