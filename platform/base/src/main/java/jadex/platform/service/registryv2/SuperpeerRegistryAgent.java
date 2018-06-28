package jadex.platform.service.registryv2;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.registryv2.ISuperpeerClientService;
import jadex.bridge.service.types.registryv2.ISuperpeerService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Super peer collects services from client and answers search requests and queries.
 */
// Dummy implementation for now.
@Agent
@Service
@ProvidedServices(replace=true,
	value=@ProvidedService(type=ISuperpeerService.class, scope=Binding.SCOPE_GLOBAL))
public class SuperpeerRegistryAgent	extends RemoteRegistryAgent	implements ISuperpeerService
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
	public IFuture<Void> registerClient(String networkname, ISuperpeerClientService client)
	{
//		ISubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>>	fut	=
			client.addSuperpeerSubscription(networkname, this);
		
		// TODO: listen for changes and add new services locally.

		// TODO: when connection is lost, remove all services and queries from client.
		
		return IFuture.DONE;
	}
	
	/**
	 *  Add a service query to the registry.
	 *  
	 *  @param query The service query.
	 *  @return Subscription to matching services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query)
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented...");
	}
	
	/**
	 *  Removes a service query from the registry.
	 *  
	 *  @param query The service query.
	 *  @return Null, when done.
	 */
	public <T> IFuture<Void> removeQuery(ServiceQuery<T> query)
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented...");
	}
}
