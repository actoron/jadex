package jadex.bridge.nonfunctional;

import jadex.commons.future.IFuture;


/**
 *  Interface for required service proxies. Allows for accessing
 *  non-functional properties of required services.
 */
public interface INFRPropertyProvider
{
	/**
	 *  Get the required service property provider.
	 *  @param sid The service id.
	 */
	public IFuture<INFMixedPropertyProvider> getRequiredServicePropertyProvider();
}
