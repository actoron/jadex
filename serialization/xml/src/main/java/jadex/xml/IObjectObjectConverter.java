package jadex.xml;

/**
 *  Interface for converters that convert an object to another object.
 */
public interface IObjectObjectConverter
{
	//-------- methods --------
	
	/**
	 *  Convert an object to another object.
	 *  @param val The value to convert.
	 */
	public Object convertObject(Object val, IContext context)	throws Exception;
}
