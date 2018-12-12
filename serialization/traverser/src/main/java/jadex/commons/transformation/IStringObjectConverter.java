package jadex.commons.transformation;


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
//	public Object convertString(String val, IContext context)	throws Exception;
	public Object convertString(String val, Object context)	throws Exception;
}
