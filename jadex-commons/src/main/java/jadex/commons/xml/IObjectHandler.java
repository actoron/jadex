package jadex.commons.xml;

import java.util.List;

/**
 *  Interface for object handler.
 *  Is called when a tag start is found and an object could be created.
 *  Is called when an end tag is found and an object could be linked to its parent.
 */
public interface IObjectHandler
{
	/**
	 *  Create an object for the current tag.
	 *  @param type The object type to create.
	 *  @param root Flag, if object should be root object.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(Object type, boolean root, Object context, ClassLoader classloader) throws Exception;
	
	/**
	 *  Handle the attribute of an object.
	 *  @param object The object.
	 *  @param attrname The attribute name.
	 *  @param attrval The attribute value.
	 *  @param attrinfo The attribute info.
	 *  @param context The context.
	 */
	public void handleAttributeValue(Object object, String attrname, List attrpath, Object attrval, 
		Object attrinfo, Object context, ClassLoader classloader) throws Exception;
	
	/**
	 *  Link an object to its parent.
	 *  @param object The object.
	 *  @param parent The parent object.
	 *  @param linkinfo The link info.
	 *  @param tagname The current tagname (for name guessing).
	 *  @param context The context.
	 */
	public void linkObject(Object object, Object parent, Object linkinfo, String tagname, 
		Object context, ClassLoader classloader) throws Exception;
}
