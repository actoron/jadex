package jadex.android.xmlpullparser;

import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;

import javaxx.xml.stream.XMLEventWriter;
import javaxx.xml.stream.XMLOutputFactory;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamWriter;
import javaxx.xml.transform.Result;

/**
 * Factory for creating an Android XML Writer that is wrapped for implementing
 * StaX API. This Implementation will throw Exceptions for Methods not currently
 * supported.
 */
public class XMLPullOutputFactory extends XMLOutputFactory {

	private HashMap<String, Object> properties;

	/**
	 * Constructor
	 */
	public XMLPullOutputFactory() {
		properties = new HashMap<String, Object>();
	}

	@Override
	public XMLStreamWriter createXMLStreamWriter(Writer stream)
			throws XMLStreamException {
		try {
			return new XMLPullStreamWriter(stream);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public XMLStreamWriter createXMLStreamWriter(OutputStream stream)
			throws XMLStreamException {
		try {
			return new XMLPullStreamWriter(stream);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public XMLStreamWriter createXMLStreamWriter(OutputStream stream,
			String encoding) throws XMLStreamException {
		try {
			return new XMLPullStreamWriter(stream, encoding);
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage());
		}
	}

	@Override
	public XMLStreamWriter createXMLStreamWriter(Result result)
			throws XMLStreamException {
		throw new MethodNotImplementedError(this.getClass().getName()
				+ ": createXMLStreamWriter not supported!");
	}

	@Override
	public XMLEventWriter createXMLEventWriter(Result result)
			throws XMLStreamException {
		throw new MethodNotImplementedError(
				"createXMLEventWriter not supported.");
	}

	@Override
	public XMLEventWriter createXMLEventWriter(OutputStream stream)
			throws XMLStreamException {
		throw new MethodNotImplementedError(
				"createXMLEventWriter not supported.");
	}

	@Override
	public XMLEventWriter createXMLEventWriter(OutputStream stream,
			String encoding) throws XMLStreamException {
		throw new MethodNotImplementedError(
				"createXMLEventWriter not supported.");
	}

	@Override
	public XMLEventWriter createXMLEventWriter(Writer stream)
			throws XMLStreamException {
		throw new MethodNotImplementedError(
				"createXMLEventWriter not supported.");
	}

	@Override
	public void setProperty(String name, Object value)
			throws IllegalArgumentException {
		properties.put(name, value);
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return properties.get(name);
	}

	@Override
	public boolean isPropertySupported(String name) {
		return false;
	}

}
