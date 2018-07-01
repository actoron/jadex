package jadex.bridge.service.types.registryv2;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;

/**
 *  Service for service registry superpeers.
 *
 */
@Service(system=true)
public interface ISuperpeerService extends IRemoteRegistryService
{
	/**
	 *  Initiates the client registration procedure
	 *  (super peer will answer initially with a forward command,
	 *  client will send updates with backward commands).
	 *  
	 *  @param networkname	Network for this connection. 
	 *  
	 *  @return Does not return while connection is running.
	 */
	// TODO: replace internal commands with typed channel (i.e. bidirectional / reverse subscription future)
	// TODO: network name required for server?
	public ITerminableFuture<Void> registerClient(String networkname);
	
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
