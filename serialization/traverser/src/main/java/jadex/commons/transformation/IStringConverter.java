package jadex.commons.transformation;

/**
 *  Convert to and from a string.
 */
public interface IStringConverter 
{
	/**
	 *  Convert a string to an object.
	 *  @param val The string.
	 *  @param type The target type.
	 *  @param context The context.
	 *  @return The object.
	 */
	public Object convertString(String val, Class<?> type, ClassLoader cl, Object context);
	
	/**
	 *  Convert an object to a string.
	 *  @param val The object.
	 *  @param type The encoding type.
	 *  @param context The context.
	 *  @return The object.
	 */
	public String convertObject(Object val, Class<?> type, ClassLoader cl, Object context); // String format = xml, json, plain?!
	
	/**
	 *  Get the type of string that can be processed (xml, json, plain).
	 *  @return The object.
	 */
	public String getType();
}
