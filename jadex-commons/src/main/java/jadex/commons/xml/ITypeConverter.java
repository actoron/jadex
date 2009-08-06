package jadex.commons.xml;

/**
 *  Type converter for converting an object to another type. 
 */
public interface ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The value to convert.
	 */
	public Object convertObject(Object val, Object root, ClassLoader classloader, Object context);
	
	/**
	 *  Test if a converter accepts a specific input type.
	 *  @param inputtype The input type.
	 *  @return True, if accepted.
	 */
	public boolean acceptsInputType(Class inputtype);
}
