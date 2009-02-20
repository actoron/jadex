package jadex.adapter.base.appdescriptor.reader;

import javax.xml.stream.XMLStreamReader;

/**
 *  Interface for object handler.
 *  Is called when a tag start is found and an object could be created.
 *  Is called when an end tag is found and an object could be linked to its parent.
 */
public interface IObjectHandler
{
	/**
	 *  Create an object for the current tag.
	 *  @param parser The parser.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(XMLStreamReader parser) throws Exception;
	
	/**
	 *  Link an object to its parent.
	 *  @param parser The parser.
	 *  @param elem The element.
	 *  @param paranet The parent element.
	 */
	public void linkObject(XMLStreamReader parser, Object elem, Object parent) throws Exception;
}
