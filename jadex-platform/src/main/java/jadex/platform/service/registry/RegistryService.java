package jadex.platform.service.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.management.ServiceNotFoundException;

import jadex.bridge.ClassInfo;
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
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.search.MultiServiceRegistry;
import jadex.bridge.service.search.SynchronizedServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.registry.IRegistryEvent;
import jadex.bridge.service.types.registry.IRegistryListener;
import jadex.bridge.service.types.registry.IRegistryService;
import jadex.bridge.service.types.registry.RegistryEvent;
import jadex.bridge.service.types.registry.RegistryListenerEvent;
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
	
	/** The multi registry. */
	protected MultiServiceRegistry registry;
	
	/** The platforms this registry has subscribed to. */
	protected Set<IComponentIdentifier> subscribedto;
	
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
		this.eventslimit = 50;
		this.timelimit = 5000;
		
		// Subscribe to awareness service to get informed when new platforms are discovered
		// todo: does not work without awareness
		
		IAwarenessManagementService awas = SServiceProvider.getLocalService(component, IAwarenessManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		
		awas.subscribeToPlatformList(true).addIntermediateResultListener(new IIntermediateResultListener<DiscoveryInfo>()
		{
			public void intermediateResultAvailable(DiscoveryInfo dis)
			{
				// Found a new platform -> search registry service and subscribe
				
				final IComponentIdentifier cid = dis.getComponentIdentifier();
	
//				System.out.println("Found platform: "+cid+" (I am: "+component.getComponentIdentifier()+")");
				
				if(hasSubscribedTo(cid))
				{
//					System.out.println("Ignoring: already subscribed to: "+cid+" (I am: "+component.getComponentIdentifier()+")");
				}
				else
				{
					addSubscribedTo(cid);
					
					searchRegistryService(cid, 0, 3, 10000).addIntermediateResultListener(new IIntermediateResultListener<IRegistryEvent>()
					{
						public void intermediateResultAvailable(IRegistryEvent event)
						{
							System.out.println("Received an update event from: "+cid+", size="+event.size());
							
							MultiServiceRegistry reg = getRegistry();
							
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
						
						public void resultAvailable(Collection<IRegistryEvent> result)
						{
							System.out.println("Should not happen");
						}
						
						public void finished()
						{
							System.out.println("Subscription finbished: "+cid);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							if(exception instanceof ServiceNotFoundException)
							{
								System.out.println("No registry service found, giving up: "+cid+" (I am: "+component.getComponentIdentifier()+")");
							}
							else
							{
								System.out.println("Exception in subscription with: "+cid+" (I am: "+component.getComponentIdentifier()+")");
							}
							removeSubscribedTo(cid);
						}
					});
				}
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
		
		// Subscribe to changes of the local registry to inform other platforms
		
		SynchronizedServiceRegistry reg = SynchronizedServiceRegistry.getRegistry(component.getComponentIdentifier().getRoot());
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
						ret.setException(new ServiceNotFoundException());
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
		
		SynchronizedServiceRegistry reg = SynchronizedServiceRegistry.getRegistry(component.getComponentIdentifier().getRoot());
		reg.removeEventListener(this);
		
		
		// Remove this platform from all subscriptions on other platforms
		
		// todo
		
		// Finish subscriptions of other platforms 
	
		// todo
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
		
		SynchronizedServiceRegistry reg = SynchronizedServiceRegistry.getRegistry(component.getComponentIdentifier().getRoot());
		RegistryEvent event = new RegistryEvent(reg.getServiceMap(), null, eventslimit, timelimit);
		ret.addIntermediateResult(event);
		
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
	protected void addSubscribedTo(IComponentIdentifier cid)
	{
		if(subscribedto==null)
			subscribedto = new HashSet<IComponentIdentifier>();
		subscribedto.add(cid);
	}
	
	/**
	 *  Test if has a subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected boolean hasSubscribedTo(IComponentIdentifier cid)
	{
		return subscribedto!=null? subscribedto.contains(cid): false;
	}
	
	/**
	 *  Remove an existing subscription.
	 *  @param cid The component id to remove.
	 */
	protected void removeSubscribedTo(IComponentIdentifier cid)
	{
		if(subscribedto==null || !subscribedto.contains(cid))
			throw new RuntimeException("SubscribedTo not known: "+cid);
		subscribedto.remove(cid);
	}
	
	/**
	 *  Get the registry.
	 *  @return The registry.
	 */
	protected MultiServiceRegistry getRegistry()
	{
		if(registry==null)
			registry = new MultiServiceRegistry();
		return registry;
	}
}
