package jadex.bdi.runtime;

import jadex.commons.IFuture;

/**
 *  The interface for accessing properties.
 */
public interface IEAPropertybase extends IEAElement
{
	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property value.
	 */
	public IFuture getProperty(String name);

	/**
	 *  Get all properties.
	 *  @return An array of property names.
	 */
	public IFuture getPropertyNames();
}

