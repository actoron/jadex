package jadex.commons.service;

import jadex.commons.IFuture;
import jadex.commons.IIntermediateFuture;

/**
 * 
 */
public interface IRequiredServiceFetcher
{
	/**
	 *  Get a required service.
	 */
	public IFuture getService(RequiredServiceInfo info, IServiceProvider provider);
	
	/**
	 *  Get a required multi service.
	 */
	public IIntermediateFuture getServices(RequiredServiceInfo info, IServiceProvider provider);
}
