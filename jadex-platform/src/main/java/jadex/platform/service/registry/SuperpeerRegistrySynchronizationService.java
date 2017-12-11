package jadex.platform.service.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
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
	
	/** The subscriptions of other platforms (superpeers) (platform cid -> subscription info). */
	protected Map<IComponentIdentifier, SubscriptionIntermediateFuture<ARegistryEvent>> subscriptions;
	
	/** The platforms this registry has subscribed to. The other superpeers will send registry updates to me. */
	protected Set<SubscriptionInfo> subscribedto;
	
	/** The currently blacklisted platforms (are not checked when newPlatformArrived() is called). */
//	protected Set<IComponentIdentifier> blackplatforms;
	protected LeaseTimeSet<IComponentIdentifier> blackplatforms;
	
	/** The client platforms that are managed by this super-peer. */
	protected LeaseTimeMap<IComponentIdentifier, ClientInfo> clients; 
	
	/** Local registry observer. */
	protected LocalRegistryObserver lrobs;
	
	/** Event collector for parent. */
	protected MultiEventCollector mevcol;
	
	/** The registry level.*/
	protected int level;
	
	/** The super-super-peer. */
	protected IComponentIdentifier ssp;
	
	/** Potential superpeers. */
	protected List<IComponentIdentifier> potssps;
	
	/** The search functionality. */
	protected PeerSearchFunctionality psfunc;
	
	/** Use awareness to find other superpeers. */
	protected boolean useawa = true;
	
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
		
		System.out.println("SuperpeerRegistrySynchronizationService: level="+level);
	}
	
	/**
	 *  Start of the service.
	 */
	@ServiceStart
	public void init()
	{
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
		lrobs = new LocalRegistryObserver(component.getComponentIdentifier(), new AgentDelayRunner(component), true)//, eventslimit, timelimit)
		{
			public void notifyObservers(ARegistryEvent event)
			{
				// Only local changes are propagated (scope in query is platform)
//				System.out.println("Event: "+event);
				forwardRegistryEventToPartner(event);
			}
		};
		
		// Event collector for the supersuperpeer
		if(level==1)
		{
			mevcol = new MultiEventCollector(component.getComponentIdentifier(), new AgentDelayRunner(component))
			{
				@Override
				public void notifyObservers(ARegistryEvent event)
				{
					forwardRegistryEventToParent(event, false);
				}
			};
		}
		// Try to contact peer supersuperpeers regularily
		else if(level==0)
		{
			component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
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
				IAwarenessManagementService awas = SServiceProvider.getLocalService(component, IAwarenessManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
				
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
				
				component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						Collection<IProxyAgentService> sers = SServiceProvider.getLocalServices(component, IProxyAgentService.class, RequiredServiceInfo.SCOPE_PLATFORM);
	
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
						
						component.getComponentFeature(IExecutionFeature.class).waitForDelay(10000, this, true);
						
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
		psfunc.getPeer(force).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, ISuperpeerRegistrySynchronizationService>(ret)
		{
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
	protected void forwardRegistryEventToPartner(ARegistryEvent event)
	{
		if(subscriptions!=null)
		{
//			System.out.println("Sending sync update: "+event);
			for(SubscriptionIntermediateFuture<ARegistryEvent> fut: subscriptions.values())
			{
				fut.addIntermediateResult(event);
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
							
							// Send full status if receiver said I was removed
							if(re.isRemoved())
							{
								RegistryEvent event = new RegistryEvent(true, ARegistryEvent.CLIENTTYPE_SUPERPEER_LEVEL1);
								event.addAddedService((IService)SServiceProvider.getLocalService(component, ISuperpeerRegistrySynchronizationService.class));
								ssp.updateClientData(event).addResultListener(this);
	//							System.out.println("Send update to superpeer: "+((IService)spregser).getServiceIdentifier().getProviderId());
							}
						}
						
						// todo: tell client (not only superpeer!) to send full update
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
				System.out.println("Could not forward client service data to supersuperpeer: "+exception);
				
				if(!force)
					forwardRegistryEventToParent(event, true);
			}
		});
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
		final IComponentIdentifier cid = ((IService)regser).getServiceIdentifier().getProviderId();

		// Do not announce platform itself
		if(cid.getRoot().equals(component.getComponentIdentifier().getRoot()))
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
////					return SServiceProvider.getService(component, cid, RequiredServiceInfo.SCOPE_PLATFORM, ISuperpeerRegistrySynchronizationService.class, false);
//					return SServiceProvider.getService(component, query);
//				}
//			}, 3, 10000).addResultListener(new IResultListener<ISuperpeerRegistrySynchronizationService>()
//			{
//				public void resultAvailable(final ISuperpeerRegistrySynchronizationService regser)
//				{
			
			regser.getLevel().addResultListener(new IResultListener<Integer>()
			{
				public void resultAvailable(Integer result)
				{
					// If superpeer on same level was found it will be used for synchronization
					if(level==result.intValue())
					{
						// Subscribe to the new remote registry
						boolean unr = ((IService)regser).getServiceIdentifier().isUnrestricted();

						System.out.println("Found registry service on: "+cid+(unr? " unrestricted": " default")+" (I am: "+component.getComponentIdentifier()+")");
						
						ISubscriptionIntermediateFuture<ARegistryEvent> fut = regser.subscribeToEvents();
						final SubscriptionInfo info = new SubscriptionInfo(cid, fut);
						
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
								
								handleRegistryEvent(event);
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
										System.out.println("Exception in my subscription with: "+cid+" (I am: "+component.getComponentIdentifier()+")");
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
						System.out.println("Found superpeer of other level: "+level+" "+((IService)regser).getServiceIdentifier()+" "+result);
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
		return new Future<Collection<IComponentIdentifier>>(subscriptions.keySet());
	}
	
	/**
	 *  Get the current clients.
	 */
	public IFuture<Collection<IComponentIdentifier>> getClients()
	{
		List<IComponentIdentifier> ret = new ArrayList<IComponentIdentifier>();
		Collection<ClientInfo> cis = clients.values();
		for(ClientInfo ci: cis)
		{
			ret.add(ci.getPlatformId());
		}
		return new Future<Collection<IComponentIdentifier>>(ret);
	}
		
	/**
	 *  Handle the update event of a registry.
	 *  @param event The event.
	 */
	protected void handleRegistryEvent(ARegistryEvent ev)
	{
		IServiceRegistry reg = getRegistry();
		
		if(ev instanceof RegistryEvent)
		{
			RegistryEvent event = (RegistryEvent)ev;
			// Only add if registry is multi type
			Set<IService> added = event.getAddedServices();
			if(added!=null)
			{
				for(IService ser: added)
				{
	//				System.out.println("added ser: "+ser);
					reg.addService(ser);
				}
			}
			
			Set<IService> removed = event.getRemovedServices();
			if(removed!=null)
			{
				for(IService ser: removed)
				{
	//				System.out.println("removed ser: "+ser);
					reg.removeService(ser);
				}
			}
		}
		else if(ev instanceof MultiRegistryEvent)
		{
			MultiRegistryEvent event = (MultiRegistryEvent)ev;
		
			if(event.getEvents()!=null)
			{
				for(ARegistryEvent e: event.getEvents())
				{
					handleRegistryEvent(e);
				}
			}
		}
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
			SubscriptionInfo[] evs = subscribedto.toArray(new SubscriptionInfo[subscribedto.size()]);
			for(SubscriptionInfo info: evs)
			{
				info.getSubscription().terminate();
			}
		}
		
		// Finish subscriptions of other platforms 
		if(subscriptions!=null)
		{
			SubscriptionIntermediateFuture<ARegistryEvent>[] evs = subscriptions.values().toArray(new SubscriptionIntermediateFuture[subscriptions.size()]);
			for(SubscriptionIntermediateFuture<ARegistryEvent> fut: evs)
			{
				fut.setFinished();
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
		if(hasSubscription(cid))
			return getSubscription(cid);
		
		System.out.println("New subscription from: "+cid);
		
		final SubscriptionIntermediateFuture<ARegistryEvent> ret = (SubscriptionIntermediateFuture<ARegistryEvent>)
			SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, component);
		
		ITerminationCommand tcom = new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				removeSubscription(cid);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		};
		ret.setTerminationCommand(tcom);
		
		addSubscription(cid, ret);
		
		// Forward current state initially
		ARegistryEvent ev = lrobs.getCurrentStateEvent();
		System.out.println("Sending full state: "+component.getComponentIdentifier()+": "+ev);
		ret.addIntermediateResult(ev);
		
		return ret;
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
		
//		System.out.println("received event from client: "+event);
		
		final IComponentIdentifier cid = ServiceCall.getCurrentInvocation().getCaller().getRoot();

		// Client can be level1 superpeer -> services are not only from superpeer but from clients
		if(clients==null)
		{
			clients = new LeaseTimeMap<IComponentIdentifier, ClientInfo>((long)(2.2*lrobs.getTimeLimit()), new ICommand<Tuple2<IComponentIdentifier, Long>>()
			{
				public void execute(Tuple2<IComponentIdentifier, Long> tup) 
				{
					System.out.println("Removed peer: "+cid);
					
					// Remove services and queries of client
					
					// todo: remove also services of clients
					
					getRegistry().removeServices(tup.getFirstEntity());
					getRegistry().removeQueriesFromPlatform(tup.getFirstEntity());
				}
			}, false, true, new AgentDelayRunner(component), false);
		}
		
		ClientInfo ci = clients.get(cid);
		
		boolean existed = true;
		if(ci==null)
		{
			ci = new ClientInfo(cid);
			existed = false;
		}
//		System.out.println("new lease time for: "+cid+" "+System.currentTimeMillis()+"  "+lrobs.getTimeLimit());
		clients.put(cid, ci);
		
//		if(event.size()>0)
//			System.out.println("Client update request from: "+cid+" size:"+event.size()+" delta: "+event.isDelta());
		
		handleRegistryEvent(event);
		
		// forward client updates to all other partner superpeers
		forwardRegistryEventToPartner(event);
		
		// collect client
//		forwardRegistryEventToParent(event);
		addEventForParent(event);
		
		// Prepare response
		if(event instanceof RegistryEvent)
		{
			RegistryEvent re = (RegistryEvent)event;
			ARegistryResponseEvent res = createResponseEvent(re.isDelta() && !existed, re.getNetworkNames());
			ret.setResult(res);
		}
		else if(event instanceof MultiRegistryEvent)
		{
			MultiRegistryEvent mre = (MultiRegistryEvent)event;
			if(mre.getEvents()!=null)
			{
				MultiRegistryResponseEvent mp = new MultiRegistryResponseEvent();
				for(ARegistryEvent e: mre.getEvents())
				{
					RegistryEvent re = (RegistryEvent)event;
					ARegistryResponseEvent res = createResponseEvent(re.isDelta() && !existed, re.getNetworkNames());
					mp.addEvent(res);
				}
				ret.setResult(mp);
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param event
	 */
	protected void addEventForParent(ARegistryEvent ev)
	{
		if(level!=-1)
			return;
		
		if(ev instanceof RegistryEvent)
		{
			RegistryEvent event = (RegistryEvent)ev;
		
			// Forward global scope events to parent
			Set<IService> added = null;
			if(event.getAddedServices()!=null)
			{
				added = new HashSet<IService>();
				for(IService ser: event.getAddedServices())
				{
					String scope = ser.getServiceIdentifier().getScope();
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
					String scope = ser.getServiceIdentifier().getScope();
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
				nev.setAddedServices(added);
				nev.setRemovedServices(rem);
				mevcol.addEvent(nev);
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected ARegistryResponseEvent createResponseEvent(boolean removed, String[] networknames)
	{
		// Special handling for superpeer clients of level 1
		// Sends back other network-compatible superpeers of level 1
		RegistryResponseEvent res = new RegistryResponseEvent(removed, lrobs.getTimeLimit());
//		if(IRegistryEvent.CLIENTTYPE_SUPERPEER_LEVEL1.equals(event.getClientType()))
//		if(IRegistryEvent.CLIENTTYPE_CLIENT.equals(event.getClientType()))
		
		// If I am a level 0 superpeer, I inform others about all known level 1 registries
		if(level==0)
		{
			ServiceQuery<IService> query = new ServiceQuery<IService>(ISuperpeerRegistrySynchronizationService.class, Binding.SCOPE_GLOBAL, null, component.getComponentIdentifier(), null);
//			IMsgSecurityInfos secinfo = (IMsgSecurityInfos)ServiceCall.getCurrentInvocation().getProperty("securityinfo");
			query.setNetworkNames(networknames);
			
			Set<IService> sers = getRegistry().searchServicesSync(query);
			if(sers!=null)
			{
				for(Iterator<IService> it=sers.iterator(); it.hasNext(); )
				{
					IService ser = it.next();
					if(clients==null || !clients.containsKey(ser.getServiceIdentifier().getProviderId().getRoot()))
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
	protected void addSubscription(IComponentIdentifier cid, SubscriptionIntermediateFuture<ARegistryEvent> future)
	{
		if(subscriptions==null)
			subscriptions = new LinkedHashMap<IComponentIdentifier, SubscriptionIntermediateFuture<ARegistryEvent>>();
		if(subscriptions.containsKey(cid))
			throw new RuntimeException("Platform already contained: "+cid);
		subscriptions.put(cid, future);
	}
	
	/**
	 *  Test if has a subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected boolean hasSubscription(IComponentIdentifier cid)
	{
		return subscriptions!=null? subscriptions.containsKey(cid): false;
	}
	
	/**
	 *  Remove an existing subscription.
	 *  @param cid The component id to remove.
	 */
	protected void removeSubscription(IComponentIdentifier cid)
	{
		if(subscriptions==null || !subscriptions.containsKey(cid))
			throw new RuntimeException("Subscriber not known: "+cid);
		subscriptions.remove(cid);
	}
	
	/**
	 *  Get a subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected ISubscriptionIntermediateFuture<ARegistryEvent> getSubscription(IComponentIdentifier cid)
	{
		return subscriptions!=null? subscriptions.get(cid): null;
	}
	
	@ServiceShutdown
	public void shutdown(Exception e)
	{
		e.printStackTrace();	
	}
	
	/**
	 *  Add a new subscription.
	 *  @param future The subscription info.
	 */
	protected void addSubscribedTo(SubscriptionInfo info)
	{
		if(subscribedto==null)
		{
			subscribedto = new HashSet<SuperpeerRegistrySynchronizationService.SubscriptionInfo>();
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
	protected void removeSubscribedTo(SubscriptionInfo info)
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
			for(SubscriptionInfo si: subscribedto)
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
		if(blackplatforms==null)
		{
//			blackplatforms = new HashSet<IComponentIdentifier>();
			blackplatforms = new LeaseTimeSet<IComponentIdentifier>(delay, new ICommand<Tuple2<IComponentIdentifier, Long>>()
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
		blackplatforms.add(cid, leasetime>0? leasetime: delay);
	}
	
	/**
	 *  Test if has a subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected boolean isBlacklistedPlatform(IComponentIdentifier cid)
	{
		return blackplatforms!=null? blackplatforms.contains(cid): false;
	}
	
	/**
	 *  Remove an existing subscription.
	 *  @param cid The component id to remove.
	 */
	protected void removeBlacklistedPlatforms(IComponentIdentifier cid)
	{
//		if(blackplatforms==null || !blackplatforms.contains(cid))
//			throw new RuntimeException("platform not blacklisted: "+cid);
		if(blackplatforms!=null)
			blackplatforms.remove(cid);
	}
	
	/**
	 *  Get the registry.
	 *  @return The registry.
	 */
	protected IServiceRegistry getRegistry()
	{
		return ServiceRegistry.getRegistry(component.getComponentIdentifier());
	}
	
	/**
	 *  Info struct for client (reverse subscriptions).
	 */
	public static class ClientInfo
	{
		/** The component identifier of the platform. */
		protected IComponentIdentifier platformid;
	
		/** The timestamp of the last received message. */
		protected long timestamp;

		/**
		 * Create a new RegistrySynchronizationService.
		 */
		public ClientInfo(IComponentIdentifier platformid)
		{
			this.platformid = platformid;
		}

		/**
		 *  Get the timestamp.
		 *  @return the timestamp
		 */
		public long getTimestamp()
		{
			return timestamp;
		}

		/**
		 *  Set the timestamp.
		 *  @param timestamp The timestamp to set
		 */
		public void setTimestamp(long timestamp)
		{
			this.timestamp = timestamp;
		}

		/**
		 *  Get the platformId.
		 *  @return The platformId
		 */
		public IComponentIdentifier getPlatformId()
		{
			return platformid;
		}

		/**
		 *  Set the platformId.
		 *  @param platformId The platformId to set
		 */
		public void setPlatformId(IComponentIdentifier platformId)
		{
			this.platformid = platformId;
		}
		
//		/**
//		 *  Check if this 
//		 *  @param True, if the event is due and should be sent.
//		 */
//		public boolean isLeaseTimeOk()
//		{
//			return System.currentTimeMillis()-timestamp>timelimit;
//		}
	}
	
	/**
	 *  Info struct for subscriptions.
	 */
	public static class SubscriptionInfo extends ClientInfo
	{
		/** The subscription. */
		protected ISubscriptionIntermediateFuture<ARegistryEvent> subscription;
		
		/**
		 * Create a new RegistrySynchronizationService.
		 */
		public SubscriptionInfo(IComponentIdentifier platformid, ISubscriptionIntermediateFuture<ARegistryEvent> subscription)
		{
			super(platformid);
			this.subscription = subscription;
		}

		/**
		 *  Get the subscription.
		 *  @return the subscription
		 */
		public ISubscriptionIntermediateFuture<ARegistryEvent> getSubscription()
		{
			return subscription;
		}

		/**
		 *  Set the subscription.
		 *  @param subscription The subscription to set
		 */
		public void setSubscription(ISubscriptionIntermediateFuture<ARegistryEvent> subscription)
		{
			this.subscription = subscription;
		}
	}
	
}
