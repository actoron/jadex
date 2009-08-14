package jadex.commons.xml.writer;

import java.util.Iterator;

import jadex.commons.xml.TypeInfo;

/**
 *  Interface for an object writer handler.
 *  Has the task to generate write information for an object.
 */
public interface IObjectWriterHandler
{
	/**
	 *  Get the tag name for an object.
	 */
	public String getTagName(Object object, Object context);
	
	/**
	 *  Get the object type
	 *  @param object The object.
	 *  @return The object type.
	 */
//	public Object getObjectType(Object object, Object context);
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	public TypeInfo getTypeInfo(Object object, String[] fullpath, Object context);
	
	/**
	 *  Get all subobjects of an object.
	 *  @param object The object.
	 *  @param typeinfo The Typeinfo.
	 */
	public WriteObjectInfo getObjectWriteInfo(Object object, TypeInfo typeinfo, Object context, ClassLoader classloader);
}
