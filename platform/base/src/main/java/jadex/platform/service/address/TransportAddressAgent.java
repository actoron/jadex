package jadex.platform.service.address;

import java.util.ArrayList;
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
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQuery.Multiplicity;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.pawareness.IPassiveAwarenessService;
import jadex.commons.Boolean3;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Autostart;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;

/**
 *  Agent that provides the security service.
 */
@Agent(autoprovide=Boolean3.TRUE, autostart=@Autostart(value=Boolean3.TRUE, name="address"))
@ProvidedServices(@ProvidedService(type=ITransportAddressService.class, scope=RequiredService.SCOPE_PLATFORM))
@Service(system=true)
public class TransportAddressAgent implements ITransportAddressService
{
	/** Freshness limit for renewed searches. */
	protected static final long CACHE_VALIDITY_DUR = 10*60*1000;
	
	/** Freshness limit for previously failed renewed searches. */
	protected static final long CACHE_INVALIDITY_DUR = 5000;
	
	/** Maximum number of peers to ask for addresses. */
	protected static final int ASK_ALL_LIMIT = 3;
	
	/** Component access. */
	@Agent
	protected IInternalAccess agent;
	
	/** The local addresses. */
	protected LinkedHashSet<TransportAddress> localaddresses;
	
	/** Current active searches. */
	protected Map<IComponentIdentifier, IFuture<List<TransportAddress>>> searches;
	
	/** Freshness state of the cache. */
	protected Map<IComponentIdentifier, Long> freshness;
	
	/** The managed addresses: target platform -> (transport name -> transport addresses) */
	protected Map<IComponentIdentifier, LinkedHashSet<TransportAddress>> addresses;
	
	/** The managed addresses: target platform -> (transport name -> transport addresses) */
	protected Map<IComponentIdentifier, LinkedHashSet<TransportAddress>> manualaddresses;
	
	/** Subscription of local address changes. */
	protected LinkedHashSet<SubscriptionIntermediateFuture<Tuple2<TransportAddress, Boolean>>> addresssubs;
	
	@AgentArgument
	protected boolean superpeerlookup = true;
	
	@AgentArgument
	protected boolean directlookup = true;
	
	@AgentArgument
	protected boolean pawalookup = true;
	
	@AgentArgument
	protected boolean awalookup = false;
	
	@AgentArgument
	protected boolean askalllookup = false;
	
	/**
	 *  Initializes the service.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		localaddresses = new LinkedHashSet<TransportAddress>();
		addresses = new HashMap<IComponentIdentifier, LinkedHashSet<TransportAddress>>();
		manualaddresses = new HashMap<IComponentIdentifier, LinkedHashSet<TransportAddress>>();
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
		assert (platformid != null && platformid.equals(platformid.getRoot()));
//		System.out.println("RESOLVE CALLED: " + platformid + " " + transporttype);
		Future<List<TransportAddress>> ret = new Future<List<TransportAddress>>();
		try
		{
			List<TransportAddress> addrs = getAddressesFromCache(platformid);
			if (addrs == null || addrs.isEmpty())
			{
				updateFromLocalAwareness(platformid);
				addrs = getAddressesFromCache(platformid);
			}
			addrs = filterAddresses(addrs, transporttype);
//			System.out.println("Addrs " + addrs == null ? "null" : Arrays.toString(addrs.toArray()));
			ret.setResult(addrs);
			
			if (freshness.get(platformid) == null ||
				freshness.get(platformid) > System.currentTimeMillis())
			{
				final Future<List<TransportAddress>> fret = new Future<List<TransportAddress>>();
				
				if (addrs == null || addrs.size() == 0)
				{
//					System.out.println("RET switcheroo: " + transporttype);
					ret = fret;
				}
				
				IFuture<List<TransportAddress>> search = searches.get(platformid);
				if (search == null)
				{
					search = agent.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<List<TransportAddress>>()
					{
						public IFuture<List<TransportAddress>> execute(IInternalAccess ia)
						{
							List<TransportAddress> ret = null;
							if (hasSuperPeer())
								 ret = searchAddressesByAskSuperPeer(platformid);
							
							//if (ret == null && !hasSuperPeer())
							if (ret == null)
							{
								ret = searchAddressesByAskPassiveAwareness(platformid);
								
								if (ret == null)
								ret = searchAddressesByAskRemote(platformid);
								
								if (ret == null)
									ret = searchAddressesByAskAwareness(platformid);
								
								// Case not needed?
//								if (ret == null)
//									ret = searchAddressesByAskAll(platformid);
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
						resultAvailable(null);
					}
					
					public void resultAvailable(List<TransportAddress> result)
					{
						if (result != null)
							freshness.put(platformid, System.currentTimeMillis() + CACHE_VALIDITY_DUR );
						else
						{
							freshness.put(platformid, System.currentTimeMillis() + CACHE_INVALIDITY_DUR );
//							throw new Error(agent.getComponentIdentifier() + " FAILED: " + platformid);
						}
						
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
			IComponentIdentifier local = agent.getId().getRoot();
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
//		System.out.println(agent.getComponentIdentifier().getRoot().toString() + " Manual addresses added: " + Arrays.toString(addresses.toArray()));
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
	 *  Updates the cache using the local awareness cache.
	 *  @param platformid The platform ID.
	 */
	protected void updateFromLocalAwareness(IComponentIdentifier platformid)
	{
		IAwarenessManagementService awa = agent.getFeature(IRequiredServicesFeature.class)
			.searchLocalService(new ServiceQuery<>(IAwarenessManagementService.class).setMultiplicity(Multiplicity.ZERO_ONE));
		if(awa!=null)
		{
			DiscoveryInfo info = awa.getCachedPlatformInfo(platformid).get();
			if (info != null)
				addAddresses(info.getAddresses());
		}
	}
	
	/**
	 *  Searches for addresses using super peer.
	 * 
	 *  @param platformid The platform ID.
	 *  @return The addresses.
	 */
	protected List<TransportAddress> searchAddressesByAskSuperPeer(IComponentIdentifier platformid)
	{
		if (superpeerlookup)
		{
			//TODO
			return null;
		}
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
		if (!directlookup)
			return null;
		
		List<TransportAddress> ret = null;
		try
		{
			ServiceQuery<ITransportAddressService> query = new ServiceQuery<ITransportAddressService>(ITransportAddressService.class, RequiredService.SCOPE_COMPONENT, platformid);
			
			ITransportAddressService rtas = agent.getFeature(IRequiredServicesFeature.class).searchService(query).get();
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
	protected List<TransportAddress> searchAddressesByAskPassiveAwareness(IComponentIdentifier platformid)
	{
		if (!pawalookup)
			return null;
		
		List<TransportAddress> ret = new ArrayList<TransportAddress>();
		
		try
		{
			Collection<IPassiveAwarenessService> awas = agent.getFeature(IRequiredServicesFeature.class).searchLocalServices(new ServiceQuery<>(IPassiveAwarenessService.class));
			for (IPassiveAwarenessService awa : awas)
			{
				try
				{
//					System.out.println(agent+" ask pawa: "+awa);
					Collection<TransportAddress> result = awa.getPlatformAddresses(platformid).get();
					if (result != null)
						ret.addAll(result);
				}
				catch (Exception e1)
				{
//					e1.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
		}
		
		addAddresses(ret);
		
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
		if (!awalookup)
			return null;
		
		List<TransportAddress> ret = null;
		IAwarenessManagementService awa = agent.getFeature(IRequiredServicesFeature.class)
			.searchLocalService(new ServiceQuery<>(IAwarenessManagementService.class).setMultiplicity(Multiplicity.ZERO_ONE));
		if(awa!=null)
		{
			DiscoveryInfo info = awa.getPlatformInfo(platformid).get();
			if (info != null)
				ret = info.getAddresses();

			addAddresses(ret);
		}
		
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
		if (!askalllookup)
			return null;
		
		final Future<List<TransportAddress>> ret = new Future<List<TransportAddress>>();
		ServiceQuery<ITransportAddressService> query = (new ServiceQuery<>(ITransportAddressService.class)).setScope(RequiredService.SCOPE_GLOBAL);
		final ITerminableIntermediateFuture<ITransportAddressService> fut = agent.getFeature(IRequiredServicesFeature.class).searchServices(query);
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
		if (platformid == null || agent.getId().getRoot().equals(platformid))
		{
			ret = new ArrayList<TransportAddress>(localaddresses);
		}
		else
		{
			LinkedHashSet<TransportAddress> addrs = addresses.get(platformid);
			LinkedHashSet<TransportAddress> manuals = manualaddresses.get(platformid);
			if (addrs == null)
			{
				ret = manuals == null ? null : new ArrayList<TransportAddress>(manuals);
			}
			else
			{
				addrs = new LinkedHashSet<TransportAddress>(addrs);
				if (manuals != null)
					addrs.addAll(manuals);
				ret = new ArrayList<TransportAddress>(addrs);
			}
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
	protected void addAddresses(Collection<TransportAddress> addrs, Map<IComponentIdentifier, LinkedHashSet<TransportAddress>> addressmap)
	{
		if (addrs != null && addrs.size() > 0)
		{
			for (TransportAddress addr : addrs)
			{
				if (agent.getId().getRoot().equals(addr.getPlatformId()))
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
					LinkedHashSet<TransportAddress> tlist = addressmap.get(addr.getPlatformId());
					
					if (tlist == null)
					{
						tlist = new LinkedHashSet<TransportAddress>();
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
