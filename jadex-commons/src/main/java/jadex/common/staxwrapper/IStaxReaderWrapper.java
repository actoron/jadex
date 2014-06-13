package jadex.common.staxwrapper;

import java.util.Map;

public interface IStaxReaderWrapper
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
	public void next();
	
	/**
	 *  Get the XML tag struct.
	 *  
	 *  @return Struct defining the tag.
	 */
	public XmlTag getXmlTag();
	
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
}
