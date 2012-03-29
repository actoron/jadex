package jadex.android.xmlpullparser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import javaxx.xml.stream.EventFilter;
import javaxx.xml.stream.StreamFilter;
import javaxx.xml.stream.XMLEventReader;
import javaxx.xml.stream.XMLInputFactory;
import javaxx.xml.stream.XMLReporter;
import javaxx.xml.stream.XMLResolver;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.util.XMLEventAllocator;
import javaxx.xml.transform.Source;

public class XMLPullInputFactory extends XMLInputFactory {

	private Map<String, Object> properties = new HashMap<String, Object>();

	private XMLReporter reporter;

	private static Map<String, String> TRANSLATE_FEATURES_TOPULL = new HashMap<String, String>();

	static {
		TRANSLATE_FEATURES_TOPULL.put(XMLInputFactory.IS_VALIDATING, XmlPullParser.FEATURE_VALIDATION);
		TRANSLATE_FEATURES_TOPULL.put(XMLInputFactory.SUPPORT_DTD, XmlPullParser.FEATURE_PROCESS_DOCDECL);
		TRANSLATE_FEATURES_TOPULL.put(XMLInputFactory.IS_NAMESPACE_AWARE, XmlPullParser.FEATURE_PROCESS_NAMESPACES);
		TRANSLATE_FEATURES_TOPULL.put(XMLInputFactory.IS_COALESCING, XMLInputFactory.IS_COALESCING);
		TRANSLATE_FEATURES_TOPULL.put(XMLInputFactory.REPORTER, XMLInputFactory.REPORTER);
	}

	@Override
	public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
		return createXMLStreamReader(new InputStreamReader(stream));
	}

	@Override
	public XMLStreamReader createXMLStreamReader(InputStream stream, String encoding) throws XMLStreamException {
		try {
			return createXMLStreamReader(new InputStreamReader(stream, encoding));
		} catch (UnsupportedEncodingException e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
		return createXMLStreamReader(null, reader);
	}

	@Override
	public XMLStreamReader createXMLStreamReader(String systemId, InputStream stream) throws XMLStreamException {
		return createXMLStreamReader(systemId, new InputStreamReader(stream));
	}

	@Override
	public XMLStreamReader createXMLStreamReader(String systemId, Reader reader) throws XMLStreamException {
		try {
			XMLPullStreamReader xmlPullStreamReader = new XMLPullStreamReader(reader);
			passProperties(xmlPullStreamReader);
			return xmlPullStreamReader;
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
		try {
			XMLPullStreamReader xmlPullStreamReader = new XMLPullStreamReader(source);
			return xmlPullStreamReader;
		} catch (XmlPullParserException e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public XMLEventReader createXMLEventReader(Reader reader) throws XMLStreamException {
		throw new XMLStreamException("createXMLEventReader not supported.");
	}

	@Override
	public XMLEventReader createXMLEventReader(String systemId, Reader reader) throws XMLStreamException {
		throw new XMLStreamException("createXMLEventReader not supported.");
	}

	@Override
	public XMLEventReader createXMLEventReader(XMLStreamReader reader) throws XMLStreamException {
		throw new XMLStreamException("createXMLEventReader not supported.");
	}

	@Override
	public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
		throw new XMLStreamException("createXMLEventReader not supported.");
	}

	@Override
	public XMLEventReader createXMLEventReader(InputStream stream) throws XMLStreamException {
		throw new XMLStreamException("createXMLEventReader not supported.");
	}

	@Override
	public XMLEventReader createXMLEventReader(InputStream stream, String encoding) throws XMLStreamException {
		throw new XMLStreamException("createXMLEventReader not supported.");
	}

	@Override
	public XMLEventReader createXMLEventReader(String systemId, InputStream stream) throws XMLStreamException {
		throw new XMLStreamException("createXMLEventReader not supported.");
	}

	@Override
	public XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
		throw new XMLStreamException("createFilteredReader not supported.");
	}

	@Override
	public XMLEventReader createFilteredReader(XMLEventReader reader, EventFilter filter) throws XMLStreamException {
		throw new XMLStreamException("createFilteredReader not supported.");
	}

	@Override
	public XMLResolver getXMLResolver() {
		throw new IllegalArgumentException(this.getClass().getName() + ": getXMLResolver not supported!");
	}

	@Override
	public void setXMLResolver(XMLResolver resolver) {
		throw new IllegalArgumentException(this.getClass().getName() + ": setXMLResolver not supported!");

	}

	@Override
	public XMLReporter getXMLReporter() {
		return reporter;
	}

	@Override
	public void setXMLReporter(XMLReporter reporter) {
		throw new IllegalArgumentException(this.getClass().getName() + ": setXMLReporter not supported!");
	}

	@Override
	public void setProperty(String name, Object value) throws IllegalArgumentException {
		if (name.equals(XMLInputFactory.REPORTER)) {
			reporter = (XMLReporter) value;
		} else if (isPropertySupported(name)) {
			properties.put(name, value);
		} else {
			throw new IllegalArgumentException("This property is not supported: " + name);
		}
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return properties.get(name);
	}

	@Override
	public boolean isPropertySupported(String name) {
		return TRANSLATE_FEATURES_TOPULL.containsKey(name);
	}

	@Override
	public void setEventAllocator(XMLEventAllocator allocator) {
		throw new IllegalArgumentException(this.getClass().getName() + ": setEventAllocator not supported!");
	}

	@Override
	public XMLEventAllocator getEventAllocator() {
		throw new IllegalArgumentException(this.getClass().getName() + ": getEventAllocator not supported!");
	}

	private void passProperties(XMLPullStreamReader xmlPullStreamReader) {
		if (reporter != null) {
			xmlPullStreamReader.setReporter(reporter);
		}

		Set<Entry<String, Object>> entrySet = properties.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String feature = TRANSLATE_FEATURES_TOPULL.get(entry.getKey());
			Object value = entry.getValue();
			// if (feature.equals(XmlPullParser.FEATURE_VALIDATION) && value ==
			// Boolean.TRUE) {
			// xmlPullStreamReader.setFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL,
			// true);
			// }
			if (feature.equals(XmlPullParser.FEATURE_VALIDATION)) {
				return;
			}
			xmlPullStreamReader.setFeature(feature, (Boolean) value);
		}

	}

}
