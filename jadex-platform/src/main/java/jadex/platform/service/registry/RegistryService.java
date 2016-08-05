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
import jadex.bridge.service.search.AbstractServiceRegistry;
import jadex.bridge.service.search.MultiServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.registry.IRegistryEvent;
import jadex.bridge.service.types.registry.IRegistryListener;
import jadex.bridge.service.types.registry.IRegistryService;
import jadex.bridge.service.types.registry.RegistryEvent;
import jadex.bridge.service.types.registry.RegistryListenerEvent;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  Registry service for synchronization with remote platforms. 
 */
public class RegistryService implements IRegistryService, IRegistryListener
{
	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;
	
	/** The subscriptions of other platforms (platform cid -> subscription info). */
	protected Map<IComponentIdentifier, SubscriptionIntermediateFuture<IRegistryEvent>> subscriptions;
	
	/** The platforms this registry has subscribed to. */
	protected Set<ISubscriptionIntermediateFuture<IRegistryEvent>> subscribedto;
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
		this.eventslimit = 1;
		this.timelimit = 5000;
		
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
					
					component.getComponentFeature(IExecutionFeature.class).waitForDelay(10000, this);
					
					return IFuture.DONE;
				}
			});
		}
		
		// Subscribe to changes of the local registry to inform other platforms
		AbstractServiceRegistry reg = getRegistry().getSubregistry(component.getComponentIdentifier());
		if(reg==null)
			reg = getRegistry();
		reg.addEventListener(this);
		
		// Set up event notification timer
		
		component.getComponentFeature(IExecutionFeature.class).waitForDelay(timelimit, new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(registryevent!=null && registryevent.isDue())
					notifySubscribers();
				component.getComponentFeature(IExecutionFeature.class).waitForDelay(timelimit, this);
				return IFuture.DONE;
			}
		});
	}
	
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
			
			final ISubscriptionIntermediateFuture<IRegistryEvent> fut = searchRegistryService(cid, 0, 3, 10000);
			addSubscribedTo(fut);
			
			fut.addIntermediateResultListener(new IIntermediateResultListener<IRegistryEvent>()
			{
				public void intermediateResultAvailable(IRegistryEvent event)
				{
					System.out.println("Received an update event from: "+cid+", size="+event.size()+" "+event.hashCode());
					
					AbstractServiceRegistry reg = getRegistry();
					
					// Only add if registry is multi type
					if(reg instanceof MultiServiceRegistry)
					{
						Map<ClassInfo, Set<IService>> added = event.getAddedServices();
						if(added!=null)
						{
							for(Map.Entry<ClassInfo, Set<IService>> entry: added.entrySet())
							{
								for(IService ser: entry.getValue())
								{
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
									reg.removeService(entry.getKey(), ser);
								}
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
					removeSubscribedTo(fut);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					if(exception instanceof ServiceNotFoundException)
					{
//						System.out.println("No registry service found, giving up: "+cid+" (I am: "+component.getComponentIdentifier()+")");
					}
					else
					{
						System.out.println("Exception in subscription with: "+cid+" (I am: "+component.getComponentIdentifier()+")");
						exception.printStackTrace();
						removeKnownPlatforms(cid);
						removeSubscribedTo(fut);
					}
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
			registryevent.addAddedService(event.getClassInfo(), event.getService());
		}
		
		if(registryevent.isDue())
			notifySubscribers();
	}
	
	/**
	 * 
	 */
	protected ISubscriptionIntermediateFuture<IRegistryEvent> searchRegistryService(final IComponentIdentifier cid, final int num, final int max, final long delay)
	{
		final SubscriptionIntermediateFuture<IRegistryEvent> ret = new SubscriptionIntermediateFuture<IRegistryEvent>();
		
		if(num<max)
		{
			SServiceProvider.getService(component, cid, RequiredServiceInfo.SCOPE_PLATFORM, IRegistryService.class, false)
				.addResultListener(new IResultListener<IRegistryService>()
			{
				public void resultAvailable(IRegistryService regser)
				{
					// Subscribe to the new remote registry
					
					System.out.println("Found registry service on: "+cid+" (I am: "+component.getComponentIdentifier()+")");
					
					regser.subscribeToEvents().addIntermediateResultListener(new IntermediateDelegationResultListener<IRegistryEvent>(ret));
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// No registry service on that platform or no access
					
					if(num+1<max)
					{
						component.getComponentFeature(IExecutionFeature.class).waitForDelay(delay)
							.addResultListener(new ExceptionDelegationResultListener<Void, Collection<IRegistryEvent>>(ret)
						{
							public void customResultAvailable(Void result) throws Exception
							{
								searchRegistryService(cid, num+1, max, delay).addResultListener(new IntermediateDelegationResultListener<IRegistryEvent>(ret));
							}
						});
					}
					else
					{
//						System.out.println("Found no registry service on: "+cid+" ("+(num+1)+"/"+max+")");
						ret.setException(new ServiceNotFoundException("Registry service not found on: "+cid));
					}
				}
			});
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
		
		AbstractServiceRegistry reg = AbstractServiceRegistry.getRegistry(component.getComponentIdentifier());
		reg.getSubregistry(component.getComponentIdentifier()).removeEventListener(this);
		
		// Remove this platform from all subscriptions on other platforms
		
		if(subscribedto!=null)
		{
			for(ISubscriptionIntermediateFuture<IRegistryEvent> fut: subscribedto)
			{
				fut.terminate();
			}
		}
		
		// Finish subscriptions of other platforms 
	
		if(subscriptions!=null)
		{
			for(SubscriptionIntermediateFuture<IRegistryEvent> fut: subscriptions.values())
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
			registryevent = null;
		}
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
		AbstractServiceRegistry reg = AbstractServiceRegistry.getRegistry(component.getComponentIdentifier());
		AbstractServiceRegistry subreg = reg.getSubregistry(component.getComponentIdentifier());
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
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected void addSubscribedTo(ISubscriptionIntermediateFuture<IRegistryEvent> fut)
	{
		if(subscribedto==null)
			subscribedto = new HashSet<ISubscriptionIntermediateFuture<IRegistryEvent>>();
		subscribedto.add(fut);
	}
	
	/**
	 *  Remove an existing subscription.
	 *  @param cid The component id to remove.
	 */
	protected void removeSubscribedTo(ISubscriptionIntermediateFuture<IRegistryEvent> fut)
	{
		if(subscribedto==null || !subscribedto.contains(fut))
			throw new RuntimeException("SubscribedTo not known: "+fut);
		subscribedto.remove(fut);
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
	protected AbstractServiceRegistry getRegistry()
	{
		return AbstractServiceRegistry.getRegistry(component.getComponentIdentifier());
	}
}
