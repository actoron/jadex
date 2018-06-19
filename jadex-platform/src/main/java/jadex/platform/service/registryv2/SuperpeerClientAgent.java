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
import jadex.commons.future.IIntermediateResultListener;
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
public class SuperpeerClientAgent	implements ISuperpeerClientService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The current super peer connections for each network. */
	protected	Map<String, Tuple2<ISuperpeerService, SubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>>>>	superpeers	= new LinkedHashMap<>();
	
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
					findSuperpeer(network);
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
		System.out.println(agent+" received super peer connection for network "+networkname+" from super peer: "+sp);
		
		// In case of multiple subscriptions -> only retain last
		clearSuperpeer(networkname);
		
		SubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>>	sub
			= new SubscriptionIntermediateFuture<>(new TerminationCommand()
		{
			@Override
			public void terminated(Exception reason)
			{
				// On failure -> just retry
				findSuperpeer(networkname);
			}
		});
		
		SFuture.avoidCallTimeouts(sub, agent);
		superpeers.put(networkname, new Tuple2<ISuperpeerService, SubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>>>(sp, sub));
		
		// TODO: add initial services
		
		return sub;
	}
	
	//------- helper methods ---------

	/**
	 *  Find a super peer for a given network.
	 *  @param networkname	The network.
	 */
	protected void clearSuperpeer(String networkname)
	{
		Tuple2<ISuperpeerService, SubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>>>	tup
			= superpeers.get(networkname);
		
		if(tup!=null)
		{
			System.out.println(agent+" dropping super peer connection for network "+networkname+" from super peer: "+tup.getFirstEntity());
			tup.getSecondEntity().setFinishedIfUndone();
			superpeers.remove(networkname);
		}
	}
	
	/**
	 *  Find a super peer for a given network.
	 *  @param networkname	The network.
	 */
	protected void findSuperpeer(String networkname)
	{
		// Clean start for new search (e.g. failure recovery).
		clearSuperpeer(networkname);
		
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
			ISubscriptionIntermediateFuture<ISuperpeerService>	fut	= SServiceProvider.addQuery(agent, query, true);
			fut.addResultListener(new IntermediateDefaultResultListener<ISuperpeerService>()
			{
				IIntermediateResultListener<ISuperpeerService>	self	= this;
				
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
					if(superpeers.containsKey(networkname))
					{
						// Stop search if connection was established.
						System.out.println(agent+" stopped searching for super peers for network "+networkname);
						fut.terminate();
					}
					else
					{
						// On error -> retry after 5 secs (realtime)
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(5000, new IComponentStep<Void>()
						{
							@Override
							public IFuture<Void> execute(IInternalAccess ia)
							{
								System.out.println(agent+" retrying searching for super peers for network "+networkname+" due to: "+reason);
								SServiceProvider.addQuery(agent, query, true).addResultListener(self);
								return IFuture.DONE;
							}
						}, true);
					}
				}
			});
		}
	}
	
	/**
	 *  Main for testing.
	 */
	public static void	main(String[] args)
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimalComm();
		config.setGui(true);
		config.addComponent(SuperpeerClientAgent.class);
//		config.setNetworkNames("test");
//		config.setNetworkSecrets("test");
		Starter.createPlatform(config, args).get();
	}
}
