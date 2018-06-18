package jadex.platform.service.registryv2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
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
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;

/**
 *  The super peer client agent is responsible for managing connections to super peers for each network.
 */
@Agent(autoprovide=Boolean3.TRUE)
public class SuperpeerClientAgent	implements ISuperpeerClientService
{
	//-------- attributes --------
	
	/** The current super peer connections for each network. */
	protected	Map<String, Object>	superpeers	= new LinkedHashMap<>();
	
	//-------- agent life cycle --------
	
	/**
	 *  Find and connect to super peers.
	 */
	@AgentCreated
	protected IFuture<Void>	init(IInternalAccess agent)
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
					ServiceQuery<ISuperpeerService>	query	= new ServiceQuery<>(ISuperpeerService.class, null, null, agent.getComponentIdentifier(), null);
					query.setNetworkNames(network);
					
					try
					{
						// Platform already super peer for network?
						ISuperpeerService	sp	= SServiceProvider.getLocalService(agent, query, true);
						superpeers.put(network, sp);						
					}
					catch(ServiceNotFoundException snfe)
					{
						// Not found locally -> Need to choose remote super peer 
						ISubscriptionIntermediateFuture<ISuperpeerService>	fut	= SServiceProvider.addQuery(agent, query, true);
						fut.addResultListener(new IntermediateDefaultResultListener<ISuperpeerService>()
						{
							IIntermediateResultListener<ISuperpeerService>	self	= this;
							
							@Override
							public void intermediateResultAvailable(ISuperpeerService sp)
							{
								sp.registerClient(network, SuperpeerClientAgent.this)
									.addResultListener(new IResultListener<Void>()
								{
									@Override
									public void resultAvailable(Void result)
									{
										checkRetry();
									}
									
									@Override
									public void exceptionOccurred(Exception exception)
									{
										checkRetry();
									}
								});
							}
							
							@Override
							public void finished()
							{
								checkRetry();
							}
							
							@Override
							public void exceptionOccurred(Exception exception)
							{
								checkRetry();
							}						
							
							/**
							 *  When query finishes or fails -> check if found, else restart query.
							 */
							protected void	checkRetry()
							{
								if(superpeers.containsKey(network))
								{
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
											SServiceProvider.addQuery(agent, query, true).addResultListener(self);
											return IFuture.DONE;
										}
									}, true);
								}
							}
						});
					}
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
		superpeers.put(networkname, sp);
		return null; //TODO
	}
}
