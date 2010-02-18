package jadex.xml;

/**
 *  Interface for converters that convert an object to a string.
 */
public interface IObjectStringConverter 
{
	//-------- methods --------
	
	/**
	 *  Convert a value to a string type.
	 *  @param val The value to convert.
	 */
	public String convertObject(Object val, IContext context);
}
