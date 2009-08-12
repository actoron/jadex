package jadex.commons.xml.bean;

import jadex.commons.xml.ITypeConverter;

/**
 *  Converts a map entry to its value.
 *  Necessary for reading maps.
 */
public class MapEntryConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The value to convert.
	 */
	public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
	{
		return ((MapEntry)val).getValue();
	}

	/**
	 *  Test if a converter accepts a specific input type.
	 *  @param inputtype The input type.
	 *  @return True, if accepted.
	 */
	public boolean acceptsInputType(Class inputtype)
	{
		return true;
	}
}