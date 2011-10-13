package jadex.bridge.service;

import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

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
	public <T> IFuture<T> getService(RequiredServiceInfo<T> info, RequiredServiceBinding binding, boolean rebind);
	
	/**
	 *  Get a required multi service.
	 *  @param info The service info.
	 *  @param provider The provider.
	 *  @param rebind Flag if should be rebound.
	 */
	public <T> IIntermediateFuture<T> getServices(RequiredServiceInfo<T> info, RequiredServiceBinding binding, boolean rebind);
}
