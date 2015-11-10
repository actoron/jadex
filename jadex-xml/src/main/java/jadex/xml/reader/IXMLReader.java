package jadex.xml.reader;

import java.io.*;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Map;

import jadex.xml.stax.ILocation;
import jadex.xml.stax.QName;
import jadex.xml.stax.XmlTag;

public interface IXMLReader
{
	/**
	 *  Gets the XML event type.
	 *  
	 *  @return Event type.
	 */
	public int getEventType();
	
	/**
	 *  Returns if the reader has more events.
	 *  
	 *  @return True, if there are more events.
	 */
	public boolean hasNext();
	
	/**
	 *  Selects the next event.
	 */
	public int next();
	
	/**
	 *  Get the XML tag struct.
	 *  
	 *  @return Struct defining the tag.
	 */
	public XmlTag getXmlTag();
	
	/**
	 *  Get the XML tag struct of the last closed tag.
	 *  
	 *  @return Struct defining the tag.
	 */
	public XmlTag getClosedTag();
	
	/**
	 *  Get the XML tag stack.
	 *  
	 *  @return Stack defining the tags.
	 */
	public LinkedList<XmlTag> getXmlTagStack();
	
	/**
	 *  Returns the attributes.
	 *  
	 *  @return The attributes.
	 */
	public Map<String, String> getAttributes();
	
	/**
	 *  Get the text for the element.
	 *  
	 *  @return The text.
	 */
	public String getText();
	
	/**
	 *  Closes the reader.
	 */
	public void close();

	/**
	 * Returns the current parser location.
	 * @return Location
	 */
	public ILocation getLocation();

	String getLocalName();

	int getAttributeCount();

	String getAttributeLocalName(int i);

	String getAttributeValue(int i);

	QName getName();

	String getAttributePrefix(int i);

	String getAttributeNamespace(int i);

}
