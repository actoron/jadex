package jadex.base.gui;

import jadex.commons.Properties;
import jadex.commons.future.IFuture;

/**
 *  Component that allows its properties being saved and restored.
 */
public interface IPropertiesProvider
{
	/**
	 *  Update from given properties.
	 */
	public IFuture setProperties(Properties props);
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture getProperties();
	
	/**
	 *  Reset state to default values.
	 */
	public IFuture resetProperties();
}
