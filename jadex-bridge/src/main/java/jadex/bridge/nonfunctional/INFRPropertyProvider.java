package jadex.bridge.nonfunctional;

import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.future.IFuture;


/**
 * 
 */
public interface INFRPropertyProvider
{
	/**
	 *  Get the required service property provider.
	 *  @param sid The service id.
	 */
	public IFuture<INFMixedPropertyProvider> getRequiredServiceProertyProvider(IServiceIdentifier sid);
}
