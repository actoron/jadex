package jadex.platform.service.registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.registry.RegistryEvent;
import jadex.commons.IAsyncFilter;
import jadex.commons.collection.IDelayRunner;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Binding;

/**
 *  Observe the local registry for changes and notify interested observers.
 */
public abstract class LocalRegistryObserver extends EventCollector
{
	/** The local registry subscription. */
	protected ISubscriptionIntermediateFuture<ServiceEvent<IService>> localregsub;
	
	/** Flag if publication scope must be more than platform to add an event. */
	protected boolean globalscope;
	
	/**
	 *  Create a new registry observer.
	 */
	public LocalRegistryObserver(IComponentIdentifier cid, final IDelayRunner timer, boolean globalscope)
	{
		this(cid, timer, 50, 10000, globalscope);
	}
	
	/**
	 *  Create a new registry observer.
	 */
	public LocalRegistryObserver(IComponentIdentifier cid, final IDelayRunner timer, int eventslimit, final long timelimit, final boolean globalscope)
	{
		super(cid, timer, eventslimit, timelimit);
		this.globalscope = globalscope;
		
		// Subscribe to changes of the local registry to inform other platforms
//		ServiceQuery<ServiceEvent<IService>> query = new ServiceQuery<ServiceEvent<IService>>(ServiceEvent.CLASSINFO, (ClassInfo)null, Binding.SCOPE_PLATFORM, (IAsyncFilter)null, null, cid);
		
		// This is the query that is used to get change notifications from local registry
		ServiceQuery<ServiceEvent<IService>> query = new ServiceQuery<ServiceEvent<IService>>((ClassInfo)null, 
			Binding.SCOPE_PLATFORM, null, cid, (IAsyncFilter)null, ServiceEvent.CLASSINFO);
		
		localregsub = ServiceRegistry.getRegistry(cid).addQuery(query);
		localregsub.addIntermediateResultListener(new IIntermediateResultListener<ServiceEvent<IService>>()
		{
			public void intermediateResultAvailable(ServiceEvent<IService> event)
			{
//				System.out.println("Local registry changed: "+event);
				
				assert registryevent!=null;
				
				String pubscope = event.getService().getServiceIdentifier().getScope();
				if(!globalscope || !RequiredServiceInfo.isScopeOnLocalPlatform(pubscope))
				{
					if(event.getType() == ServiceEvent.SERVICE_ADDED 
						|| event.getType() == ServiceEvent.SERVICE_CHANGED)
					{
						((RegistryEvent)registryevent).addAddedService(event.getService());
					}
					else if(event.getType() == ServiceEvent.SERVICE_REMOVED)
					{
						((RegistryEvent)registryevent).addRemovedService(event.getService());
					}
				}
				
				if(registryevent.isDue())
				{
					notifyObservers(registryevent);
					registryevent = createEvent();
				}
			}

			public void exceptionOccurred(Exception exception)
			{
			}

			public void resultAvailable(Collection<ServiceEvent<IService>> result)
			{
			}

			public void finished()
			{
			}
		});
	}
	
	/**
	 *  Call when local observation should be terminated.
	 */
	public void terminate()
	{
		localregsub.terminate();
	}

	/**
	 *  Get the current state of the registry (owner=cid).
	 *  @return An event with the full state.
	 */
	public RegistryEvent getCurrentStateEvent(IComponentIdentifier owner)
	{
		IServiceRegistry reg = ServiceRegistry.getRegistry(cid);
		ServiceQuery<IService> query = new ServiceQuery<IService>((Class)null, Binding.SCOPE_PLATFORM, null, owner==null? cid: owner, null);
		Set<IService> added = reg.searchServicesSync(query);
		
		// Remove only non-globally-scoped services
//		Set<IComponentIdentifier> clients = new HashSet<IComponentIdentifier>();
		if(added!=null)
		{
			for(Iterator<IService> it=added.iterator(); it.hasNext(); )
			{
				IService ser = it.next();
//				clients.add(ser.getServiceIdentifier().getProviderId().getRoot());
				// Remove locally (platform) scoped events
				if(globalscope && RequiredServiceInfo.isScopeOnLocalPlatform(ser.getServiceIdentifier().getScope()))
					it.remove();
			}
		}
		RegistryEvent event = new RegistryEvent(added, null, eventslimit, timelimit, false, null);
//		clients.add(getComponentIdentifier());
//		event.setClients(clients);
		event.addClient(getComponentIdentifier().getRoot());
		event.setSender(getComponentIdentifier().getRoot());
		return event;
	}
}
