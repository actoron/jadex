package jadex.platform.service.pawareness;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.pawareness.IPassiveAwarenessService;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;

/**
 *  Implements passive awareness via shared memory.
 */
@Service
@Agent(autoprovide = Boolean3.TRUE)
public class PassiveAwarenessIntraVMAgent implements IPassiveAwarenessService //extends PassiveAwarenessBaseAgent
{
	//-------- constants --------
	
	/** Read-write lock for discoveries map. */
	protected static final ReadWriteLock disclock = new ReentrantReadWriteLock(false);
	
	/** The started discovery agents. */
	protected static final Map<IComponentIdentifier, PassiveAwarenessIntraVMAgent> discoveries	= new HashMap<IComponentIdentifier, PassiveAwarenessIntraVMAgent>();
//	protected static final Set<PassiveAwarenessIntraVMAgent>	discoveries	= Collections.synchronizedSet(new HashSet<PassiveAwarenessIntraVMAgent>());

	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The subscriptions. */
	protected Set<SubscriptionIntermediateFuture<IComponentIdentifier>>	subscriptions = new HashSet<SubscriptionIntermediateFuture<IComponentIdentifier>>();
	
	//-------- agent lifecycle --------

	/**
	 *  At startup create a multicast socket for listening.
	 */
	@ServiceStart
	public void	start() throws Exception
	{
		IComponentIdentifier pfid = agent.getComponentIdentifier().getRoot();
		disclock.writeLock().lock();
		for (PassiveAwarenessIntraVMAgent otheragent : discoveries.values())
			otheragent.announceNewPlatform(pfid);
		discoveries.put(pfid, this);
		disclock.writeLock().unlock();
	}
	
	@ServiceShutdown
	public void shutdown()	throws Exception
	{
		disclock.writeLock().lock();
		discoveries.remove(agent.getComponentIdentifier().getRoot());
		disclock.writeLock().unlock();
	}
	
	//-------- methods --------
	
	/**
	 *  Gets the address for a platform ID using the awareness mechanism.
	 * 
	 *  @param platformid The platform ID.
	 *  @return The transport addresses or null if not available.
	 */
	public IFuture<Collection<TransportAddress>> getPlatformAddresses(IComponentIdentifier platformid)
	{
		Future<Collection<TransportAddress>> ret = new Future<Collection<TransportAddress>>();
		PassiveAwarenessIntraVMAgent remote = null;
		disclock.readLock().lock();
		remote = discoveries.get(platformid);
		disclock.readLock().unlock();
		
		if (remote != null)
		{
			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(5000, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					ret.setResultIfUndone(null);
					return IFuture.DONE;
				}
			}, true);
			remote.agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					ITransportAddressService tas = SServiceProvider.getLocalService(ia, ITransportAddressService.class);
					tas.getAddresses().addResultListener(new IResultListener<List<TransportAddress>>()
					{
						public void exceptionOccurred(Exception exception)
						{
							resultAvailable(null);
						}
						
						public void resultAvailable(final List<TransportAddress> result)
						{
							PassiveAwarenessIntraVMAgent.this.agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									ret.setResultIfUndone(result);
									return IFuture.DONE;
								}
							});
						}
					});
					return IFuture.DONE;
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Try to find other platforms and finish after timeout.
	 *  Immediately returns known platforms and concurrently issues a new search, waiting for replies until the timeout.
	 */
	public IIntermediateFuture<IComponentIdentifier> searchPlatforms()
	{
		IntermediateFuture<IComponentIdentifier> ret = new IntermediateFuture<IComponentIdentifier>();
		disclock.readLock().lock();
		HashSet<IComponentIdentifier> result = new HashSet<IComponentIdentifier>(discoveries.keySet());
		disclock.readLock().unlock();
		result.remove(agent.getComponentIdentifier().getRoot());
		ret.setResult(result);
		return ret;
	}
	
	/**
	 *  Receive info about a new platform, called with remote thread.
	 *  @param newplatform The new platform.
	 */
	public void announceNewPlatform(final IComponentIdentifier newplatform)
	{
		// Locking technically not necessary, lock should already be held
		// but using lock reentrancy just to be on the safe side.
		disclock.readLock().lock();
		@SuppressWarnings("unchecked")
		final SubscriptionIntermediateFuture<IComponentIdentifier>[] listeners = subscriptions.toArray(new SubscriptionIntermediateFuture[subscriptions.size()]);
		disclock.readLock().unlock();
		
		agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				for (SubscriptionIntermediateFuture<IComponentIdentifier> listener : listeners)
					listener.addIntermediateResult(newplatform);
				return IFuture.DONE;
			}
		});
	}

	public ISubscriptionIntermediateFuture<IComponentIdentifier> subscribeToNewPlatforms()
	{
		disclock.readLock().lock();
		SubscriptionIntermediateFuture<IComponentIdentifier> sub = new SubscriptionIntermediateFuture<IComponentIdentifier>();
		subscriptions.add(sub);

		// Add initial results
		for(IComponentIdentifier platform : discoveries.keySet())
		{
			sub.addIntermediateResult(platform);
		}
		disclock.readLock().unlock();

		return sub;
	}	
}
