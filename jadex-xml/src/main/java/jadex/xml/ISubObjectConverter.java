package jadex.xml;

/**
 * 
 */
public interface ISubObjectConverter
{
	/**
	 *  Convert an object to another object.
	 *  @param val The value to convert.
	 */
	public Object convertObjectForRead(Object val, IContext context);
	
	/**
	 *  Convert an object to another object.
	 *  @param val The value to convert.
	 */
	public Object convertObjectForWrite(Object val, IContext context);
}
