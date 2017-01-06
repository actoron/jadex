package jadex.platform.service.registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITransportComponentIdentifier;
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
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.registry.IRegistryEvent;
import jadex.bridge.service.types.registry.IRegistryListener;
import jadex.bridge.service.types.registry.IRegistrySynchronizationService;
import jadex.bridge.service.types.registry.RegistryEvent;
import jadex.bridge.service.types.registry.RegistryListenerEvent;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.commons.ICommand;
import jadex.commons.IResultCommand;
import jadex.commons.collection.ILeaseTimeSet;
import jadex.commons.collection.LeaseTimeSet;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  Registry service for synchronization with remote platforms. 
 */
public class RegistrySynchronizationService implements IRegistrySynchronizationService, IRegistryListener
{
	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;
	
	/** The subscriptions of other platforms (platform cid -> subscription info). */
	protected Map<IComponentIdentifier, SubscriptionIntermediateFuture<IRegistryEvent>> subscriptions;
	
	/** The platforms this registry has subscribed to. */
	protected ILeaseTimeSet<SubscriptionInfo> subscribedto;
	protected Set<IComponentIdentifier> knownplatforms;
	
	
	/** The current registry event (is accumulated). */
	protected RegistryEvent registryevent;
	
	/** The max number of events to collect before sending a bunch event. */
	protected int eventslimit;
	
	/** The timelimit for sending events even when only few have arrived. */
	protected long timelimit;
			
	/**
	 *  Start of the service.
	 */
	@ServiceStart
	public void init()
	{
		// Subscribe to changes of the local registry to inform other platforms
		IServiceRegistry reg = getRegistry().getSubregistry(component.getComponentIdentifier());
		if(reg==null)
			throw new IllegalArgumentException("Registry synchronization requires multi service registy to store results");
		reg.addEventListener(this);
		
		this.eventslimit = 50;
		this.timelimit = 5000;
		this.registryevent = new RegistryEvent();
		
		// Subscribe to awareness service to get informed when new platforms are discovered
		// todo: does not work without awareness
		
		try
		{
			IAwarenessManagementService awas = SServiceProvider.getLocalService(component, IAwarenessManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			
			awas.subscribeToPlatformList(true).addIntermediateResultListener(new IIntermediateResultListener<DiscoveryInfo>()
			{
				public void intermediateResultAvailable(DiscoveryInfo dis)
				{
					// Found a new platform -> search registry service and subscribe
					
					final IComponentIdentifier cid = dis.getComponentIdentifier();
					
					newPlatformFound(cid);
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
							ser.getRemoteComponentIdentifier().addResultListener(new IResultListener<ITransportComponentIdentifier>()
							{
								public void resultAvailable(ITransportComponentIdentifier rcid)
								{
									newPlatformFound(rcid);
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
		
		// Set up event notification timer
		
		component.getComponentFeature(IExecutionFeature.class).waitForDelay(timelimit, new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(registryevent!=null && registryevent.isDue())
					notifySubscribers();
				component.getComponentFeature(IExecutionFeature.class).waitForDelay(timelimit, this, true);
				return IFuture.DONE;
			}
		}, true);
	}
	
	/**
	 *  Called when a new platform was found.
	 */
	protected void newPlatformFound(final IComponentIdentifier cid)
	{
//		System.out.println("Found platform: "+cid+" (I am: "+component.getComponentIdentifier()+")");
		
//		System.out.println(getRegistry());
			
		if(isKnownPlatform(cid))
		{
//			System.out.println("Ignoring: already subscribed to: "+cid+" (I am: "+component.getComponentIdentifier()+")");
		}
		else
		{
			addKnownPlatform(cid);
			
			SServiceProvider.waitForService(component, new IResultCommand<IFuture<IRegistrySynchronizationService>, Void>()
			{
				public IFuture<IRegistrySynchronizationService> execute(Void args)
				{
					return SServiceProvider.getService(component, cid, RequiredServiceInfo.SCOPE_PLATFORM, IRegistrySynchronizationService.class, false);
				}
			}, 3, 10000).addResultListener(new IResultListener<IRegistrySynchronizationService>()
			{
				public void resultAvailable(IRegistrySynchronizationService regser)
				{
					// Subscribe to the new remote registry
					
					System.out.println("Found registry service on: "+cid+" (I am: "+component.getComponentIdentifier()+")");
					
					ISubscriptionIntermediateFuture<IRegistryEvent> fut = regser.subscribeToEvents();
					final SubscriptionInfo info = new SubscriptionInfo(cid, fut);
					
					addSubscribedTo(info);
										
					fut.addIntermediateResultListener(new IIntermediateResultListener<IRegistryEvent>()
					{
						public void intermediateResultAvailable(IRegistryEvent event)
						{
//							if(event.size()>0)
//								System.out.println("Received an update event from: "+cid+", size="+event.size()+" "+event.hashCode()
//									+" at: "+System.currentTimeMillis()+"(I am: "+component.getComponentIdentifier()+")");
							
							IServiceRegistry reg = getRegistry();
							
							info.setTimestamp(System.currentTimeMillis());
							subscribedto.update(info);
							
							// Only add if registry is multi type
							Map<ClassInfo, Set<IService>> added = event.getAddedServices();
							if(added!=null)
							{
								for(Map.Entry<ClassInfo, Set<IService>> entry: added.entrySet())
								{
									for(IService ser: entry.getValue())
									{
//										System.out.println("added ser: "+entry.getKey());
										reg.addService(entry.getKey(), ser);
									}
								}
							}
							
							Map<ClassInfo, Set<IService>> removed = event.getRemovedServices();
							if(removed!=null)
							{
								for(Map.Entry<ClassInfo, Set<IService>> entry: removed.entrySet())
								{
									for(IService ser: entry.getValue())
									{
//										System.out.println("removed ser: "+entry.getKey());
										reg.removeService(entry.getKey(), ser);
									}
								}
							}
						}
						
						public void resultAvailable(Collection<IRegistryEvent> result)
						{
							finished();
						}
						
						public void finished()
						{
							System.out.println("Subscription finbished: "+cid);
							removeKnownPlatforms(cid);
							removeSubscribedTo(info);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							if(exception instanceof ServiceNotFoundException)
							{
//								System.out.println("No registry service found, giving up: "+cid+" (I am: "+component.getComponentIdentifier()+")");
							}
							else
							{
								if(!(exception instanceof FutureTerminatedException)) // ignore terminate
								{
									System.out.println("Exception in my subscription with: "+cid+" (I am: "+component.getComponentIdentifier()+")");
//									exception.printStackTrace();
								}
								removeKnownPlatforms(cid);
								removeSubscribedTo(info);
							}
						}
					});
				}
				
				public void exceptionOccurred(Exception exception)
				{
					System.out.println("Found no registry service on: "+cid);
				}
			});
		}
	}
		
	/**
	 *  Listener method called on changes of the local registry.
	 */
	public void registryChanged(RegistryListenerEvent event)
	{
		if(registryevent==null)
			registryevent= new RegistryEvent();
		
		if(event.getType().equals(RegistryListenerEvent.Type.ADDED))
		{
			registryevent.addAddedService(event.getClassInfo(), event.getService());
		}
		else if(event.getType().equals(RegistryListenerEvent.Type.REMOVED))
		{
			registryevent.addRemovedService(event.getClassInfo(), event.getService());
		}
		
		if(registryevent.isDue())
			notifySubscribers();
	}
	
	/**
	 *  Called on shutdown.
	 */
	@ServiceShutdown
	public void destroy()
	{
		// Remove listener on local registry
		
		IServiceRegistry reg = ServiceRegistry.getRegistry(component.getComponentIdentifier());
		reg.getSubregistry(component.getComponentIdentifier()).removeEventListener(this);
		
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
			SubscriptionIntermediateFuture<IRegistryEvent>[] evs = subscriptions.values().toArray(new SubscriptionIntermediateFuture[subscriptions.size()]);
			for(SubscriptionIntermediateFuture<IRegistryEvent> fut: evs)
			{
				fut.setFinished();
			}
		}
	}
	
	/**
	 *  Notify all subscribed platforms that an event has occurred.
	 */
	protected void notifySubscribers()
	{
		if(subscriptions!=null)
		{
			for(SubscriptionIntermediateFuture<IRegistryEvent> fut: subscriptions.values())
			{
				fut.addIntermediateResult(registryevent);
			}
			//registryevent = null;
		}
		registryevent = new RegistryEvent();
	}
	
	/**
	 *  Others subscribe to change events of this registry. 
	 */
	public ISubscriptionIntermediateFuture<IRegistryEvent> subscribeToEvents()
	{
		final IComponentIdentifier cid = ServiceCall.getCurrentInvocation().getCaller().getRoot();
		
		// If already subscribed reuse existing future
		if(hasSubscription(cid))
			return getSubscription(cid);
		
		System.out.println("New subscription from: "+cid);
		
		final SubscriptionIntermediateFuture<IRegistryEvent> ret = (SubscriptionIntermediateFuture<IRegistryEvent>)
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
		IServiceRegistry reg = ServiceRegistry.getRegistry(component.getComponentIdentifier());
		IServiceRegistry subreg = reg.getSubregistry(component.getComponentIdentifier());
		if(subreg!=null)
		{
			RegistryEvent event = new RegistryEvent(subreg.getServiceMap(), null, eventslimit, timelimit);
			ret.addIntermediateResult(event);
		}
		
		return ret;
	}

	/**
	 *  Add a new subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected void addSubscription(IComponentIdentifier cid, SubscriptionIntermediateFuture<IRegistryEvent> future)
	{
		if(subscriptions==null)
			subscriptions = new LinkedHashMap<IComponentIdentifier, SubscriptionIntermediateFuture<IRegistryEvent>>();
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
	protected ISubscriptionIntermediateFuture<IRegistryEvent> getSubscription(IComponentIdentifier cid)
	{
		return subscriptions!=null? subscriptions.get(cid): null;
	}
	
	/**
	 *  Add a new subscription.
	 *  @param future The subscription info.
	 */
	protected void addSubscribedTo(SubscriptionInfo info)
	{
		if(subscribedto==null)
		{
			subscribedto = LeaseTimeSet.createLeaseTimeCollection((long)(2.2*timelimit), new ICommand<SubscriptionInfo>()
			{
				public void execute(SubscriptionInfo entry) 
				{
					System.out.println("Remove subscription of: "+entry.getPlatformId());
					getRegistry().removeSubregistry(entry.getPlatformId());
				}
			}, new AgentDelayRunner(component), false, null);
		}
		
		subscribedto.update(info);
	}
	
	/**
	 *  Remove an existing subscription.
	 *  @param cid The component id to remove.
	 */
	protected void removeSubscribedTo(SubscriptionInfo info)
	{
		if(subscribedto==null || !subscribedto.contains(info))
			throw new RuntimeException("SubscribedTo not known: "+info);
		subscribedto.remove(info);
	}
	
	/**
	 *  Add a known platform.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected void addKnownPlatform(IComponentIdentifier cid)
	{
		if(knownplatforms==null)
			knownplatforms = new HashSet<IComponentIdentifier>();
		knownplatforms.add(cid);
	}
	
	/**
	 *  Test if has a subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected boolean isKnownPlatform(IComponentIdentifier cid)
	{
		return knownplatforms!=null? knownplatforms.contains(cid): false;
	}
	
	/**
	 *  Remove an existing subscription.
	 *  @param cid The component id to remove.
	 */
	protected void removeKnownPlatforms(IComponentIdentifier cid)
	{
		if(knownplatforms==null || !knownplatforms.contains(cid))
			throw new RuntimeException("platform not known: "+cid);
		knownplatforms.remove(cid);
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
	 *  Info struct for subscriptions.
	 */
	public static class SubscriptionInfo
	{
		/** The subscription. */
		protected ISubscriptionIntermediateFuture<IRegistryEvent> subscription;
		
		/** The component identifier of the platform. */
		protected IComponentIdentifier platformid;
	
		/** The timestamp of the last received message. */
		protected long timestamp;

		/**
		 * Create a new RegistrySynchronizationService.
		 */
		public SubscriptionInfo(IComponentIdentifier platformid, ISubscriptionIntermediateFuture<IRegistryEvent> subscription)
		{
			this.platformid = platformid;
			this.subscription = subscription;
		}

		/**
		 *  Get the subscription.
		 *  @return the subscription
		 */
		public ISubscriptionIntermediateFuture<IRegistryEvent> getSubscription()
		{
			return subscription;
		}

		/**
		 *  Set the subscription.
		 *  @param subscription The subscription to set
		 */
		public void setSubscription(ISubscriptionIntermediateFuture<IRegistryEvent> subscription)
		{
			this.subscription = subscription;
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
}
