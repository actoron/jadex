package jadex.commons.staxwrapper;

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

/**
 *  Wrapper for the Java stax interface.
 *
 */
public class StaxReaderWrapper implements IStaxReaderWrapper
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
	public void next()
	{
		try
		{
			reader.next();
			
			if (reader.getEventType() == XMLStreamConstants.START_ELEMENT && reader.getName() != null)
			{
				QName qname = reader.getName();
				tagstack.push(new XmlTag(qname.getNamespaceURI(), qname.getLocalPart()));
			}
			
			if (reader.getEventType() == XMLStreamConstants.END_ELEMENT && reader.getName() != null)
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
	    	
	    	if (reader.getEventType() == XMLStreamConstants.START_ELEMENT &&
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
			bis.close();
		}
		catch (IOException e)
		{
		}
	}
}
