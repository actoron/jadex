package jadex.bridge.service.types.registryv2;

import java.util.Collection;

import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Service access to a remote registry.
 */
public interface IRemoteRegistryService
{
	/**
	 *  Search remote registry for a single service.
	 *  
	 *  @param query The search query.
	 *  @return The first matching service or exception if not found.
	 */
	public <T> IFuture<T> searchService(ServiceQuery<T> query);
	
	/**
	 *  Search remote registry for services.
	 *  
	 *  @param query The search query.
	 *  @return The matching services or exception if none are found.
	 */
	public <T> IFuture<Collection<T>> searchServices(ServiceQuery<T> query);
}
