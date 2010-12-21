package jadex.commons.service;

import jadex.commons.IFuture;

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
	public IFuture getServices(RequiredServiceInfo info, IServiceProvider provider);
}
