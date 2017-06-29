package jadex.platform.service.registry;

import java.util.Collection;
import java.util.HashSet;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
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
	
	/**
	 *  Create a new registry observer.
	 */
	public LocalRegistryObserver(IComponentIdentifier cid, final IDelayRunner timer)
	{
		this(cid, timer, 50, 5000);
	}
	
	/**
	 *  Create a new registry observer.
	 */
	public LocalRegistryObserver(IComponentIdentifier cid, final IDelayRunner timer, int eventslimit, final long timelimit)
	{
		this.cid = cid;
		this.timer = timer;
		this.eventslimit = eventslimit;
		this.timelimit = timelimit;
		
		// Subscribe to changes of the local registry to inform other platforms
//		ServiceQuery<ServiceEvent<IService>> query = new ServiceQuery<ServiceEvent<IService>>(ServiceEvent.CLASSINFO, (ClassInfo)null, Binding.SCOPE_PLATFORM, (IAsyncFilter)null, null, cid);
		ServiceQuery<ServiceEvent<IService>> query = new ServiceQuery<ServiceEvent<IService>>((ClassInfo)null, Binding.SCOPE_PLATFORM, null, cid, (IAsyncFilter)null, ServiceEvent.CLASSINFO);
		localregsub = ServiceRegistry.getRegistry(cid).addQuery(query);
		localregsub.addIntermediateResultListener(new IIntermediateResultListener<ServiceEvent<IService>>()
		{
			public void intermediateResultAvailable(ServiceEvent<IService> event)
			{
				if(registryevent==null)
					registryevent= new RegistryEvent(true);
				
				if(event.getType() == ServiceEvent.SERVICE_ADDED)
				{
					registryevent.addAddedService(event.getService());
				}
				else if(event.getType() == ServiceEvent.SERVICE_REMOVED)
				{
					registryevent.addRemovedService(event.getService());
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
	}
	
	/**
	 *  Start or restart the timer.
	 */
	protected void restartTimer()
	{
		if(canceltimer!=null)
			canceltimer.run();
		
		// Set up event notification timer
		System.out.println("notify in: "+getTimeLimit());
		canceltimer = timer.waitForDelay(getTimeLimit(), new Runnable()
		{
			public void run()
			{
//				System.out.println("notifyObservers");
				if(registryevent!=null && registryevent.isDue())
				{
					notifyObservers(registryevent);
					registryevent = new RegistryEvent(true);
				}
				canceltimer = timer.waitForDelay(getTimeLimit(), this);
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
		RegistryEvent event = new RegistryEvent(new HashSet<IService>(reg.searchServicesSync(query)), null, eventslimit, timelimit, false);
		return event;
	}
}
