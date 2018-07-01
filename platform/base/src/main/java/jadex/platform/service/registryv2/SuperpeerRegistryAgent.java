package jadex.platform.service.registryv2;

import jadex.bridge.SFuture;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.registryv2.ISuperpeerService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminationCommand;
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
	 *  (super peer will answer initially with a forward command,
	 *  client will send updates with backward commands).
	 *  
	 *  @param networkname	Network for this connection. 
	 *  
	 *  @return Does not return while connection is running.
	 */
	// TODO: replace internal commands with typed channel (i.e. bidirectional / reverse subscription future)
	// TODO: network name required for server?
	public ITerminableFuture<Void> registerClient(String networkname)
	{
		TerminableFuture<Void>	ret	= new TerminableFuture<>(new TerminationCommand()
		{
			@Override
			public void terminated(Exception reason)
			{
				System.out.println("Super peer connection terminated: "+reason);
			}
		});
		
		SFuture.avoidCallTimeouts(ret, ia);
		
		// Initial register-ok response
		ret.sendForwardCommand(null);

		// TODO: listen for changes and add new services locally.

		// TODO: when connection is lost, remove all services and queries from client.
		
		return ret;
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
