package jadex.bridge;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IComponentFactoryExtensionService
{
	/**
	 *  Get extension. 
	 */
	public IFuture getExtension(String componenttype);
	
}
