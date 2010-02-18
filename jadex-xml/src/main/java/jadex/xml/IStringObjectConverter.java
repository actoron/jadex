package jadex.xml;

/**
 *  Type converter for converting an object to another type. 
 */
public interface IStringObjectConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The value to convert.
	 */
	public Object convertString(String val, IContext context);
}
