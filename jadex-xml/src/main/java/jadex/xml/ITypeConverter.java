package jadex.xml;

/**
 *  Type converter for converting an object to another type. 
 */
public interface ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The value to convert.
	 */
	// todo: remove root parameter
	public Object convertObject(Object val, Object root, ClassLoader classloader, Object context);
	
	/**
	 *  Test if a converter accepts a specific input type.
	 *  @param inputtype The input type.
	 *  @return True, if accepted.
	 */
	// todo: remove or change signature to Object (OAV)
//	public boolean acceptsInputType(Class inputtype);
}
