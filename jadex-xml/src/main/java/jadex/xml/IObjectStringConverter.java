package jadex.xml;

/**
 * 
 */
public interface IObjectStringConverter 
{
	/**
	 *  Convert a value to a string type.
	 *  @param val The value to convert.
	 */
	public String convertObject(Object val, IContext context);
}
