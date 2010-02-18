package jadex.xml;

/**
 *  Type converter for converting a string to another type. 
 */
public interface IStringObjectConverter
{
	//-------- methods --------
	
	/**
	 *  Convert a string value to another type.
	 *  @param val The value to convert.
	 */
	public Object convertString(String val, IContext context);
}
