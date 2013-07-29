package jadex.bridge.nonfunctional;

/**
 *  A non-functional property.
 *  
 *  NOTE: Implementing classes must implement a constructor with
 *  the signature INFProperty(String name) to allow the service
 *  to initialize the property during creation.
 */
public interface INFProperty<T extends Object, U extends Object>
{
	/**
	 *  Gets the name of the property.
	 *
	 *  @return The name of the property.
	 */
	public String getName();
	
	/**
	 *  Returns the meta information about the property.
	 *  
	 *  @return The meta information about the property.
	 */
	public INFPropertyMetaInfo getMetaInfo();
	
	/**
	 *  Returns the current value of the property.
	 *  
	 *  @param type Type of the value.
	 *  @return The current value of the property.
	 */
	public T getValue(Class<T> type);
	
	/**
	 *  Returns the current value of the property, performs unit conversion if necessary.
	 *  
	 *  @param type Type of the value.
	 *  @param unit Unit of the returned value.
	 *  
	 *  @return The current value of the property.
	 */
	public T getValue(Class<T> type, Class<U> unit);
}
