package jadex.platform.service.registryv2;

import java.util.logging.Level;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.registryv2.ISuperpeerService;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
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
	
	/** The superpeer service registry */
	protected IServiceRegistry serviceregistry = new ServiceRegistry();
	
	/**
	 *  Initiates the client registration procedure
	 *  (super peer will answer initially with an empty intermediate result,
	 *  client will send updates with backward commands).
	 *  
	 *  @param networkname	Network for this connection. 
	 *  
	 *  @return Does not return any more results while connection is running.
	 */
	// TODO: replace internal commands with typed channel (i.e. bidirectional / reverse subscription future)
	// TODO: network name required for server?
	public ISubscriptionIntermediateFuture<Void> registerClient(String networkname)
	{
		IComponentIdentifier	client	= ServiceCall.getCurrentInvocation().getCaller();
		System.out.println(ia+": Initiating super peer connection with client "+client+" for network "+networkname);
		
		SubscriptionIntermediateFuture<Void>	ret	= new SubscriptionIntermediateFuture<>(new TerminationCommand()
		{
			@Override
			public void terminated(Exception reason)
			{
				System.out.println(ia+": Super peer connection with client "+client+" for network "+networkname+" terminated due to "+reason);
			}
		});
		
		SFuture.avoidCallTimeouts(ret, ia);
		
		// Initial register-ok response
		ret.addIntermediateResult(null);
		
		// TODO: listen for changes and add new services locally.
		ret.addBackwardCommand(new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				return obj instanceof ServiceEvent;
			}
		}, new ICommand<Object>()
		{
			@SuppressWarnings("unchecked")
			public void execute(Object obj)
			{
				ServiceEvent<IServiceIdentifier> event = (ServiceEvent<IServiceIdentifier>) obj;
				switch(event.getType())
				{
					case ServiceEvent.SERVICE_ADDED:
						System.out.println("ADDING SERVICE: " + event.getService());
						serviceregistry.addService(event.getService());
						break;
						
					case ServiceEvent.SERVICE_CHANGED:
						serviceregistry.updateService(event.getService());
						break;
						
					case ServiceEvent.SERVICE_REMOVED:
						serviceregistry.removeService(event.getService());
						break;
						
					default:
						ia.getLogger().log(Level.SEVERE, "Unknown ServiceEvent: " + event.getType());
				}
				
			}
		});

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
}
