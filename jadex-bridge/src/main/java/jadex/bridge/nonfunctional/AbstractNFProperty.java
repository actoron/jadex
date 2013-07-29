package jadex.bridge.nonfunctional;

/**
 *  A non-functional property.
 *  
 *  NOTE: Implementing classes must implement a constructor with
 *  the signature INFProperty(String name) to allow the service
 *  to initialize the property during creation.
 */
public abstract class AbstractNFProperty<T extends Object, U extends Object> implements INFProperty<T, U>
{
	/** Name of the property. */
	protected String name;
	
	/**
	 *  Creates the property.
	 * 
	 *  @param name Name of the property.
	 */
	public AbstractNFProperty(String name)
	{
		this.name = name;
	}
	
	/**
	 *  Gets the name of the property.
	 *
	 *  @return The name of the property.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Returns the current value of the property.
	 *  
	 *  @param type Type of the value.
	 *  @return The current value of the property.
	 */
	public T getValue(Class<T> type)
	{
		return getValue(type, null);
	}
}
