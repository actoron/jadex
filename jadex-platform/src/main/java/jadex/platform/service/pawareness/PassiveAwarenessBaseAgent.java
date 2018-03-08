package jadex.platform.service.pawareness;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.pawareness.IPassiveAwarenessService;
import jadex.commons.Boolean3;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;

/**
 *  Base agent for all passive awareness implementations (discovery and management).
 *  Adds addresses to transport service and collects and notifies about new platforms.
 */
// TODO: passively remove platforms based on validity durations
@Service
@Agent(autoprovide=Boolean3.TRUE)
public class PassiveAwarenessBaseAgent	implements IPassiveAwarenessService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The current search, if any. */
	protected IntermediateFuture<IComponentIdentifier>	search;
	
	/** The subscriptions. */
	protected Set<SubscriptionIntermediateFuture<IComponentIdentifier>>	subscriptions;
	
	/** The currently known platforms. */
	protected Set<IComponentIdentifier>	platforms;
	
	//-------- agent lifecycle --------
	
	/**
	 *  Start the service.
	 *  @throws Exception, e.g. when required socket can not be opened. 
	 */
	@ServiceStart
	public void	start() throws Exception
	{
		subscriptions	= new LinkedHashSet<SubscriptionIntermediateFuture<IComponentIdentifier>>();
		platforms	= new LinkedHashSet<IComponentIdentifier>();
	}
	
	/**
	 *  Stop the service.
	 */
	@ServiceShutdown
	protected void	shutdown()
	{
		for(SubscriptionIntermediateFuture<IComponentIdentifier> sub: subscriptions)
		{
			// Undone, because might be cancelled already, but not yet removed.
			sub.setFinishedIfUndone();
		}
	}
	
	//-------- IPassiveAwarenessService --------
	
	/**
	 *  Try to find other platforms and finish after timeout.
	 *  Immediately returns known platforms and concurrently issues a new search, waiting for replies until the timeout.
	 */
	public IIntermediateFuture<IComponentIdentifier>	searchPlatforms()
	{
		if(search==null)
		{
			search	= new IntermediateFuture<IComponentIdentifier>();
		}
		
		// TODO: initial
		// TODO: timeout
		
		return search;
	}
	

	/**
	 *  Immediately return known platforms and continuously publish newly found platforms.
	 *  Does no active searching.
	 */
	public ISubscriptionIntermediateFuture<IComponentIdentifier>	subscribeToNewPlatforms()
	{
		SubscriptionIntermediateFuture<IComponentIdentifier>	sub	= new SubscriptionIntermediateFuture<IComponentIdentifier>();
		subscriptions.add(sub);
		// TODO: initial
		return sub;
	}
	
	//-------- template methods --------
	
	/**
	 *  To be called whenever platform addresses are discovered.
	 */
	protected void	discovered(final Collection<TransportAddress> addresses)
	{
		ITransportAddressService tas = SServiceProvider.getLocalService(agent, ITransportAddressService.class);
		tas.addManualAddresses(addresses).addResultListener(new IResultListener<Void>()
		{
			@Override
			public void resultAvailable(Void result)
			{
				// Extract platforms from address list
				Set<IComponentIdentifier>	new_platforms	= new LinkedHashSet<IComponentIdentifier>();
				for(TransportAddress address: addresses)
				{
					new_platforms.add(address.getPlatformId());
				}
				
				// Add platforms and notify about new.
				for(IComponentIdentifier platform: new_platforms)
				{
					if(platforms.add(platform))
					{
						if(search!=null)
						{
							search.addIntermediateResult(platform);
						}
						
						for(SubscriptionIntermediateFuture<IComponentIdentifier> sub: subscriptions)
						{
							sub.addIntermediateResult(platform);
						}
					}
				}
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				// shouldn't happen?
			}
		});
	}
}
