package jadex.platform.service.awareness;

import java.util.Collections;
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
import jadex.micro.annotation.AgentArgument;

/**
 *  Implements passive awareness via shared memory.
 *  Mainly used for robust local testing.
 */
@Service
@Agent(autoprovide = Boolean3.TRUE, autostart=Boolean3.FALSE)
public class IntraVMAwarenessAgent implements IAwarenessService
{	
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The data holder (to be injected from outside to be shared across multiple platforms). */
	@AgentArgument
	protected AwarenessData data;
	
	//-------- agent lifecycle --------

	/**
	 *  At startup add platform to list.
	 */
	@ServiceStart
	//@OnStart
	public void	start() throws Exception
	{
		if(data==null)
		{
			throw new IllegalArgumentException(getClass().getSimpleName()+" requires an argument 'data' of type "+AwarenessData.class.getName());
		}
		data.register(this);
	}
	
	/**
	 *  At shutdown remove platform from list.
	 */
	@ServiceShutdown
	//@OnEnd
	public void shutdown()	throws Exception
	{
		data.deregister(this);
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
		IntraVMAwarenessAgent remote = data.getAgentForId(platformid);
		
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
		Set<IComponentIdentifier> result = data.getPlatforms();
		result.remove(agent.getId().getRoot());	// remove own platform from list
		return new IntermediateFuture<IComponentIdentifier>(result);
	}
	
	//-------- helper classes --------
	
	/**
	 *  Shared data structure across connected awareness agents.
	 */
	public static class AwarenessData
	{
		/** Read-write lock for discoveries map. */
		protected ReadWriteLock disclock = new ReentrantReadWriteLock(false);
		
		/** The started discovery agents. */
		protected Map<IComponentIdentifier, IntraVMAwarenessAgent> discoveries	= new HashMap<IComponentIdentifier, IntraVMAwarenessAgent>();
		
		/**
		 *  Register a platform via its awareness agent.
		 */
		protected void	register(IntraVMAwarenessAgent agent)
		{
			IComponentIdentifier pfid = agent.agent.getId().getRoot();
			disclock.writeLock().lock();
			discoveries.put(pfid, agent);
			disclock.writeLock().unlock();
		}
		
		/**
		 *  Deregister a previously registered platform.
		 */
		protected void	deregister(IntraVMAwarenessAgent agent)
		{
			IComponentIdentifier pfid = agent.agent.getId().getRoot();
			disclock.writeLock().lock();
			discoveries.remove(pfid);
			disclock.writeLock().unlock();
		}
		
		/**
		 *  Lookup the target agent for a platform id.
		 *  @return The previously registered agent if found or null otherwise.
		 */
		protected IntraVMAwarenessAgent getAgentForId(IComponentIdentifier platformid)
		{
			IntraVMAwarenessAgent remote = null;
			disclock.readLock().lock();
			remote = discoveries.get(platformid);
			disclock.readLock().unlock();
			return remote;
		}
		
		/**
		 *  Get the currently registered platform ids.
		 */
		protected Set<IComponentIdentifier> getPlatforms()
		{
			disclock.readLock().lock();
			HashSet<IComponentIdentifier> result = new HashSet<IComponentIdentifier>(discoveries.keySet());
			disclock.readLock().unlock();
			return result;
		}
	}
}
