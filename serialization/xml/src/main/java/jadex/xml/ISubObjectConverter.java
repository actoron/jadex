package jadex.xml;

/**
 *  Converter for subobjects. Consist of two methods for object-object conversion.
 */
public interface ISubObjectConverter
{
	//-------- methods --------
	
	/**
	 *  Convert an object to another object.
	 *  @param val The value to convert.
	 */
	public Object convertObjectForRead(Object val, IContext context) throws Exception;
	
	/**
	 *  Convert an object to another object.
	 *  @param val The value to convert.
	 */
	public Object convertObjectForWrite(Object val, IContext context) throws Exception;
}
