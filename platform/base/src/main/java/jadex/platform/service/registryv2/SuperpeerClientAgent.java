package jadex.platform.service.registryv2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.registryv2.ISuperpeerService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Boolean3;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;

/**
 *  The super peer client agent is responsible for managing connections to super peers for each network.
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service
public class SuperpeerClientAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The current query future for super peers for a given network (only set while searching for the network). */
	protected Map<String, ISubscriptionIntermediateFuture<ISuperpeerService>>	queries
		= new LinkedHashMap<>();

	/** The current super peer connections for each network (only set when found, i.e. when not searching for the network). */
	protected Map<String, Tuple2<ISuperpeerService, ITerminableFuture<Void>>>	superpeers
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
					ITerminableFuture<Void>	fut	= sp.registerClient(networkname);
					fut.addResultListener(new IFutureCommandResultListener<Void>()
					{
						@Override
						public void commandAvailable(Object command)
						{
							// First command -> connected (shouldn't be any other commands).
							System.out.println(agent+" received super peer connection for network "+networkname+" from super peer: "+sp);
							
							// Stop ongoing search, if any
							stopSuperpeerSearch(networkname);
							superpeers.put(networkname, new Tuple2<ISuperpeerService, ITerminableFuture<Void>>(sp, fut));
							
							// TODO: add initial services

							// TODO: send updates from registry	
						}	
						
						@Override
						public void resultAvailable(Void result)
						{
							// On failure -> just retry
							System.out.println(agent+" super peer connection for network "+networkname+" from super peer "+sp+" finished. (why?)");
							startSuperpeerSearch(networkname);
						}
						
						@Override
						public void exceptionOccurred(Exception exception)
						{
							// On failure -> just retry
							System.out.println(agent+" super peer connection for network "+networkname+" from super peer "+sp+" failed due to: "+exception);
							startSuperpeerSearch(networkname);
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
			superpeers.get(networkname).getSecondEntity().terminate();
			superpeers.remove(networkname);
		}
	}
	
	/**
	 *  Main for testing.
	 */
	public static void	main(String[] args)
	{
		// Common base configuration
		IPlatformConfiguration	baseconfig	= PlatformConfigurationHandler.getMinimalComm();
//		baseconfig.setGui(true);
		baseconfig.setLogging(true);
		
		// Super peer base configuration
		IPlatformConfiguration	spbaseconfig	= baseconfig.clone();
		spbaseconfig.addComponent(SuperpeerRegistryAgent.class);
		
		IPlatformConfiguration	config;
		
		// Super peer AB
		config	= spbaseconfig.clone();
		config.setPlatformName("SPAB_*");
		config.setNetworkNames("network-a", "network-b");
		config.setNetworkSecrets("secret-a", "secret-b");
		Starter.createPlatform(config, args).get();
		
		// Super peer BC
		config	= spbaseconfig.clone();
		config.setPlatformName("SPBC_*");
		config.setNetworkNames("network-c", "network-b");
		config.setNetworkSecrets("secret-c", "secret-b");
		Starter.createPlatform(config, args).get();

		// Client ABC
		config	= baseconfig.clone();
		config.addComponent(SuperpeerClientAgent.class);
		config.setPlatformName("ClientABC_*");
		config.setNetworkNames("network-a", "network-b", "network-c");
		config.setNetworkSecrets("secret-a", "secret-b", "secret-c");
		Starter.createPlatform(config, args).get();
	}
}
