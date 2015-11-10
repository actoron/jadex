package jadex.xml.stax;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jadex.xml.reader.IXMLReader;

/**
 *  Wrapper for the Java stax interface.
 *
 */
public class StaxReaderWrapper implements IXMLReader
{
	/** The stream. */
	protected BufferedInputStream bis;
	
	/** The wrapped reader. */
	protected XMLStreamReader reader;
	
	/** The current tag. */
	protected LinkedList<XmlTag> tagstack = new LinkedList<XmlTag>();
	
	/** The last tag that was closed. */
	protected XmlTag closedtag;
	
	/** The current attributes. */
	Map<String, String> attrs;
	
	public StaxReaderWrapper(InputStream in)
	{
		bis = new BufferedInputStream(in);
		XMLInputFactory fac = XMLInputFactory.newInstance(); 
		try
		{
			reader = fac.createXMLStreamReader(bis);
		}
		catch (XMLStreamException e)
		{
			throw new RuntimeException(e);
		}
	}

	public StaxReaderWrapper(XMLStreamReader reader) {
		this.reader = reader;
	}
	
	/**
	 *  Gets the XML event type.
	 *  
	 *  @return Event type.
	 */
	public int getEventType()
	{
		return reader.getEventType();
	}
	
	/**
	 *  Returns if the reader has more events.
	 *  
	 *  @return True, if there are more events.
	 */
	public boolean hasNext()
	{
		try
		{
			return reader.hasNext();
		}
		catch (XMLStreamException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Selects the next event.
	 */
	public int next()
	{
		try
		{
			int next = reader.next();

			if (next == XMLStreamConstants.START_ELEMENT && reader.getName() != null)
			{
				QName qname = reader.getName();
				tagstack.push(new XmlTag(qname.getNamespaceURI(), qname.getLocalPart()));
			}
			
			if (next == XMLStreamConstants.END_ELEMENT && reader.getName() != null)
			{
				XmlTag tag = tagstack.peek();
				if (tag != null)
				{
					if (tag.getNamespace().equals(reader.getName().getNamespaceURI()) &&
						tag.getLocalPart().equals(reader.getLocalName()))
					{
						closedtag = tagstack.pop();
					}
				}
			}
	    	
	    	if (next == XMLStreamConstants.START_ELEMENT &&
	    		reader.getAttributeCount() > 0)
	    	{
	    		attrs = new HashMap<String, String>(reader.getAttributeCount());
		    	for (int i = 0; i < reader.getAttributeCount(); ++i)
		    	{
		    		attrs.put(reader.getAttributeLocalName(i), XmlUtil.unescapeString(reader.getAttributeValue(i)));
		    	}
	    	}
	    	else
	    	{
	    		attrs = null;
	    	}
		}
		catch (XMLStreamException e)
		{
			throw new RuntimeException(e);
		}

		return getEventType();
	}
	
	/**
	 *  Get the XML tag struct.
	 *  
	 *  @return Struct defining the tag.
	 */
	public XmlTag getXmlTag()
	{
		return tagstack.peek();
	}
	
	/**
	 *  Get the XML tag struct of the last closed tag.
	 *  
	 *  @return Struct defining the tag.
	 */
	public XmlTag getClosedTag()
	{
		return closedtag;
	}
	
	/**
	 *  Get the XML tag stack.
	 *  
	 *  @return Stack defining the tags.
	 */
	public LinkedList<XmlTag> getXmlTagStack()
	{
		return tagstack;
	}
	
	/**
	 *  Returns the attributes.
	 *  
	 *  @return The attributes.
	 */
	public Map<String, String> getAttributes()
	{
		return attrs;
	}
	
	/**
	 *  Get the text for the element.
	 *  
	 *  @return The text.
	 */
	public String getText()
	{
		return reader.getText();
	}
	
	/**
	 *  Closes the reader.
	 */
	public void close()
	{
		try
		{
			reader.close();
		}
		catch (XMLStreamException e)
		{
		}
		try
		{
			if (bis != null) {
				bis.close();
			}
		}
		catch (IOException e)
		{
		}
	}

	/**
	 * Returns the current parser location.
	 * @return Location
	 */
	public ILocation getLocation()
	{
		return StaxLocationWrapper.fromLocation(reader.getLocation());
	}

	public String getLocalName() {
		return reader.getLocalName();
	}

	public int getAttributeCount() {
		return reader.getAttributeCount();
	}

	public String getAttributeLocalName(int i) {
		return reader.getAttributeLocalName(i);
	}

	public String getAttributeValue(int i) {
		return reader.getAttributeValue(i);
	}

	public jadex.xml.stax.QName getName() {
		QName name = reader.getName();
		return new jadex.xml.stax.QName(name.getNamespaceURI(), name.getLocalPart(), name.getPrefix());
	}

	public String getAttributePrefix(int i) {
		return reader.getAttributePrefix(i);
	}

	public String getAttributeNamespace(int i) {
		return reader.getAttributeNamespace(i);
	}
}