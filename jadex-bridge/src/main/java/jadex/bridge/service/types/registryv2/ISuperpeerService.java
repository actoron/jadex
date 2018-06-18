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
	 *  (super peer will perform a subscription callback).
	 *  
	 *  @param networkname	Network name for callback. 
	 *  @param client	Subscription service for callback. 
	 *  
	 *  @return Null, when registration is complete.
	 */
	// TODO: replace ping pong with Channel (i.e. bidirectional subscription future)
	public IFuture<Void> registerClient(String networkname, ISuperpeerClientService client);
	
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
