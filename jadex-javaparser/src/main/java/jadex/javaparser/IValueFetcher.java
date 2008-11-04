package jadex.javaparser;

/**
 *  Interface for fetching a predefined value.
 *  The parser uses an IValueFetcher to retrieve
 *  predefined values such as "$val".
 *  
 *  This interfaces is better than a value map because
 *  values can 
 */
public interface IValueFetcher
{
	/**
	 *  Fetch a value via its name.
	 *  @param name The name.
	 *  @return The value.
	 */
	public Object fetchValue(String name);
	
	/**
	 *  Fetch a value via its name from an object.
	 *  @param name The name.
	 *  @param object The object.
	 *  @return The value.
	 */
	public Object fetchValue(String name, Object object);
}
