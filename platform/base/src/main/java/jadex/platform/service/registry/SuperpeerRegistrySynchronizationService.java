package jadex.platform.service.registry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.registry.ARegistryEvent;
import jadex.bridge.service.types.registry.ARegistryResponseEvent;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.MultiRegistryEvent;
import jadex.bridge.service.types.registry.MultiRegistryResponseEvent;
import jadex.bridge.service.types.registry.RegistryEvent;
import jadex.bridge.service.types.registry.RegistryResponseEvent;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.commons.ICommand;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.LeaseTimeMap;
import jadex.commons.collection.LeaseTimeSet;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Binding;

/**
 *  Registry service for synchronization with remote platforms. 
 *  
 *  Has two behaviors:
 *  a) allows others to subscribe and sends updates according to local service registry
 *  b) uses awareness to detect new platforms and searches the ISuperpeerRegistrySynchronizationService for them. Subscribes at those of same level.
 */
public class SuperpeerRegistrySynchronizationService implements ISuperpeerRegistrySynchronizationService
{
	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;
	
	/** The subscriptions of other platforms (superpeers) (platform cid -> subscription info) They inform me. */
	protected Map<IComponentIdentifier, PeerInfo> partners;
	
	/** The platforms this registry has subscribed to. The other superpeers will send registry updates to me. */
	protected Set<PeerInfo> subscribedto;
	
	/** The currently blacklisted platforms (are not checked when newPlatformArrived() is called). */
	protected LeaseTimeSet<IComponentIdentifier> blplatforms;
	
	/** The client platforms that are managed by this super-peer. */
	protected LeaseTimeMap<IComponentIdentifier, PeerInfo> clients; 
	
	/** Local registry observer. */
	protected LocalRegistryObserver lrobs;
	
	/** Event collector for parent. */
	protected MultiEventCollector parentcol;
	
	/** Event collector for partners. */
	protected MultiEventCollector partnercol;
	
	/** Handles resposabilities of clients. */
	protected DependenciesHandler crh;
	
	/** Handles resposabilities of partners. */
	protected DependenciesHandler prh;
	
	/** The registry level.*/
	protected int level;
	
	//-------- super-super-peer handling --------
	
	/** The super-super-peer. */
	protected ISuperpeerRegistrySynchronizationService ssp;
	
	/** Potential superpeers. */
	protected List<IComponentIdentifier> potssps;
	
	/** The search functionality. */
	protected PeerSearchFunctionality psfunc;
	

	/** Use awareness to find other superpeers. */
	protected boolean useawa;
	
	/** The general delay used. */
	protected long delay;

	/**
	 *  Create a new service.
	 */
	public SuperpeerRegistrySynchronizationService(int level)
	{
		this(DEFAULT_SUPERSUPERPEERS, level);
	}
	
	/**
	 *  Create a new service.
	 */
	public SuperpeerRegistrySynchronizationService(IComponentIdentifier[] ssps, int level)
	{
		if(ssps!=null)
			this.potssps = SUtil.arrayToList(ssps);
		
		this.level = level;
		this.delay = 10000;
		this.useawa = true;
		
		System.out.println("SuperpeerRegistrySynchronizationService: level="+level);
	}
	
	/**
	 *  Start of the service.
	 */
	@ServiceStart
	public void init()
	{
		// Handler that updates the responsibilities of clients
		crh = new DependenciesHandler(getRegistry())
		{
			@Override
			public void putPeerInfo(PeerInfo client)
			{
				if(clients==null)
				{
					clients = new LeaseTimeMap<IComponentIdentifier, PeerInfo>((long)(2.2*lrobs.getTimeLimit()), new ICommand<Tuple2<Map.Entry<IComponentIdentifier, PeerInfo>, Long>>()
					{
						public void execute(Tuple2<Map.Entry<IComponentIdentifier, PeerInfo>, Long> tup) 
						{
							System.out.println("Removed peer: "+tup.getFirstEntity().getKey()+" lease: "+2.2*lrobs.getTimeLimit());
							
							// Remove services and queries of client and indirect clients
							removeAllClientRegistrations(tup.getFirstEntity().getValue());
						}
					}, false, true, new AgentDelayRunner(component), false);
				}
				
				clients.put(client.getPlatformId(), client);
			}
			
			@Override
			public PeerInfo getPeerInfo(IComponentIdentifier cid)
			{
				return clients==null? null: clients.get(cid);
			}
			
		};
		
		// Handler that updates the responsibilities of partners
		prh = new DependenciesHandler(getRegistry())
		{
			@Override
			public void putPeerInfo(PeerInfo client)
			{
				if(partners==null)
					partners = new LinkedHashMap<IComponentIdentifier, PeerInfo>();
//				System.out.println("addP: "+client.getPlatformId());
				partners.put(client.getPlatformId(), (PeerInfo)client);
			}
			
			@Override
			public PeerInfo getPeerInfo(IComponentIdentifier cid)
			{
				return partners==null? null: partners.get(cid);
			}
			
			public PeerInfo createPeerInfo(IComponentIdentifier cid)
			{
				return new PeerInfo(cid);
			}
		};
		
		// Search supersuperpeer functionality (uses list and search)
		this.psfunc = new PeerSearchFunctionality()
		{
			protected Iterator<IComponentIdentifier> it;
			
			@Override
			public IFuture<IComponentIdentifier> getNextPotentialPeer(boolean reset)
			{
				Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
				
				if(reset)
					it = potssps.iterator();
				
				if(it.hasNext())
				{
					IComponentIdentifier tmp = it.next();
					ret.setResult(tmp);
				}
				else
				{
					ret.setException(new RuntimeException("No more potential peers"));
				}
				
				return ret;
			}
			
			@Override
			public IFuture<Boolean> isOk(IComponentIdentifier cid)
			{
				final Future<Boolean> ret = new Future<Boolean>();
				try
				{
					ISuperpeerRegistrySynchronizationService sps = PeerRegistrySynchronizationService.getSuperpeerRegistrySynchronizationService(component, cid);
					sps.getLevel().addResultListener(new IResultListener<Integer>()
					{
						public void resultAvailable(Integer result) 
						{
							if(internalGetLevel()-1==result.intValue())
							{
								ret.setResult(Boolean.TRUE);
							}
							else
							{
								ret.setResult(Boolean.FALSE);
							}
						}
						
						public void exceptionOccurred(Exception exception) 
						{
							ret.setResult(Boolean.FALSE);
						}
					});
				}
				catch(ServiceNotFoundException e)
				{
					ret.setResult(Boolean.FALSE);
				}
				return ret;
			}
		};
		
		// Subscribe to changes of the local registry to inform other platforms
		lrobs = new LocalRegistryObserver(component, new AgentDelayRunner(component), true)//, eventslimit, timelimit)
		{
			public void notifyObservers(ARegistryEvent event)
			{
				// Only local changes are propagated (scope in query is platform)
//				System.out.println("Event: "+event);
//				forwardRegistryEventToPartners(event);
				partnercol.addEvent(event);
			}
		};
		
		// Event collector for partners (superpeers of same level)
		partnercol = new MultiEventCollector(component.getId(), new AgentDelayRunner(component))
		{
			@Override
			public void notifyObservers(ARegistryEvent event)
			{
//				System.out.println("collector notify partners");
				event.setClients(internalGetClients());
				forwardRegistryEventToPartners(event);
			}
			
			@Override
			public ARegistryEvent createEvent()
			{
				MultiRegistryEvent ret = (MultiRegistryEvent)super.createEvent();
				return ret;
			}
		};
		
		// Event collector for the supersuperpeer (contacting the ssp and send bunch updates from clients and myself)
		if(level==1)
		{
			parentcol = new MultiEventCollector(component.getId(), new AgentDelayRunner(component))
			{
				@Override
				public void notifyObservers(ARegistryEvent event)
				{
//					System.out.println("collector notify ssp");
					event.setClients(internalGetClients()); 
					forwardRegistryEventToParent(event, false);
				}
				
				@Override
				public ARegistryEvent createEvent()
				{
					MultiRegistryEvent ret = (MultiRegistryEvent)super.createEvent();
					return ret;
				}
			};
		}
		// Try to contact supersuperpeers regularily (for syncing)
		else if(level==0)
		{
			component.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					for(IComponentIdentifier psp: potssps)
					{
						newPlatformFound(psp, 0);
					}
//					component.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, this);
					return IFuture.DONE;
				}
			});
		}
		
		// Subscribe to awareness service to get informed when new (network wide) platforms are discovered
		if(useawa)
		{
			try
			{
				IAwarenessManagementService awas = component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IAwarenessManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
				
				awas.subscribeToPlatformList(true).addIntermediateResultListener(new IIntermediateResultListener<DiscoveryInfo>()
				{
					public void intermediateResultAvailable(DiscoveryInfo dis)
					{
						// Found a new platform -> search registry service and subscribe
						
						final IComponentIdentifier cid = dis.getComponentIdentifier();
						
						newPlatformFound(cid, 0);
					}
					
					public void resultAvailable(Collection<DiscoveryInfo> result)
					{
						// Should not happen
						System.out.println("Awareness subscription finished unexpectly");
					}
					
					public void finished()
					{
						// Should not happen
						System.out.println("Awareness subscription finished unexpectly");
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
					}
				});
			}
			catch(ServiceNotFoundException e)
			{
				System.out.println("Cannot subscribe at local awareness service (not found - using proxy agent approach)");
				
				component.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						Collection<IProxyAgentService> sers = component.getFeature(IRequiredServicesFeature.class).searchLocalServices(new ServiceQuery<>(IProxyAgentService.class, RequiredServiceInfo.SCOPE_PLATFORM));
	
						if(sers!=null && sers.size()>0)
						{
							for(IProxyAgentService ser: sers)
							{
								ser.getRemoteComponentIdentifier().addResultListener(new IResultListener<IComponentIdentifier>()
								{
									public void resultAvailable(IComponentIdentifier rcid)
									{
										newPlatformFound(rcid, 0);
									}
									
									public void exceptionOccurred(Exception exception)
									{
										exception.printStackTrace();
									}
								});
							}
						}
						
						component.getFeature(IExecutionFeature.class).waitForDelay(10000, this, true);
						
						return IFuture.DONE;
					}
				});
			}
		}
	}
	
	/**
	 *  Find a supersuperpeer from a given list of superpeers.
	 */
	protected IFuture<ISuperpeerRegistrySynchronizationService> getSupersuperpeerService(boolean force)
	{
		final Future<ISuperpeerRegistrySynchronizationService> ret = new Future<ISuperpeerRegistrySynchronizationService>();
		// Get a superpeer id
		psfunc.getPeer(force).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, ISuperpeerRegistrySynchronizationService>(ret)
		{
			// And afterwards a fake service adapter for it
			public void customResultAvailable(IComponentIdentifier result) throws Exception
			{
				try
				{
					ISuperpeerRegistrySynchronizationService res = PeerRegistrySynchronizationService.getSuperpeerRegistrySynchronizationService(component, result);
					ret.setResult(res);
				}
				catch(Exception e)
				{
					ret.setException(e);
				}
			}
		});
	
		return ret;
	}
	
	/**
	 *  Forward a registry event to all other superpeers.
	 */
	protected void forwardRegistryEventToPartners(ARegistryEvent event)
	{
		if(partners!=null)
		{
//			System.out.println("Sending sync update: "+event);
			for(PeerInfo info: partners.values())
			{
				// Can happen if partner contacts first
				if(info.getSubscription()!=null)
					((SubscriptionIntermediateFuture<ARegistryEvent>)info.getSubscription()).addIntermediateResult(event);
			}
		}
	}
	
	/**
	 *  Forward a registry event to the parent superpeer.
	 */
	protected void forwardRegistryEventToParent(final ARegistryEvent event, final boolean force)
	{
		getSupersuperpeerService(force).addResultListener(new IResultListener<ISuperpeerRegistrySynchronizationService>()
		{
			public void resultAvailable(final ISuperpeerRegistrySynchronizationService ssp)
			{
				// If ssp changed request full state from clients (todo: request only partial state with L0 info)
				if(SuperpeerRegistrySynchronizationService.this.ssp!=null && SuperpeerRegistrySynchronizationService.this.ssp != ssp)
					requestClientFullState();
				SuperpeerRegistrySynchronizationService.this.ssp = ssp;
				
//				System.out.println("Send update to ssp: "+((IService)ssp).getServiceIdentifier().getProviderId()+" "+event);
				ssp.updateClientData(event).addResultListener(new IResultListener<ARegistryResponseEvent>()
				{
					public void resultAvailable(ARegistryResponseEvent revent)
					{
						// todo!!!: handle multi events propery (inform clients about full state requests)
						if(revent instanceof MultiRegistryResponseEvent)
						{
							MultiRegistryResponseEvent mre = (MultiRegistryResponseEvent)revent;
							if(mre.getEvents()!=null)
							{
								for(ARegistryResponseEvent e: mre.getEvents())
								{
									RegistryResponseEvent re = (RegistryResponseEvent)e;
									resultAvailable(re);
								}
							}
						}
						else if(revent instanceof RegistryResponseEvent)
						{
							RegistryResponseEvent re = (RegistryResponseEvent)revent;
							System.out.println("registry update event level 1: "+Arrays.toString(re.getSuperpeers()));

							// Superpeer level 0 sent info about available level 1 superpeers
							if(re.getSuperpeers()!=null && re.getSuperpeers().length>0)
							{
								System.out.println("Was informed about new partner superpeers: "+Arrays.toString(re.getSuperpeers()));
								for(ISuperpeerRegistrySynchronizationService ser: re.getSuperpeers())
								{
									newPlatformFound(ser, 0);
								}
							}
						}
						
						// Send full status if receiver said I was unknown
						if(revent.isUnknown())
						{
//							RegistryEvent event = new RegistryEvent(true, ARegistryEvent.CLIENTTYPE_SUPERPEER_LEVEL1);
							ARegistryEvent event = getCurrentStateEvent();
							event.setClientType(ARegistryEvent.CLIENTTYPE_SUPERPEER_LEVEL1);
//							event.addAddedService((IService)component.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( ISuperpeerRegistrySynchronizationService.class)));
							ssp.updateClientData(event).addResultListener(this);
//							System.out.println("Send full update to ssp: "+((IService)ssp).getServiceIdentifier().getProviderId()+" "+event);
						}
						
						// Tell client (not only superpeer!) to send full update
						// Forwards response event to real (indirect) client
						if(revent.getReceiver()!=null && !getComponent().getId().getRoot().equals(revent.getReceiver()))
						{
							System.out.println("indirect answer: "+revent.getReceiver()+" "+getComponent().getId().getRoot());
							PeerInfo pi = getClient(revent.getReceiver());
							if(pi!=null)
							{
								pi.addAnswer(revent);
							}
							else
							{
								System.out.println("Unknown client: "+revent.getReceiver());
							}
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// Exception during update call on supersuperpeer
						// Supersuperpeer could have vanished or network partition
						
						System.out.println("Exception with supersuperpeer, resetting");
						exception.printStackTrace();
						
						if(!force)
							forwardRegistryEventToParent(event, true);
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				//System.out.println("Could not forward client service data to supersuperpeer (no one found): "+exception);
				component.getLogger().warning("Could not forward client service data to supersuperpeer (no one found): "+exception);
				
				// No new search when no superpeer was found, leads to 
//				if(!force)
//					forwardRegistryEventToParent(event, true);
			}
		});
	}
	
	/**
	 *  Get a client per id.
	 *  @param cid The id.
	 *  @return The peer info of the client.
	 */
	protected PeerInfo getClient(IComponentIdentifier cid)
	{
		return clients==null? null: clients.get(cid);
	}
	
	/**
	 *  Called when a new platform was found.
	 */
	protected void newPlatformFound(final IComponentIdentifier cid, final long leasetime)
	{
		final ISuperpeerRegistrySynchronizationService ser = PeerRegistrySynchronizationService.getSuperpeerRegistrySynchronizationService(component, cid);
		newPlatformFound(ser, leasetime);
	}
	
	/**
	 *  Called when a new platform was found.
	 */
	protected void newPlatformFound(final ISuperpeerRegistrySynchronizationService regser, final long leasetime)
	{
		final IComponentIdentifier cid = ((IService)regser).getId().getProviderId().getRoot();

		// Do not announce platform itself
		if(cid.equals(component.getId().getRoot()))
			return;
		
//		System.out.println("Informed about platform: "+cid+" (I am: "+component.getComponentIdentifier()+"), lease="+leasetime);
		
//		System.out.println(blackplatforms);
		
//		System.out.println(getRegistry());
			
		if(isBlacklistedPlatform(cid) || containsSubscribedTo(cid))
		{
//			System.out.println("Ignoring: already subscribed to: "+cid+" (I am: "+component.getComponentIdentifier()+")");
		}
		else
		{
			addBlacklistedPlatform(cid, leasetime);
			
//			boolean ssp = component.getComponentFeature(IPojoComponentFeature.class).getPojoAgent(SuperpeerRegistrySynchronizationAgent.class).isSupersuperpeer();
//			final ServiceQuery<ISuperpeerRegistrySynchronizationService> query = new ServiceQuery<ISuperpeerRegistrySynchronizationService>(ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM, null, component.getComponentIdentifier(), null);
//			query.setUnrestricted(ssp); // ssp means offers unrestricted
//			query.setPlatform(cid); // target platform on which to search
//			
//			SServiceProvider.waitForService(component, new IResultCommand<IFuture<ISuperpeerRegistrySynchronizationService>, Void>()
//			{
//				public IFuture<ISuperpeerRegistrySynchronizationService> execute(Void args)
//				{
////					return component.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( cid, RequiredServiceInfo.SCOPE_PLATFORM, ISuperpeerRegistrySynchronizationService.class, false));
//					return component.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( query));
//				}
//			}, 3, 10000).addResultListener(new IResultListener<ISuperpeerRegistrySynchronizationService>()
//			{
//				public void resultAvailable(final ISuperpeerRegistrySynchronizationService regser)
//				{
			
			// Partner search
			
			regser.getLevel().addResultListener(new IResultListener<Integer>()
			{
				public void resultAvailable(Integer result)
				{
					// If superpeer on same level was found it will be used for synchronization
					
					if(level==result.intValue())
					{
						// Subscribe to the new remote registry
						boolean unr = ((IService)regser).getId().isUnrestricted();

						System.out.println("Found registry service on: "+cid+(unr? " unrestricted": " default")+" (I am: "+component.getId()+")");
						
						ISubscriptionIntermediateFuture<ARegistryEvent> fut = regser.subscribeToEvents();
						final PeerInfo info = new PeerInfo(cid, fut);
						
						addSubscribedTo(info);
											
						fut.addIntermediateResultListener(new IIntermediateResultListener<ARegistryEvent>()
						{
							public void intermediateResultAvailable(ARegistryEvent event)
							{
//								if(event.size()>0)
//									System.out.println("Received an update event from: "+cid+", size="+event.size()+" "+event.hashCode()
//										+" at: "+System.currentTimeMillis()+"(I am: "+component.getComponentIdentifier()+")");
								
								// Update meta-data (lease time removal) and content in registry
								
								// Update the platform subscription info (the other platform will be removed if idle too long)
								info.setTimestamp(System.currentTimeMillis());
//								subscribedto.update(info);
								
								// todo: request updates
								// only called for top-level event as it has the right sender (other events might only be forwarded)
								Set<IComponentIdentifier> unknown = prh.updateDependencies(cid, event);
								
								handleRegistryEvent(event, null);
							}
							
							public void resultAvailable(Collection<ARegistryEvent> result)
							{
								finished();
							}
							
							public void finished()
							{
								System.out.println("Subscription finbished: "+cid);
								removeBlacklistedPlatforms(cid);
								removeSubscribedTo(info);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								if(exception instanceof ServiceNotFoundException)
								{
//									System.out.println("No registry service found, giving up: "+cid+" (I am: "+component.getComponentIdentifier()+")");
								}
								else
								{
									if(!(exception instanceof FutureTerminatedException)) // ignore terminate
									{
										System.out.println("Exception in my subscription with: "+cid+" (I am: "+component.getId()+")");
//										exception.printStackTrace();
									}
									removeBlacklistedPlatforms(cid);
									removeSubscribedTo(info);
								}
							}
						});
					}
					else
					{	
						System.out.println("Found superpeer of other level: "+level+" "+((IService)regser).getId()+" "+result);
					}
				}

				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("ex: "+exception);
//					if(!(exception instanceof ServiceNotFoundException))
//						removeBlacklistedPlatforms(cid);
				}
			});
					
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					System.out.println("Found no superpeer registry service on: "+cid);
//				}
//			});
		}
	}
	
	/**
	 *  Get the current partner superpeers.
	 */
	public IFuture<Collection<IComponentIdentifier>> getPartnerSuperpeers()
	{
		Collection<IComponentIdentifier>	ret;
		if(partners!=null)
			ret	= partners.keySet();
		else
			ret	= Collections.emptySet();
		return new Future<Collection<IComponentIdentifier>>((Collection<IComponentIdentifier>)(ret));
	}
	
	/**
	 *  Get the current clients (direct and indirect in flat form).
	 */
	public IFuture<Collection<IComponentIdentifier>> getClients()
	{
		return new Future<Collection<IComponentIdentifier>>(internalGetClients());
	}
	
	/**
	 *  Get the current clients (direct and indirect in flat form).
	 */
	public Set<IComponentIdentifier> internalGetClients()
	{
		Set<IComponentIdentifier> ret = new HashSet<IComponentIdentifier>();
		if(clients!=null)
		{
			Collection<PeerInfo> cis = clients.values();
			for(PeerInfo ci: cis)
			{
				ret.add(ci.getPlatformId());
			}
			ret.addAll(clients.keySet());
		}
		return ret;
	}
	
	/**
	 *  Request full state info from clients.
	 *  Add response event to answer sections of clients.
	 *  Will be sent in response to next request.
	 */
	protected void requestClientFullState()
	{
		if(clients!=null)
		{
			Collection<PeerInfo> pis = clients.values();
			for(PeerInfo pi: pis)
			{
				pi.addAnswer(new RegistryResponseEvent(pi.getPlatformId(), true));
			}	
		}
	}
	
	/**
	 *  Handle the update event of a registry.
	 *  @param event The event.
	 */
	protected void handleRegistryEvent(ARegistryEvent ev, Set<IComponentIdentifier> platforms)
	{
		IServiceRegistry reg = getRegistry();
		
		if(ev instanceof RegistryEvent)
		{
			RegistryEvent event = (RegistryEvent)ev;
			
			IComponentIdentifier plat = null;
			
			// Only add if registry is multi type
			Collection<IService> added = event.getAddedServices();
			if(added!=null)
			{
				for(IService ser: added)
				{
					if(checkScope(ser))
					{
						System.out.println("added ser: "+ser);
						reg.addService(ser.getId());
					}
					else
					{
						System.out.println("Not responsible for: "+ser+" level="+level);
					}
					
					if(plat==null)
						plat = ser.getId().getProviderId().getRoot();
				}
			}
			
			Collection<IService> removed = event.getRemovedServices();
			if(removed!=null)
			{
				for(IService ser: removed)
				{
					System.out.println("removed ser due to event: "+ser);
					reg.removeService(ser.getId());
					
					if(plat==null)
						plat = ser.getId().getProviderId().getRoot();
				}
			}
			
			if(platforms!=null)
				platforms.add(plat);
		}
		else if(ev instanceof MultiRegistryEvent)
		{
			MultiRegistryEvent event = (MultiRegistryEvent)ev;
		
			if(event.getEvents()!=null)
			{
				for(ARegistryEvent e: event.getEvents())
				{
					handleRegistryEvent(e, platforms);
				}
			}
		}
	}
	
	/**
	 *  Check if the service is handled by this superpeer.
	 */
	protected boolean checkScope(IService ser) // String clienttype
	{
		boolean ret = true;
		
		String scope = ser.getId().getScope();
		// SSP L0
		if(level==0)
		{
			// SSP L0 should not store local data  
			if(!RequiredServiceInfo.SCOPE_APPLICATION_GLOBAL.equals(scope)
				&& !RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			{
				ret = false;
			}
		}
		else
		{
			// SSP L1 should not store global data
			if(RequiredServiceInfo.SCOPE_APPLICATION_GLOBAL.equals(scope)
				|| RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			{
				ret = false;
			}
		}
		
		return ret;
	}
	
	
	/**
	 *  Called on shutdown.
	 */
	@ServiceShutdown
	public void destroy()
	{
		// Remove listener on local registry
		lrobs.terminate();
		
		// Remove this platform from all subscriptions on other platforms
		if(subscribedto!=null)
		{
//			ISubscriptionIntermediateFuture<IRegistryEvent>[] evs = subscribedto.toArray(new ISubscriptionIntermediateFuture[subscribedto.size()]);
			PeerInfo[] evs = subscribedto.toArray(new PeerInfo[subscribedto.size()]);
			for(PeerInfo info: evs)
			{
				info.getSubscription().terminate();
			}
		}
		
		// Finish subscriptions of other platforms 
		if(partners!=null)
		{
			PeerInfo[] pis = partners.values().toArray(new PeerInfo[partners.size()]);
			for(PeerInfo pi: pis)
			{
				if(pi.getSubscription() instanceof SubscriptionIntermediateFuture)
				{
					SubscriptionIntermediateFuture<ARegistryEvent> fut = (SubscriptionIntermediateFuture<ARegistryEvent>)pi.getSubscription();
					fut.setFinished();
				}
			}
		}
	}
	
	/**
	 *  Others subscribe to change events of this registry. 
	 */
	public ISubscriptionIntermediateFuture<ARegistryEvent> subscribeToEvents()
	{
		final IComponentIdentifier cid = ServiceCall.getCurrentInvocation().getCaller().getRoot();
		
		// If already subscribed reuse existing future
		if(hasPartner(cid) && getPartnerSubscription(cid)!=null)
			return getPartnerSubscription(cid);
		
		System.out.println("New subscription from: "+cid);
		
		final SubscriptionIntermediateFuture<ARegistryEvent> ret = (SubscriptionIntermediateFuture<ARegistryEvent>)
			SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, component);
		
		ITerminationCommand tcom = new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				removePartner(cid);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		};
		ret.setTerminationCommand(tcom);
		
		addPartner(cid, ret);
		
		// Forward current state initially
		// The current state consists of those parts that are managed as clients
		ARegistryEvent mre = getCurrentStateEvent();
		
		System.out.println("Sending full state: "+component.getId()+": "+mre);
		
		ret.addIntermediateResult(mre);
		
		return ret;
	}
	
	/**
	 *  Get the current state of the registry (including managed clients).
	 */
	protected ARegistryEvent getCurrentStateEvent()
	{
		// Forward current state initially
		// The current state consists of those parts that are managed as clients
		
		MultiRegistryEvent mre = new MultiRegistryEvent();
		
		for(IComponentIdentifier c: internalGetClients())
		{
			RegistryEvent ev = lrobs.getCurrentStateEvent(c);
			mre.addEvent(ev);
		}
		
		RegistryEvent ev = lrobs.getCurrentStateEvent(getComponent().getId());
		mre.addEvent(ev);
		mre.setClients(internalGetClients());
		mre.addClient(getComponent().getId().getRoot());
		mre.setSender(getComponent().getId().getRoot());
		
		return mre;
	}
	
	/**
	 *  Update the data of a new or already known client platform on the super-peer.
	 *  This is used by clients to let the super-peer know local changes.
	 *  (This is similar to a reverse subscription. The response tells the client
	 *  how long the lease time is and is the client was removed).
	 */
	public IFuture<ARegistryResponseEvent> updateClientData(ARegistryEvent event)
	{
		Future<ARegistryResponseEvent> ret = new Future<ARegistryResponseEvent>();
		
//		if(event instanceof MultiRegistryEvent && ((MultiRegistryEvent)(event)).getEvents()!=null)
//		{
//			System.out.println("received multi event from client: "+getComponent().getComponentIdentifier()+" "+((MultiRegistryEvent)(event)).getEvents().size());
//		}
//		else if(event instanceof RegistryEvent)
//		{
////			RegistryEvent re = (RegistryEvent)event;
////			int added = re.getAddedServices()!=null? re.getAddedServices().size(): 0;
////			int removed = re.getRemovedServices()!=null? re.getRemovedServices().size(): 0;
//			System.out.println("received event from client: "+System.currentTimeMillis()+" "+getComponent().getComponentIdentifier()+" event: "+event);
//		}
		
		final IComponentIdentifier cid = ServiceCall.getCurrentInvocation().getCaller().getRoot();

		Set<IComponentIdentifier> unknown = crh.updateDependencies(cid, event);
		
//		System.out.println("unknown: "+unknown);
		
		// indirectly deduces indirect clients from events
		handleRegistryEvent(event, null);
		
//		if(event.size()>0)
//			System.out.println("Client update request from: "+cid+" size:"+event.size()+" delta: "+event.isDelta());
		
		// Forward client updates to all other partner superpeers
//		forwardRegistryEventToPartners(event);
		partnercol.addEvent(event);
		
		// Collect events for parent
		addEventForParent(event);
		
		ARegistryResponseEvent res = prepareRegistryEventResponse(event, unknown);
//		System.out.println("response event is: "+res+" "+res.isUnknown());
		
		ret.setResult(res);
		
		return ret;
	}
	
	/**
	 *  Create a response (add collected and waiting answers for clients).
	 */
	protected ARegistryResponseEvent prepareRegistryEventResponse(ARegistryEvent event, Set<IComponentIdentifier> unknown)
	{
		ARegistryResponseEvent ret = null;
		
		PeerInfo pi = getClient(event.getSender());
		List<ARegistryResponseEvent> answers = pi==null? null: pi.removeAnswers();
		
		ARegistryResponseEvent res = createRegistryEventResponse(event, unknown);
		
		if(answers!=null)
		{
			MultiRegistryResponseEvent mp;
			
			if(!(res instanceof MultiRegistryResponseEvent))
			{
				mp = (MultiRegistryResponseEvent)createResponseEvent(/*mre.isDelta() &&*/ unknown.contains(event.getSender()), event.getNetworkNames(), true, event.getSender());
				mp.addEvent(res);
			}	
			else
			{
				mp = (MultiRegistryResponseEvent)res;
			}
			for(ARegistryResponseEvent a: answers)
			{
				mp.addEvent(a);
			}
			
			ret = mp;
		}
		else
		{
			ret = res;
		}
		
		return ret;
	}
	
	/**
	 *  Create a response event.
	 */
	protected ARegistryResponseEvent createRegistryEventResponse(ARegistryEvent event, Set<IComponentIdentifier> unknown)
	{
		ARegistryResponseEvent ret = null;
		
		// Prepare response
		if(event instanceof RegistryEvent)
		{
			RegistryEvent re = (RegistryEvent)event;
			ret = createResponseEvent(re.isDelta() && unknown.contains(re.getSender()), re.getNetworkNames(), false, event.getSender());
		}
		else if(event instanceof MultiRegistryEvent)
		{
			MultiRegistryEvent mre = (MultiRegistryEvent)event;
			MultiRegistryResponseEvent mp = (MultiRegistryResponseEvent)createResponseEvent(/*mre.isDelta() &&*/ unknown.contains(mre.getSender()), mre.getNetworkNames(), true, event.getSender());
			if(mre.getEvents()!=null)
			{
				for(ARegistryEvent e: mre.getEvents())
				{
//					ARegistryEvent re = (ARegistryEvent)event;
					ret = createRegistryEventResponse(e, unknown);
//					ret = createResponseEvent(re.isDelta() && unknown.contains(re.getSender()), re.getNetworkNames(), true, event.getSender());
					mp.addEvent(ret);
				}
			}
			ret = mp;
		}
		
		return ret;
	}
	
	/**
	 *  Remove all data that was registered by a client.
	 *  Can be direct data from ONE client or can be data
	 *  from MULTIPLE clients (when client is level 1 sp).
	 */
	protected void removeAllClientRegistrations(PeerInfo ci)
	{
		System.out.println("Remove client with all services: "+ci.getPlatformId());
		
		getRegistry().removeServices(ci.getPlatformId());
		//TODO
//		getRegistry().removeQueriesFromPlatform(ci.getPlatformId());
		
		Set<IComponentIdentifier> icls = ci.getIndirectClients();
		if(icls!=null)
		{
			for(IComponentIdentifier icl: icls)
			{
				getRegistry().removeServices(icl);
				//TODO
//				getRegistry().removeQueriesFromPlatform(icl);
			}
		}
	}
	
	/**
	 *  Add an event for the parent, i.e. ssp.
	 *  @param event The event.
	 */
	protected void addEventForParent(ARegistryEvent ev)
	{
		// Only level 1 superpeers have a parent to forward events to
		if(level!=1)
			return;
		
		if(ev instanceof MultiRegistryEvent)
		{
			MultiRegistryEvent mre = (MultiRegistryEvent)ev;
			List<ARegistryEvent> res = mre.getEvents();
			if(res!=null && res.size()>0)
			{
				for(ARegistryEvent re: res)
					addEventForParent(re);
			}
		}
		else if(ev instanceof RegistryEvent)
		{
			RegistryEvent event = (RegistryEvent)ev;
		
			// Forward global scope events to parent
			Set<IService> added = null;
			if(event.getAddedServices()!=null)
			{
				added = new HashSet<IService>();
				for(IService ser: event.getAddedServices())
				{
					String scope = ser.getId().getScope();
					if(Binding.SCOPE_GLOBAL.equals(scope)
						|| Binding.SCOPE_APPLICATION_GLOBAL.equals((scope)))
					{
						added.add(ser);
					}
				}
			}
			
			Set<IService> rem = null;
			if(event.getRemovedServices()!=null)
			{
				rem = new HashSet<IService>();
				for(IService ser: event.getRemovedServices())
				{
					String scope = ser.getId().getScope();
					if(Binding.SCOPE_GLOBAL.equals(scope)
						|| Binding.SCOPE_APPLICATION_GLOBAL.equals((scope)))
					{
						rem.add(ser);
					}
				}
			}
			
			if(rem!=null && rem.size()>0 || added!=null && added.size()>0)
			{
				final RegistryEvent nev = new RegistryEvent(event.isDelta(), ARegistryEvent.CLIENTTYPE_SUPERPEER_LEVEL1);
				nev.setSender(ev.getSender());
				nev.setAddedServices(added);
				nev.setRemovedServices(rem);
				parentcol.addEvent(nev);
			}
		}
	}
	
	/**
	 *  Create a response event for a client.
	 */
	protected ARegistryResponseEvent createResponseEvent(boolean unknown, String[] networknames, boolean multi, IComponentIdentifier sender)
	{
		// Special handling for superpeer clients of level 1
		// Sends back other network-compatible superpeers of level 1
		RegistryResponseEvent res = multi? new MultiRegistryResponseEvent(unknown, lrobs.getTimeLimit()): new RegistryResponseEvent(unknown, lrobs.getTimeLimit());
		res.setReceiver(sender);
//		if(IRegistryEvent.CLIENTTYPE_SUPERPEER_LEVEL1.equals(event.getClientType()))
//		if(IRegistryEvent.CLIENTTYPE_CLIENT.equals(event.getClientType()))
		
		// If I am a level 0 superpeer, I inform others about all known level 1 registries
		if(level==0)
		{
			ServiceQuery<IService> query = new ServiceQuery<IService>(ISuperpeerRegistrySynchronizationService.class, Binding.SCOPE_GLOBAL, component.getId(), null);
//			IMsgSecurityInfos secinfo = (IMsgSecurityInfos)ServiceCall.getCurrentInvocation().getProperty("securityinfo");
			query.setNetworkNames(networknames);
			
			Set<IServiceIdentifier> sers = getRegistry().searchServices(query);
			if(sers!=null)
			{
				for(Iterator<IServiceIdentifier> it=sers.iterator(); it.hasNext(); )
				{
					IServiceIdentifier ser = it.next();
					if(clients==null || !clients.containsKey(ser.getProviderId().getRoot()))
						it.remove();
				}
				res.setSuperpeers(sers.toArray(new ISuperpeerRegistrySynchronizationService[sers.size()]));
			}
			
//			System.out.println("Sending level 1 info: "+cid+" "+Arrays.toString(res.getSuperpeers()));
		}
		
		return res;
	}
	
	/**
	 *  Get the level (level 0 is the topmost superpeer level).
	 *  @retrun The level.
	 */
	public IFuture<Integer> getLevel()
	{
		return new Future<Integer>(level);
	}
	
	/**
	 *  Get the level (level 0 is the topmost superpeer level).
	 *  @retrun The level.
	 */
	public int internalGetLevel()
	{
		return level;
	}

	/**
	 *  Add a new subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected void addPartner(IComponentIdentifier cid, SubscriptionIntermediateFuture<ARegistryEvent> future)
	{
//		System.out.println("addPartner: "+cid);
		if(partners==null)
			partners = new LinkedHashMap<IComponentIdentifier, PeerInfo>();
		PeerInfo si = partners.get(cid);
		if(si==null)
			si = new PeerInfo(cid, future);
		else
			si.setSubscription(future);
		partners.put(cid, si);
	}
	
	/**
	 *  Test if has a subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected boolean hasPartner(IComponentIdentifier cid)
	{
		return partners!=null? partners.containsKey(cid): false;
	}
	
	/**
	 *  Remove an existing subscription.
	 *  @param cid The component id to remove.
	 */
	protected void removePartner(IComponentIdentifier cid)
	{
		if(partners==null || !partners.containsKey(cid))
			throw new RuntimeException("Subscriber not known: "+cid);
		PeerInfo si = partners.remove(cid);
		removeAllClientRegistrations(si);
	}
	
	/**
	 *  Get a subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected ISubscriptionIntermediateFuture<ARegistryEvent> getPartnerSubscription(IComponentIdentifier cid)
	{
		return partners!=null? partners.get(cid).getSubscription(): null;
	}
	
	/**
	 *  Called when service terminates.
	 */
	@ServiceShutdown
	public void shutdown(Exception e)
	{
		e.printStackTrace();	
	}
	
	/**
	 *  Add a new subscription.
	 *  @param future The subscription info.
	 */
	protected void addSubscribedTo(PeerInfo info)
	{
		if(subscribedto==null)
		{
			subscribedto = new HashSet<PeerInfo>();
//			subscribedto = LeaseTimeSet.createLeaseTimeCollection((long)(2.2*lrobs.getTimeLimit()), new ICommand<SubscriptionInfo>()
//			{
//				public void execute(SubscriptionInfo entry) 
//				{
//					System.out.println("Remove subscription of: "+entry.getPlatformId());
////					getRegistry().removeSubregistry(entry.getPlatformId());
//					
//					// Remove services of other superpeer
//					getRegistry().removeServices(entry.getPlatformId());
//					// Necessary?! Should not have queries of other superpeers
//					getRegistry().removeQueriesFromPlatform(entry.getPlatformId()); 
//				}
//			}, new AgentDelayRunner(component), false, null);
		}
		
//		subscribedto.update(info);
		subscribedto.add(info);
	}
	
	/**
	 *  Remove an existing subscription.
	 *  @param platform The component id to remove.
	 */
	protected void removeSubscribedTo(PeerInfo info)
	{
		if(subscribedto==null || !subscribedto.contains(info))
			throw new RuntimeException("SubscribedTo not known: "+info);
		subscribedto.remove(info);
	}
	
	/**
	 *  Check if an id is in the set of platform that this platform has subscribed to (to receive updates for sync).
	 *  @param cid The id.
	 *  @return True if is subscribed.
	 */
	protected boolean containsSubscribedTo(IComponentIdentifier cid)
	{
		boolean ret = false;
		
		if(subscribedto!=null)
		{
			for(PeerInfo si: subscribedto)
			{
				if(si.getPlatformId().equals(cid))
				{
					ret = true;
					break;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Add a known platform.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected void addBlacklistedPlatform(IComponentIdentifier cid, long leasetime)
	{
		if(blplatforms==null)
		{
//			blackplatforms = new HashSet<IComponentIdentifier>();
			blplatforms = new LeaseTimeSet<IComponentIdentifier>(delay, new ICommand<Tuple2<IComponentIdentifier, Long>>()
			{
				public void execute(Tuple2<IComponentIdentifier, Long> tup)
				{
					// Exponential backoff
					Long lt = tup.getSecondEntity();
					if(lt<1000000) // more than 16 mins
						lt = (long)(lt*1.2);
//					leasetimes.put(cid, lt);
					
//					System.out.println("lease over for: "+tup.getFirstEntity()+" "+lt);
					
					newPlatformFound(tup.getFirstEntity(), lt);
				}
			}, new AgentDelayRunner(component));
		}
		blplatforms.add(cid, leasetime>0? leasetime: delay);
	}
	
	/**
	 *  Test if has a subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected boolean isBlacklistedPlatform(IComponentIdentifier cid)
	{
		return blplatforms!=null? blplatforms.contains(cid): false;
	}
	
	/**
	 *  Remove an existing subscription.
	 *  @param cid The component id to remove.
	 */
	protected void removeBlacklistedPlatforms(IComponentIdentifier cid)
	{
//		if(blackplatforms==null || !blackplatforms.contains(cid))
//			throw new RuntimeException("platform not blacklisted: "+cid);
		if(blplatforms!=null)
			blplatforms.remove(cid);
	}
	
	/**
	 *  Get the registry.
	 *  @return The registry.
	 */
	protected IServiceRegistry getRegistry()
	{
		return ServiceRegistry.getRegistry(component.getId());
	}

	/**
	 *  Get the component.
	 *  @return The component.
	 */
	public IInternalAccess getComponent()
	{
		return component;
	}
	
	
	//-------- remote registry access --------
	
	/**
	 *  Add (i.e. register) a query
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query)
	{
//		System.out.println("Adding query: "+query);
		ISubscriptionIntermediateFuture<T>	ret	= getRegistry().addQuery(query);
		
		// As registry is plain object -> need to add no-timeout here. (hack???)
		SFuture.avoidCallTimeouts((Future<?>)ret, getComponent());
		
		return ret;
	}
	
	/**
	 *  Remove a query.
	 */
	public <T> IFuture<Void> removeQuery(ServiceQuery<T> query)
	{
		//TODO
//		getRegistry().removeQuery(query);
		return IFuture.DONE;
	}
	
//	/**
//	 *  Info struct for subscriptions.
//	 */
//	public static class SubscriptionInfo extends ClientInfo
//	{
//		/** The subscription. */
//		protected ISubscriptionIntermediateFuture<ARegistryEvent> subscription;
//		
//		/**
//		 * Create a new RegistrySynchronizationService.
//		 */
//		public SubscriptionInfo(IComponentIdentifier platformid, ISubscriptionIntermediateFuture<ARegistryEvent> subscription)
//		{
//			super(platformid);
//			this.subscription = subscription;
//		}
//
//		/**
//		 *  Get the subscription.
//		 *  @return the subscription
//		 */
//		public ISubscriptionIntermediateFuture<ARegistryEvent> getSubscription()
//		{
//			return subscription;
//		}
//
//		/**
//		 *  Set the subscription.
//		 *  @param subscription The subscription to set
//		 */
//		public void setSubscription(ISubscriptionIntermediateFuture<ARegistryEvent> subscription)
//		{
//			this.subscription = subscription;
//		}
//	}
}
