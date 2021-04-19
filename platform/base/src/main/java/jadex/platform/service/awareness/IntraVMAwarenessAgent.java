package jadex.platform.service.awareness;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.awareness.IAwarenessService;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;

/**
 *  Implements passive awareness via shared memory.
 */
@Service
@Agent(autoprovide = Boolean3.TRUE, autostart=Boolean3.FALSE)
public class IntraVMAwarenessAgent implements IAwarenessService
{
	//-------- constants --------
	
	/** Read-write lock for discoveries map. */
	protected static final ReadWriteLock disclock = new ReentrantReadWriteLock(false);
	
	/** The started discovery agents. */
	protected static final Map<IComponentIdentifier, IntraVMAwarenessAgent> discoveries	= new HashMap<IComponentIdentifier, IntraVMAwarenessAgent>();
	
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
//	/** The subscriptions. */
//	protected Set<SubscriptionIntermediateFuture<IComponentIdentifier>>	subscriptions = new HashSet<SubscriptionIntermediateFuture<IComponentIdentifier>>();
	
	//-------- agent lifecycle --------

	/**
	 *  At startup add platform to list.
	 */
	@ServiceStart
	//@OnStart
	public void	start() throws Exception
	{
		IComponentIdentifier pfid = agent.getId().getRoot();
		disclock.writeLock().lock();
//		for(IntraVMAwarenessAgent otheragent: discoveries.values())
//			otheragent.announceNewPlatform(pfid);
		discoveries.put(pfid, this);
		disclock.writeLock().unlock();
	}
	
	/**
	 *  At shutdown remove platform from list.
	 */
	@ServiceShutdown
	//@OnEnd
	public void shutdown()	throws Exception
	{
		disclock.writeLock().lock();
		discoveries.remove(agent.getId().getRoot());
		disclock.writeLock().unlock();
	}
	
	//-------- IAwarenessService interface --------
	
	/**
	 *  Gets the address for a platform ID using the awareness mechanism.
	 * 
	 *  @param platformid The platform ID.
	 *  @return The transport addresses or null if not available.
	 */
	public IFuture<List<TransportAddress>> getPlatformAddresses(IComponentIdentifier platformid)
	{
		IntraVMAwarenessAgent remote = null;
		disclock.readLock().lock();
		remote = discoveries.get(platformid);
		disclock.readLock().unlock();
		
		if(remote!=null)
		{
			
			return remote.agent.getExternalAccess().scheduleStep(new IComponentStep<List<TransportAddress>>()
			{
				public IFuture<List<TransportAddress>> execute(IInternalAccess ia)
				{
					ITransportAddressService tas = ia.getFeature(IRequiredServicesFeature.class).getLocalService(new ServiceQuery<>( ITransportAddressService.class));
					return tas.getAddresses();
				}
			});
		}
		else
		{
			return new Future<List<TransportAddress>>(Collections.emptyList());
		}
	}
	
	/**
	 *  Try to find other platforms and finish after timeout.
	 *  Immediately returns known platforms and concurrently issues a new search, waiting for replies until the timeout.
	 */
	public IIntermediateFuture<IComponentIdentifier> searchPlatforms()
	{
		disclock.readLock().lock();
		HashSet<IComponentIdentifier> result = new HashSet<IComponentIdentifier>(discoveries.keySet());
		disclock.readLock().unlock();
		result.remove(agent.getId().getRoot());
		return new IntermediateFuture<IComponentIdentifier>(result);
	}
	
	/**
	 *  Try to find other platforms while providing a quick answer.
	 *  Services should respond to a call as close to instantaneous as possible, but
	 *  with an upper bound of less than 1 second.
	 *  Issues a new search, but answers using known platforms. On first request
	 */
//	public IFuture<Set<IComponentIdentifier>> searchPlatformsFast()
//	{
//		disclock.readLock().lock();
//		HashSet<IComponentIdentifier> result = new HashSet<IComponentIdentifier>(discoveries.keySet());
//		disclock.readLock().unlock();
//		result.remove(agent.getId().getRoot());
//		return new Future<Set<IComponentIdentifier>>(result);
//	}
	
//	/**
//	 *  Immediately return known platforms and continuously publish newly found platforms.
//	 *  Does no active searching.
//	 */
//	public ISubscriptionIntermediateFuture<IComponentIdentifier> subscribeToNewPlatforms()
//	{
//		SubscriptionIntermediateFuture<IComponentIdentifier> sub = new SubscriptionIntermediateFuture<IComponentIdentifier>();
//		sub.setTerminationCommand(new TerminationCommand()
//		{
//			@Override
//			public void terminated(Exception reason)
//			{
//				subscriptions.remove(sub);
//			}
//		});
//		subscriptions.add(sub);
//		
//		// Add initial results
//		disclock.readLock().lock();
//		for(IComponentIdentifier platform : discoveries.keySet())
//		{
//			sub.addIntermediateResult(platform);
//		}
//		disclock.readLock().unlock();
//
//		return sub;
//	}	
//	
//	//-------- helper methods --------
//	
//	/**
//	 *  Receive info about a new platform, called with remote thread.
//	 *  @param newplatform The new platform.
//	 */
//	protected void announceNewPlatform(final IComponentIdentifier newplatform)
//	{
//		agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				for (SubscriptionIntermediateFuture<IComponentIdentifier> listener : subscriptions)
//					listener.addIntermediateResultIfUndone(newplatform);
//				return IFuture.DONE;
//			}
//		});
//	}
}
