package jadex.bdi.runtime;

/**
 *  The interface for accessing properties.
 */
public interface IPropertybase extends IElement
{
	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property value.
	 */
	public Object getProperty(String name);

	/**
	 *  Get all properties.
	 *  @return An array of property names.
	 */
	public String[] getPropertyNames();
}
