package jadex.platform.service.registryv2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.registryv2.ISuperpeerClientService;
import jadex.bridge.service.types.registryv2.ISuperpeerService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Boolean3;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;

/**
 *  The super peer client agent is responsible for managing connections to super peers for each network.
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service
public class SuperpeerClientAgent	implements ISuperpeerClientService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The current query future for super peers for a given network (only set while searching for the network). */
	protected Map<String, ISubscriptionIntermediateFuture<ISuperpeerService>>	queries
		= new LinkedHashMap<>();

	/** The current super peer connections for each network (only set when found, i.e. when not searching for the network). */
	protected Map<String, Tuple2<ISuperpeerService, SubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>>>>	superpeers
		= new LinkedHashMap<>();
	
	//-------- agent life cycle --------
	
	/**
	 *  Find and connect to super peers.
	 */
	@AgentCreated
	protected IFuture<Void>	init()
	{
		Future<Void>	ret	= new Future<>();
		ISecurityService	secser	= SServiceProvider.getLocalService(agent, ISecurityService.class);
		secser.getNetworkNames()
			.addResultListener(new ExceptionDelegationResultListener<Set<String>, Void>(ret)
		{
			@Override
			public void customResultAvailable(Set<String> networks) throws Exception
			{
				for(String network: networks)
				{
					startSuperpeerSearch(network);
				}
					
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	//-------- ISuperpeerClientService interface --------
	
	/**
	 *  Subscribes a super peer to the client,
	 *  receiving events of services added to or
	 *  removed from the client.
	 *  
	 *  @param networkname	The relevant network (i.e. only services for this network will be passed to the super peer).
	 *  @param sp	The super peer.
	 *  
	 *  @return Added/removed service events.
	 */
	public ISubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>> addSuperpeerSubscription(String networkname, ISuperpeerService sp)
	{
		SubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>>	sub;

		if(superpeers.containsKey(networkname))
		{
			// Only keep first connection (i.e. assumed fastest/closest server)
			System.out.println(agent+" ignored additional super peer connection for network "+networkname+" from super peer: "+sp);
			sub	= new SubscriptionIntermediateFuture<>(new IllegalStateException("Already connected."));
		}
		
		else
		{
			System.out.println(agent+" received super peer connection for network "+networkname+" from super peer: "+sp);
			
			// Stop ongoing search, if any
			stopSuperpeerSearch(networkname);
			
			sub = new SubscriptionIntermediateFuture<>(new TerminationCommand()
			{
				@Override
				public void terminated(Exception reason)
				{
					// On failure -> just retry
					System.out.println(agent+" super peer connection for network "+networkname+" from super peer "+sp+" failed due to: "+reason);
					startSuperpeerSearch(networkname);
				}
			});
			
			SFuture.avoidCallTimeouts(sub, agent);
			superpeers.put(networkname, new Tuple2<ISuperpeerService, SubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>>>(sp, sub));
			
			// TODO: add initial services

			// TODO: send updates from registry
		}
		
		return sub;
	}
	
	//------- helper methods ---------

	/**
	 *  Find a super peer for a given network.
	 *  Query is automatically restarted on failure.
	 *  @param networkname	The network.
	 */
	protected void startSuperpeerSearch(String networkname)
	{
		// Clean start for new search (e.g. failure recovery).
		stopSuperpeerSubscription(networkname);
		stopSuperpeerSearch(networkname);
		
		ServiceQuery<ISuperpeerService>	query	= new ServiceQuery<>(ISuperpeerService.class, Binding.SCOPE_GLOBAL, null, agent.getComponentIdentifier(), null);
		query.setNetworkNames(networkname);
		
		try
		{
			// Platform already super peer for network?
//			ISuperpeerService	sp	= 
			SServiceProvider.getLocalService(agent, query, true);	// TODO: getLocalService0()?
			
			// no need to store own platform?
//			superpeers.put(network, new Tuple2<ISuperpeerService, SubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>>>(sp, null));
		}
		catch(ServiceNotFoundException snfe)
		{
			System.out.println(agent+" searching for super peers for network "+networkname);
			
			// Not found locally -> Need to choose remote super peer
			ISubscriptionIntermediateFuture<ISuperpeerService>	fut;
			fut	= SServiceProvider.addQuery(agent, query, true);
			queries.put(networkname, fut);
			fut.addResultListener(new IntermediateDefaultResultListener<ISuperpeerService>()
			{
				@Override
				public void intermediateResultAvailable(ISuperpeerService sp)
				{
					System.out.println(agent+" requesting super peer connection for network "+networkname+" from super peer: "+sp);
					sp.registerClient(networkname, SuperpeerClientAgent.this)
						.addResultListener(new IResultListener<Void>()
					{
						@Override
						public void resultAvailable(Void result)
						{
							checkRetry(result);
						}
						
						@Override
						public void exceptionOccurred(Exception exception)
						{
							checkRetry(exception);
						}
					});
				}
				
				@Override
				public void finished()
				{
					checkRetry(null);
				}
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
					checkRetry(exception);
				}						
				
				
				/**
				 *  When query finishes or fails -> check if found, else restart query.
				 */
				protected void	checkRetry(Object reason)
				{
					// Search still valid but ended?
					if(queries.get(networkname)==fut && fut.isDone())
					{
						// On error -> restart search after e.g. 3 secs (realtime) (small delay to prevent busy loop on persistent immediate error)
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(Starter.getScaledRemoteDefaultTimeout(agent.getComponentIdentifier(), 0.1), new IComponentStep<Void>()
						{
							@Override
							public IFuture<Void> execute(IInternalAccess ia)
							{
								// Still no other search started in between?
								if(queries.get(networkname)==fut)
								{
									startSuperpeerSearch(networkname);
								}
								return IFuture.DONE;
							}
						}, true);
					}
				}
			});
			
			
			// For robustness: restart search every e.g. 30 seconds (realtime) until connected (e.g. SP found but initial connection failed -> requires restarting running search to be found again!?)
			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(Starter.getRemoteDefaultTimeout(agent.getComponentIdentifier()), new IComponentStep<Void>()
			{
				@Override
				public IFuture<Void> execute(IInternalAccess ia)
				{
					// Search still valid (i.e. no connection established yet).
					if(queries.get(networkname)==fut)
					{
						startSuperpeerSearch(networkname);
					}
					return IFuture.DONE;
				}
			}, true);
		}
	}

	/**
	 *  Stop an ongoing super peer search for the given network (if any).
	 *  @param networkname
	 */
	private void stopSuperpeerSearch(String networkname)
	{
		if(queries.containsKey(networkname))
		{
			System.out.println(agent+" stopping search for super peers for network: "+networkname);
			ISubscriptionIntermediateFuture<ISuperpeerService>	fut	= queries.remove(networkname);	// Remove before terminate to avoid auto-start of new search on error.
			fut.terminate();
		}
	}
	
	private void stopSuperpeerSubscription(String networkname)
	{
		if(superpeers.containsKey(networkname))
		{
			System.out.println(agent+" dropping super peer connection for network "+networkname+" from super peer: "+superpeers.get(networkname).getFirstEntity());
			superpeers.get(networkname).getSecondEntity().setFinishedIfUndone();
			superpeers.remove(networkname);
		}
	}
	
	/**
	 *  Main for testing.
	 */
	public static void	main(String[] args)
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimalComm();
		config.setGui(true);
//		config.setLogging(true);
		config.addComponent(SuperpeerClientAgent.class);
		config.addComponent(SuperpeerRegistryAgent.class);
		config.setNetworkNames("test");
		config.setNetworkSecrets("test");
		Starter.createPlatform(config, args).get();
	}
}
