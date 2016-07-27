package jadex.platform.service.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
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
 * 
 */
public class RegistryService implements IRegistryService
{
	/** The component. */
	@Agent
	protected IInternalAccess component;
	
	/** The subscriptions of other platforms (platform cid -> subscription info). */
	protected Map<IComponentIdentifier, SubscriptionIntermediateFuture<IRegistryEvent>> subscriptions;
	
	/** The locally cloned registries of remote platforms. */
	protected Map<IComponentIdentifier, PlatformServiceRegistry> registries;
	
	/**
	 *  Start of the service.
	 */
	@ServiceStart
	public void init()
	{
		IAwarenessManagementService awas = SServiceProvider.getLocalService(component, IAwarenessManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		awas.subscribeToPlatformList(true).addIntermediateResultListener(new IIntermediateResultListener<DiscoveryInfo>()
		{
			public void intermediateResultAvailable(DiscoveryInfo dis)
			{
				// Found a new platform -> search registry service and subscribe
				
				final IComponentIdentifier cid = dis.getComponentIdentifier();
				
				SServiceProvider.getService(component, cid, IRegistryService.class)
					.addResultListener(new IResultListener<IRegistryService>()
				{
					public void resultAvailable(IRegistryService regser)
					{
						// Subscribe to the new remote registry
						
						regser.subscribeToEvents().addIntermediateResultListener(new IIntermediateResultListener<IRegistryEvent>()
						{
							public void intermediateResultAvailable(IRegistryEvent event)
							{
								System.out.println("Received: "+event);
								
								
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
								exception.printStackTrace();
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
					}
				});
			}
			
			public void resultAvailable(Collection<DiscoveryInfo> result)
			{
			}
			
			public void finished()
			{
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println();
			}
		});
	}
	
	/**
	 *  Subscribe to change events of the registry. 
	 */
	public ISubscriptionIntermediateFuture<IRegistryEvent> subscribeToEvents()
	{
		final SubscriptionIntermediateFuture<IRegistryEvent> ret = (SubscriptionIntermediateFuture<IRegistryEvent>)
			SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, component);
		
		final IComponentIdentifier cid = ServiceCall.getCurrentInvocation().getCaller().getRoot();
		
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
		Set<IService> addedservices = reg.getServices(null);
		RegistryEvent event = new RegistryEvent(addedservices, null);
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
	 *  Get the registry per platform identifier.
	 *  @param cid The component identifier.
	 *  @return The registry.
	 */
	protected PlatformServiceRegistry getRegistry(IComponentIdentifier cid)
	{
		return registries!=null? registries.get(cid): null;
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
