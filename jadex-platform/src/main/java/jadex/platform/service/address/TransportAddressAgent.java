package jadex.platform.service.address;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.commons.Boolean3;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
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
	protected List<TransportAddress> localaddresses;
	
	/** Current active searches. */
	protected Map<IComponentIdentifier, IFuture<List<TransportAddress>>> searches;
	
	/** Freshness state of the cache. */
	protected Map<IComponentIdentifier, Long> freshness;
	
	/** The managed addresses: target platform -> (transport name -> transport addresses) */
	protected Map<IComponentIdentifier, List<TransportAddress>> addresses;
	
	/** The managed addresses: target platform -> (transport name -> transport addresses) */
	protected Map<IComponentIdentifier, List<TransportAddress>> manualaddresses;
	
	/** Subscription of local address changes. */
	protected LinkedHashSet<SubscriptionIntermediateFuture<Tuple2<TransportAddress, Boolean>>> addresssubs;
	
	/**
	 *  Initializes the service.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		localaddresses = new ArrayList<TransportAddress>();
		addresses = new HashMap<IComponentIdentifier, List<TransportAddress>>();
		manualaddresses = new HashMap<IComponentIdentifier, List<TransportAddress>>();
		addresssubs = new LinkedHashSet<SubscriptionIntermediateFuture<Tuple2<TransportAddress, Boolean>>>(); 
		freshness = new HashMap<IComponentIdentifier, Long>();
		searches = new HashMap<IComponentIdentifier, IFuture<List<TransportAddress>>>();
		
		return IFuture.DONE;
	}
	
	/**
	 *  Gets the addresses of the local platform.
	 *  
	 *  @return Addresses of the local platform.
	 */
	public IFuture<List<TransportAddress>> getAddresses()
	{
		return new Future<List<TransportAddress>>(getAddressesFromCache(null));
	}
	
	/**
	 *  Gets the addresses of the local platform.
	 *  
	 *  @param transporttype The transport type.
	 *  @return Addresses of the local platform.
	 */
	public IFuture<List<TransportAddress>> getAddresses(String transporttype)
	{
		List<TransportAddress> ret = null;
		for (TransportAddress addr : localaddresses)
		{
			if (addr.getTransportType().equals(transporttype))
			{
				if (ret == null)
					ret = new ArrayList<TransportAddress>();
				ret.add(addr);
			}
		}
		return new Future<List<TransportAddress>>(ret);
	}
	
	/**
	 *  Gets the addresses of another platform known to the local platform.
	 *  
	 *  @param platformid ID of the platform.
	 *  @return Addresses of the platform, if known.
	 */
	public IFuture<List<TransportAddress>> getAddresses(IComponentIdentifier platformid)
	{		
		List<TransportAddress> ret = getAddressesFromCache(platformid);
		return new Future<List<TransportAddress>>(ret);
	}
	
	/**
	 *  Gets the addresses of another platform known to the local platform.
	 *  
	 *  @param platformid ID of the platform.
	 *  @return Addresses of the platform, if known.
	 */
	public IFuture<List<TransportAddress>> getAddresses(IComponentIdentifier platformid, String transporttype)
	{
		List<TransportAddress> addrs = getAddressesFromCache(platformid);
		List<TransportAddress> ret = null;
		
		if (addrs != null)
		{
			for (TransportAddress addr : addrs)
			{
				if (addr.getTransportType().equals(transporttype))
				{
					if (ret == null)
						ret = new ArrayList<TransportAddress>();
					ret.add(addr);
				}
			}
		}
		
		return new Future<List<TransportAddress>>(ret);
	}
	
	/**
	 *  Recursively looks up the addresses of a platform for a specific transport type.
	 *  
	 *  @param platformid ID of the platform.
	 *  @param transporttype The transport type.
	 *  @return Addresses of the local platform.
	 */
	public IFuture<List<TransportAddress>> resolveAddresses(final IComponentIdentifier platformid, final String transporttype)
	{
		Future<List<TransportAddress>> ret = new Future<List<TransportAddress>>();
		try
		{
			List<TransportAddress> addrs = getAddressesFromCache(platformid);
			addrs = filterAddresses(addrs, transporttype);
			ret.setResult(addrs);
			
			if (freshness.get(platformid) == null ||
				freshness.get(platformid) + CACHE_VALIDITY_DUR > System.currentTimeMillis())
			{
				final Future<List<TransportAddress>> fret = new Future<List<TransportAddress>>();
				
				if (addrs != null && addrs.size() > 0)
					ret = fret;
				
				IFuture<List<TransportAddress>> search = searches.get(platformid);
				if (search == null)
				{
					search = agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<List<TransportAddress>>()
					{
						public IFuture<List<TransportAddress>> execute(IInternalAccess ia)
						{
							List<TransportAddress> ret = null;
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
							
							return new Future<List<TransportAddress>>(ret);
						}
					});
					searches.put(platformid, search);
				}
				
				search.addResultListener(new IResultListener<List<TransportAddress>>()
				{
					public void exceptionOccurred(Exception exception)
					{
						freshness.put(platformid, System.currentTimeMillis());
						
						resultAvailable(null);
					}
					
					public void resultAvailable(List<TransportAddress> result)
					{
						freshness.put(platformid, System.currentTimeMillis());
						
//						System.out.println("Resolved addresses for " + platformid + ": " + Arrays.toString(result.toArray()));
						fret.setResult(filterAddresses(result, transporttype));
					}
				});
			}
		}
		catch (Exception e)
		{
		}
		
		return ret;
	}
	
	/**
	 *  Adds the addresses of the local platform.
	 *  
	 *  @param addresses Local platform addresses.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addLocalAddresses(Collection<TransportAddress> addresses)
	{
		Future<Void> ret = new Future<Void>();
		if (addresses != null)
		{
			IComponentIdentifier local = agent.getComponentIdentifier().getRoot();
			boolean ok = true;
			for (TransportAddress addr : addresses)
			{
				if (!local.equals(addr.getPlatformId()))
				{
					ok = false;
					break;
				}
			}
			
			if (ok)
			{
				addAddresses(addresses);
				ret.setResult(null);
			}
			else
			{
				ret.setException(new IllegalArgumentException("Addresses are not local."));
			}
		}
		else
		{
			ret.setException(new IllegalArgumentException("Addresses must not be null."));
		}
		return ret;
	}
	
	/**
	 *  Adds user-specified addresses.
	 *  Warning: Only use this to add manually specified addresses.
	 *  
	 *  @param addresses Platform addresses.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addManualAddresses(Collection<TransportAddress> addresses)
	{
		addToManualAddressesList(addresses);
		return IFuture.DONE;
	}
	
	/**
	 *  Subscribe to local address changes.
	 *  
	 *  @return Address and true if removed.
	 */
	public ISubscriptionIntermediateFuture<Tuple2<TransportAddress, Boolean>> subscribeToLocalAddresses()
	{
		final SubscriptionIntermediateFuture<Tuple2<TransportAddress, Boolean>> sub = (SubscriptionIntermediateFuture<Tuple2<TransportAddress, Boolean>>) SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		sub.setTerminationCommand(new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				addresssubs.remove(sub);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		});
		addresssubs.add(sub);
		
		for (TransportAddress addr : localaddresses)
		{
			sub.addIntermediateResult(new Tuple2<TransportAddress, Boolean>(addr, false));
		}
		
		return sub;
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
	protected List<TransportAddress> searchAddressesByAskSuperPeer(IComponentIdentifier platformid)
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
	protected List<TransportAddress> searchAddressesByAskRemote(IComponentIdentifier platformid)
	{
		List<TransportAddress> ret = null;
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
	protected List<TransportAddress> searchAddressesByAskAwareness(IComponentIdentifier platformid)
	{
		List<TransportAddress> ret = null;
		try
		{
			IAwarenessManagementService awa = SServiceProvider.getLocalService(agent, IAwarenessManagementService.class);
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
	protected List<TransportAddress> searchAddressesByAskAll(final IComponentIdentifier platformid)
	{
		final Future<List<TransportAddress>> ret = new Future<List<TransportAddress>>();
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
				Set<TransportAddress> addrs = new LinkedHashSet<TransportAddress>();
				for (ITransportAddressService peer : peers)
				{
					try
					{
						List<TransportAddress> remoteaddrs = peer.getAddresses(platformid).get();
						if (remoteaddrs != null)
							addrs.addAll(remoteaddrs);
					}
					catch (Exception e)
					{
					}
				}
				addAddresses(addrs);
				
				ret.setResult(new ArrayList<TransportAddress>(addrs));
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
	protected List<TransportAddress> getAddressesFromCache(IComponentIdentifier platformid)
	{
		List<TransportAddress> ret = null;
		if (platformid == null || agent.getComponentIdentifier().getRoot().equals(platformid))
		{
			ret = localaddresses;
		}
		else
		{
			ret = addresses.get(platformid);
			List<TransportAddress> manuals = manualaddresses.get(platformid);
			if (ret == null)
				ret = manuals;
			else if (manuals != null)
				ret.addAll(manuals);
		}
		return ret;
	}
	
	/**
	 *  Adds addresses to cache.
	 *  
	 *  @param addrs The addresses.
	 */
	protected void addAddresses(Collection<TransportAddress> addrs)
	{
		addAddresses(addrs, addresses);
	}
	
	/**
	 *  Adds addresses to manual address list.
	 *  
	 *  @param addrs The addresses.
	 */
	protected void addToManualAddressesList(Collection<TransportAddress> addrs)
	{
		addAddresses(addrs, manualaddresses);
	}
	
	/**
	 *  Adds addresses to a map.
	 *  
	 *  @param addrs The addresses.
	 *  @param addressmap The map of address that is the target of the operation.
	 */
	protected void addAddresses(Collection<TransportAddress> addrs, Map<IComponentIdentifier, List<TransportAddress>> addressmap)
	{
		if (addrs != null && addrs.size() > 0)
		{
			for (TransportAddress addr : addrs)
			{
				if (agent.getComponentIdentifier().getRoot().equals(addr.getPlatformId()))
				{
					if (!localaddresses.contains(addr))
					{
						localaddresses.add(addr);
						for (Iterator<SubscriptionIntermediateFuture<Tuple2<TransportAddress, Boolean>>> it = addresssubs.iterator(); it.hasNext(); )
						{
							SubscriptionIntermediateFuture<Tuple2<TransportAddress, Boolean>> sub = it.next();
							try
							{
								sub.addIntermediateResult(new Tuple2<TransportAddress, Boolean>(addr, false));
							}
							catch (Exception e)
							{
								it.remove();
							}
						}
					}
				}
				else
				{
					List<TransportAddress> tlist = addressmap.get(addr.getPlatformId());
					
					if (tlist == null)
					{
						tlist = new ArrayList<TransportAddress>();
						addressmap.put(addr.getPlatformId(), tlist);
					}
					tlist.add(addr);
				}
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
	protected List<TransportAddress> filterAddresses(List<TransportAddress> addresses, String transporttype)
	{
		List<TransportAddress> ret = null;
		if (addresses != null)
		{
			for (TransportAddress addr : addresses)
			{
				if (addr.getTransportType().equals(transporttype))
				{
					if (ret == null)
						ret = new ArrayList<TransportAddress>();
					ret.add(addr);
				}
			}
		}
		return ret;
	}
}
