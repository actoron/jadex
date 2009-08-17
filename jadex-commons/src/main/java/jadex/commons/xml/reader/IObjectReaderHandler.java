package jadex.commons.xml.reader;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 *  Interface for object reader handler.
 *  Is called when a tag start is found and an object could be created.
 *  Is called when an end tag is found and an object could be linked to its parent.
 */
public interface IObjectReaderHandler
{
	/**
	 *  Create an object for the current tag.
	 *  @param type The object type to create.
	 *  @param root Flag, if object should be root object.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(Object typeinfo, boolean root, Object context, Map rawattributes, ClassLoader classloader) throws Exception;
	
	/**
	 *  Convert a content string object to another type of object.
	 */
	public Object convertContentObject(Object object, QName tag, Object context, ClassLoader classloader);
	
	/**
	 *  Handle the attribute of an object.
	 *  @param object The object.
	 *  @param xmlattrname The attribute name.
	 *  @param attrval The attribute value.
	 *  @param attrinfo The attribute info.
	 *  @param context The context.
	 */
	public void handleAttributeValue(Object object, String xmlattrname, List attrpath, String attrval, 
		Object attrinfo, Object context, ClassLoader classloader, Object root) throws Exception;
	
	/**
	 *  Link an object to its parent.
	 *  @param object The object.
	 *  @param parent The parent object.
	 *  @param linkinfo The link info.
	 *  @param tagname The current tagname (for name guessing).
	 *  @param context The context.
	 */
	public void linkObject(Object object, Object parent, Object linkinfo, QName[] pathname, 
		Object context, ClassLoader classloader, Object root) throws Exception;
}
