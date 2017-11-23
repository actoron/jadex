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
public abstract class LocalRegistryObserver
{
	/** The local platform id. */
	protected IComponentIdentifier cid;
	
	/** The local registry subscription. */
	protected ISubscriptionIntermediateFuture<ServiceEvent<IService>> localregsub;
	
	/** The current registry event (is accumulated). */
	protected RegistryEvent registryevent;
	
	/** The max number of events to collect before sending a bunch event. */
	protected int eventslimit;
	
	/** The timelimit for sending events even when only few have arrived. */
	protected long timelimit;
	
	/** The timer. */
	protected IDelayRunner timer;
	protected Runnable canceltimer;
	
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
		this.cid = cid;
		this.timer = timer;
		this.eventslimit = eventslimit;
		this.timelimit = timelimit;
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
				
				if(registryevent==null)
					registryevent= new RegistryEvent(true);
				
				String pubscope = event.getService().getServiceIdentifier().getScope();
				if(!globalscope || !RequiredServiceInfo.isScopeOnLocalPlatform(pubscope))
				{
					if(event.getType() == ServiceEvent.SERVICE_ADDED 
						|| event.getType() == ServiceEvent.SERVICE_CHANGED)
					{
						registryevent.addAddedService(event.getService());
					}
					else if(event.getType() == ServiceEvent.SERVICE_REMOVED)
					{
						registryevent.addRemovedService(event.getService());
					}
				}
				
				if(registryevent.isDue())
				{
					notifyObservers(registryevent);
					registryevent = new RegistryEvent(true);
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
		
//		this.eventslimit = 50;
//		this.timelimit = 5000;
		this.registryevent = new RegistryEvent(true);
		
		restartTimer();
		
//		System.out.println("local registry observer started");
	}
	
	/**
	 *  Start or restart the timer.
	 */
	protected void restartTimer()
	{
		if(canceltimer!=null)
			canceltimer.run();
		
		// Set up event notification timer
//		System.out.println("notify in: "+getTimeLimit());
		canceltimer = timer.waitForDelay(getTimeLimit(), new Runnable()
		{
			public void run()
			{
//				System.out.println("notifyObservers: "+System.currentTimeMillis()+" "+hashCode());
				
				// uses timelimit for event and for waiting
				if(registryevent!=null)
				{
					if(registryevent.isDue())
					{
						notifyObservers(registryevent);
						registryevent = new RegistryEvent(true, timelimit);
					}
					// do not wait below 10ms
					canceltimer = timer.waitForDelay(Math.max(10, registryevent.getTimeUntilDue()), this);
				}
			}
		});
	}
	
	/**
	 *  Notify all subscribed platforms that an event has occurred.
	 *  @param event The event.
	 */
	public abstract void notifyObservers(RegistryEvent event);

	/**
	 *  Call when local observation should be terminated.
	 */
	public void terminate()
	{
		localregsub.terminate();
	}

	/**
	 *  Get the eventslimit.
	 *  @return the eventslimit
	 */
	public int getEventsLimit()
	{
		return eventslimit;
	}

	/**
	 *  Get the timelimit.
	 *  @return the timelimit
	 */
	public long getTimeLimit()
	{
		return timelimit;
	}
	
	/**
	 *  Set the timelimit.
	 *  @param timelimit The timelimit to set
	 */
	public void setTimelimit(long timelimit)
	{
		if(timelimit!=this.timelimit)
		{
//			System.out.println("Timelimit is: "+timelimit);
			this.timelimit = timelimit;
			restartTimer();
		}
	}

	/**
	 *  Get the current state of the registry (full content).
	 *  @return An event with the full state.
	 */
	public RegistryEvent getCurrentStateEvent()
	{
		IServiceRegistry reg = ServiceRegistry.getRegistry(cid);
		ServiceQuery<IService> query = new ServiceQuery<IService>((Class)null, Binding.SCOPE_PLATFORM, null, cid, null);
		Set<IService> added = reg.searchServicesSync(query);
		// Remove only platform scoped services
		if(added!=null)
		{
			for(Iterator<IService> it=added.iterator(); it.hasNext(); )
			{
				IService ser = it.next();
				if(globalscope && RequiredServiceInfo.isScopeOnLocalPlatform(ser.getServiceIdentifier().getScope()))
					it.remove();
			}
		}
		RegistryEvent event = new RegistryEvent(added, null, eventslimit, timelimit, false, null);
		return event;
	}
}
