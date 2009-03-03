package jadex.commons.xml;

import java.util.List;

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
	 *  @param comment The comment.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(XMLStreamReader parser, String comment, Object context, List stack) throws Exception;
	
	/**
	 *  Handle content for an object.
	 *  @param parser The parser.
	 *  @param comment The comment.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public void handleContent(XMLStreamReader parser, Object elem, String content, Object context, List stack) throws Exception;
	
	/**
	 *  Link an object to its parent.
	 *  @param parser The parser.
	 *  @param elem The element.
	 *  @param parent The parent element.
	 *  @param context The context.
	 */
	public void linkObject(XMLStreamReader parser, Object elem, Object parent, Object context, List stack) throws Exception;
}
