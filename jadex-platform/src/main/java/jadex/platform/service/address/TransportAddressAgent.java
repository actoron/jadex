package jadex.platform.service.address;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.address.ITransportAddress;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;

/**
 *  Agent that provides the security service.
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service(system=true)
public class TransportAddressAgent implements ITransportAddressService
{
	/** Freshness limit for renewed searches. */
	protected static final long CACHE_VALIDITY_DUR = 10*60*1000;
	
	/** Maximum number of peers to ask for addresses. */
	protected static final int ASK_ALL_LIMIT = 3;
	
	/** Component access. */
	@Agent
	protected IInternalAccess agent;
	
	/** The local addresses. */
	protected List<ITransportAddress> localaddresses;
	
	/** Current active searches. */
	protected Map<IComponentIdentifier, IFuture<List<ITransportAddress>>> searches;
	
	/** Freshness state of the cache. */
	protected Map<IComponentIdentifier, Long> freshness;
	
	/** The managed addresses: target platform -> (transport name -> transport addresses) */
	protected Map<IComponentIdentifier, List<ITransportAddress>> addresses;
	
	/**
	 *  Initializes the service.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		addresses = new HashMap<IComponentIdentifier, List<ITransportAddress>>();
		
		return IFuture.DONE;
	}
	
	/**
	 *  Gets the addresses of the local platform.
	 *  
	 *  @return Addresses of the local platform.
	 */
	public IFuture<List<ITransportAddress>> getAddresses()
	{
		return new Future<List<ITransportAddress>>(getAddressesFromCache(null));
	}
	
	/**
	 *  Gets the addresses of the local platform.
	 *  
	 *  @param transporttype The transport type.
	 *  @return Addresses of the local platform.
	 */
	public IFuture<List<ITransportAddress>> getAddresses(String transporttype)
	{
		List<ITransportAddress> ret = null;
		for (ITransportAddress addr : localaddresses)
		{
			if (addr.getTransportType().equals(transporttype))
			{
				if (ret == null)
					ret = new ArrayList<ITransportAddress>();
				ret.add(addr);
			}
		}
		return new Future<List<ITransportAddress>>(ret);
	}
	
	/**
	 *  Gets the addresses of another platform known to the local platform.
	 *  
	 *  @param platformid ID of the platform.
	 *  @return Addresses of the platform, if known.
	 */
	public IFuture<List<ITransportAddress>> getAddresses(IComponentIdentifier platformid)
	{		
		List<ITransportAddress> ret = getAddressesFromCache(platformid);
		return new Future<List<ITransportAddress>>(ret);
	}
	
	/**
	 *  Gets the addresses of another platform known to the local platform.
	 *  
	 *  @param platformid ID of the platform.
	 *  @return Addresses of the platform, if known.
	 */
	public IFuture<List<ITransportAddress>> getAddresses(IComponentIdentifier platformid, String transporttype)
	{
		List<ITransportAddress> addrs = getAddressesFromCache(platformid);
		List<ITransportAddress> ret = null;
		
		if (addrs != null)
		{
			for (ITransportAddress addr : addrs)
			{
				if (addr.getTransportType().equals(transporttype))
				{
					if (ret == null)
						ret = new ArrayList<ITransportAddress>();
					ret.add(addr);
				}
			}
		}
		
		return new Future<List<ITransportAddress>>(ret);
	}
	
	/**
	 *  Recursively looks up the addresses of a platform for a specific transport type.
	 *  
	 *  @param platformid ID of the platform.
	 *  @param transporttype The transport type.
	 *  @return Addresses of the local platform.
	 */
	public IFuture<List<ITransportAddress>> getAddressesRecursively(final IComponentIdentifier platformid, String transporttype)
	{
		IFuture<List<ITransportAddress>> ret = null;
		
		if (freshness.get(platformid) != null &&
			freshness.get(platformid) + CACHE_VALIDITY_DUR > System.currentTimeMillis());
		{
			List<ITransportAddress> addrs = getAddressesFromCache(platformid);
			addrs = filterAddresses(addrs, transporttype);
			if (addrs != null)
				ret = new Future<List<ITransportAddress>>(filterAddresses(addrs, transporttype));
		}
		
		if (ret == null)
		{
			final Future<List<ITransportAddress>> fret = new Future<List<ITransportAddress>>();
			ret = fret;
			IFuture<List<ITransportAddress>> search = searches.get(platformid);
			if (search == null)
			{
				search = agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<List<ITransportAddress>>()
				{
					public IFuture<List<ITransportAddress>> execute(IInternalAccess ia)
					{
						List<ITransportAddress> ret = null;
						if (hasSuperPeer())
							 ret = searchAddressesByAskSuperPeer(platformid);
						
						if (ret == null && !hasSuperPeer())
						{
							ret = searchAddressesByAskRemote(platformid);
							
							if (ret == null)
								ret = searchAddressesByAskAwareness(platformid);
							
							if (ret == null)
								ret = searchAddressesByAskAll(platformid);
						}
						
						ret = getAddressesFromCache(platformid);
						
						searches.remove(platformid);
						
						return new Future<List<ITransportAddress>>(ret);
					}
				});
				searches.put(platformid, search);
			}
			
			search.addResultListener(new IResultListener<List<ITransportAddress>>()
			{
				public void exceptionOccurred(Exception exception)
				{
					resultAvailable(null);
				}
				
				public void resultAvailable(List<ITransportAddress> result)
				{
					freshness.put(platformid, System.currentTimeMillis());
					
					fret.setResult(result);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Checks if a super peer is available.
	 * 
	 *  @return True, if super peer is available.
	 */
	protected boolean hasSuperPeer()
	{
		return false;
	}
	
	/**
	 *  Searches for addresses using super peer.
	 * 
	 *  @param platformid The platform ID.
	 *  @return The addresses.
	 */
	protected List<ITransportAddress> searchAddressesByAskSuperPeer(IComponentIdentifier platformid)
	{
		//TODO
		return null;
	}
	
	/**
	 *  Searches for addresses directly asking remote platform.
	 * 
	 *  @param platformid The platform ID.
	 *  @return The addresses.
	 */
	protected List<ITransportAddress> searchAddressesByAskRemote(IComponentIdentifier platformid)
	{
		List<ITransportAddress> ret = null;
		try
		{
			ServiceQuery<ITransportAddressService> query = new ServiceQuery<ITransportAddressService>(ITransportAddressService.class, Binding.SCOPE_COMPONENT, null, platformid, null);
			
			ITransportAddressService rtas = SServiceProvider.getService(agent, query).get();
			if (rtas != null)
			{
				ret = rtas.getAddresses().get();
			}
			addAddresses(ret);
		}
		catch (Exception e)
		{
		}
		return ret;
	}
	
	/**
	 *  Searches for addresses using super peer.
	 * 
	 *  @param platformid The platform ID.
	 *  @return The addresses.
	 */
	protected List<ITransportAddress> searchAddressesByAskAwareness(IComponentIdentifier platformid)
	{
		List<ITransportAddress> ret = null;
		IAwarenessManagementService awa = SServiceProvider.getLocalService(agent, IAwarenessManagementService.class);
		try
		{
			DiscoveryInfo info = awa.getPlatformInfo(platformid).get();
			if (info != null)
				ret = info.getAddresses();
		}
		catch (Exception e)
		{
		}
		
		addAddresses(ret);
		
		return ret;
	}
	
	/**
	 *  Randomly ask other platforms for addresses (search of last resort).
	 *  
	 *  @param platformid The platform ID.
	 *  @return The addresses.
	 */
	protected List<ITransportAddress> searchAddressesByAskAll(final IComponentIdentifier platformid)
	{
		final Future<List<ITransportAddress>> ret = new Future<List<ITransportAddress>>();
		final ITerminableIntermediateFuture<ITransportAddressService> fut = SServiceProvider.getServices(agent, ITransportAddressService.class, Binding.SCOPE_GLOBAL);
		fut.addIntermediateResultListener(new IIntermediateResultListener<ITransportAddressService>()
		{
			/** The peers. */
			protected List<ITransportAddressService> peers = new ArrayList<ITransportAddressService>();
			
			
			
			public void exceptionOccurred(Exception exception)
			{
			}
			
			public void resultAvailable(Collection<ITransportAddressService> result)
			{
			}
			
			public void intermediateResultAvailable(ITransportAddressService result)
			{
				peers.add(result);
				
				if (peers.size() == ASK_ALL_LIMIT)
					fut.terminate();
			}
			
			public void finished()
			{
				Set<ITransportAddress> addrs = new LinkedHashSet<ITransportAddress>();
				for (ITransportAddressService peer : peers)
				{
					try
					{
						List<ITransportAddress> remoteaddrs = peer.getAddresses(platformid).get();
						if (remoteaddrs != null)
							addrs.addAll(remoteaddrs);
					}
					catch (Exception e)
					{
					}
				}
				addAddresses(addrs);
				
				ret.setResult(new ArrayList<ITransportAddress>(addrs));
			}
		});
		
		return ret.get();
	}
	
	/**
	 *  Gets platform addresses from local cache.
	 *  
	 *  @param platformid Platform ID.
	 *  @return Addresses.
	 */
	protected List<ITransportAddress> getAddressesFromCache(IComponentIdentifier platformid)
	{
		List<ITransportAddress> ret = null;
		if (platformid == null || agent.getComponentIdentifier().getRoot().equals(platformid))
			ret = localaddresses;
		else
			ret = addresses.get(platformid);
		return ret;
	}
	
	/**
	 *  Adds addresses to cache.
	 *  
	 *  @param addrs The addresses.
	 */
	protected void addAddresses(Collection<ITransportAddress> addrs)
	{
		if (addrs != null && addrs.size() > 0)
		{
			for (ITransportAddress addr : addrs)
			{
				List<ITransportAddress> tlist = null;
				if (agent.getComponentIdentifier().getRoot().equals(addr.getPlatformId()))
				{
					tlist = localaddresses;
				}
				else
				{
					tlist = addresses.get(addr.getPlatformId());
					
					if (tlist == null)
					{
						tlist = new ArrayList<ITransportAddress>();
						addresses.put(addr.getPlatformId(), tlist);
					}
				}
				tlist.addAll(addrs);
			}
		}
	}
	
	/**
	 *  Filters addresses for transport types.
	 *  
	 *  @param addresses The addresses to filter.
	 *  @param transporttype The transport type.
	 *  @return Filtered list or null of none match.
	 */
	protected List<ITransportAddress> filterAddresses(List<ITransportAddress> addresses, String transporttype)
	{
		List<ITransportAddress> ret = null;
		if (addresses != null)
		{
			for (ITransportAddress addr : addresses)
			{
				if (addr.getTransportType().equals(transporttype))
				{
					if (ret == null)
						ret = new ArrayList<ITransportAddress>();
					ret.add(addr);
				}
			}
		}
		return ret;
	}
}
