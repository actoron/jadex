package jadex.platform.service.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.PlatformServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.registry.IRegistryEvent;
import jadex.bridge.service.types.registry.IRegistryService;
import jadex.bridge.service.types.registry.RegistryEvent;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;

/**
 *  Registry service for synchronization with remote platforms. 
 */
public class RegistryService implements IRegistryService
{
	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;
	
	/** The subscriptions of other platforms (platform cid -> subscription info). */
	protected Map<IComponentIdentifier, SubscriptionIntermediateFuture<IRegistryEvent>> subscriptions;
	
	/** The locally cloned registries of remote platforms. */
	protected Map<IComponentIdentifier, PlatformServiceRegistry> registries;
	
	/** The platforms this registry has subscribed to. */
	protected Set<IComponentIdentifier> subscribedto;
	
	/**
	 *  Start of the service.
	 */
	@ServiceStart
	public void init()
	{
		IAwarenessManagementService awas = SServiceProvider.getLocalService(component, IAwarenessManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		
		// Subscribe to awareness service to get informed when new platforms are discovered
		// todo: does not work without awareness
		
		awas.subscribeToPlatformList(true).addIntermediateResultListener(new IIntermediateResultListener<DiscoveryInfo>()
		{
			public void intermediateResultAvailable(DiscoveryInfo dis)
			{
				// Found a new platform -> search registry service and subscribe
				
				final IComponentIdentifier cid = dis.getComponentIdentifier();
				
				if(hasSubscribedTo(cid))
				{
//					System.out.println("Ignoring: already subscribed to: "+cid+" (I am: "+component.getComponentIdentifier()+")");
				}
				else
				{
					addSubscribedTo(cid);
					
					SServiceProvider.getService(component, cid, RequiredServiceInfo.SCOPE_PLATFORM, IRegistryService.class, false)
						.addResultListener(new IResultListener<IRegistryService>()
					{
						public void resultAvailable(IRegistryService regser)
						{
							// Subscribe to the new remote registry
							
							System.out.println("Found registry service on: "+cid+" (I am: "+component.getComponentIdentifier()+")");
							
							regser.subscribeToEvents().addIntermediateResultListener(new IIntermediateResultListener<IRegistryEvent>()
							{
								public void intermediateResultAvailable(IRegistryEvent event)
								{
									System.out.println("Received: "+event);
									
									PlatformServiceRegistry reg = getRegistry(cid);
									
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
									System.out.println("Exception in subscription with: "+cid+" (I am: "+component.getComponentIdentifier()+")");
									removeSubscribedTo(cid);
								}
							});
						}
						
						public void exceptionOccurred(Exception exception)
						{
							// No registry service on that platform or no access
							
	//						System.out.println("Found no registry service on: "+cid);
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
	}
	
	/**
	 *  Subscribe to change events of the registry. 
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
		
		PlatformServiceRegistry reg = PlatformServiceRegistry.getRegistry(component.getComponentIdentifier().getRoot());
		RegistryEvent event = new RegistryEvent(reg.getServiceMap(), null);
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
	 *  Get the registry per platform identifier.
	 *  @param cid The component identifier.
	 *  @return The registry.
	 */
	protected PlatformServiceRegistry getRegistry(IComponentIdentifier cid)
	{
		if(registries==null)
			registries = new HashMap<IComponentIdentifier, PlatformServiceRegistry>();
		PlatformServiceRegistry ret = registries.get(cid);
		if(ret==null)
		{
			ret = new PlatformServiceRegistry();
			addRegistry(cid, ret);
		}
		return ret;
	}
	
	/**
	 *  Add a new registry.
	 *  @param registry The registry.
	 */
	protected void addRegistry(IComponentIdentifier cid, PlatformServiceRegistry registry)
	{
		if(registries==null)
			registries = new HashMap<IComponentIdentifier, PlatformServiceRegistry>();
		if(registries.containsKey(cid))
			throw new RuntimeException("Registry already contained: "+cid);
		registries.put(cid, registry);
	}
	
	/**
	 *  Remove an existing registry.
	 *  @param cid The component id to remove.
	 */
	protected void removeRegistry(IComponentIdentifier cid)
	{
		if(registries==null || !registries.containsKey(cid))
			throw new RuntimeException("Registry not contained: "+cid);
		registries.remove(cid);
	}
}
