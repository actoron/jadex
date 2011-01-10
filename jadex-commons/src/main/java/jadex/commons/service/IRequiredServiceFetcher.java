package jadex.commons.service;

import jadex.commons.IFuture;
import jadex.commons.IIntermediateFuture;

/**
 *  Interface for fetching required services.
 */
public interface IRequiredServiceFetcher
{
	/**
	 *  Get a required service.
	 *  @param info The service info.
	 *  @param provider The provider.
	 *  @param rebind Flag if should be rebound.
	 */
	public IFuture getService(RequiredServiceInfo info, IServiceProvider provider, boolean rebind);
	
	/**
	 *  Get a required multi service.
	 *  @param info The service info.
	 *  @param provider The provider.
	 *  @param rebind Flag if should be rebound.
	 */
	public IIntermediateFuture getServices(RequiredServiceInfo info, IServiceProvider provider, boolean rebind);
}
