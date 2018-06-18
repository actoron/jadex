package jadex.bridge.service.types.registryv2;

import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Service for service registry superpeers.
 *
 */
public interface ISuperpeerService extends IRemoteRegistryService
{
	/**
	 *  Initiates the client registration procedure
	 *  (superpeer will perform a subscription callback)
	 *  
	 *  @return Null, when registration is complete.
	 */
	public IFuture<Void> registerClient();
	
	/**
	 *  Add a service query to the registry.
	 *  
	 *  @param query The service query.
	 *  @return Subscription to matching services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query);
	
	/**
	 *  Removes a service query from the registry.
	 *  
	 *  @param query The service query.
	 *  @return Null, when done.
	 */
	public <T> IFuture<Void> removeQuery(ServiceQuery<T> query);
}
