package jadex.xml;

/**
 * 
 */
public interface IObjectObjectConverter
{
	/**
	 *  Convert an object to another object.
	 *  @param val The value to convert.
	 */
	public Object convertObject(Object val, IContext context);
}
