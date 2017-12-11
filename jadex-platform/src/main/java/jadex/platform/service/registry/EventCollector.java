package jadex.platform.service.registry;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.registry.ARegistryEvent;
import jadex.bridge.service.types.registry.RegistryEvent;
import jadex.commons.collection.IDelayRunner;

/**
 * 
 */
public abstract class EventCollector
{
	/** The local platform id. */
	protected IComponentIdentifier cid;
	
	/** The current registry event (is accumulated). */
	protected ARegistryEvent registryevent;
	
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
	public EventCollector(IComponentIdentifier cid, final IDelayRunner timer)
	{
		this(cid, timer, 50, 10000);
	}
	
	/**
	 *  Create a new registry observer.
	 */
	public EventCollector(IComponentIdentifier cid, final IDelayRunner timer, int eventslimit, final long timelimit)
	{
		this.cid = cid;
		this.timer = timer;
		this.eventslimit = eventslimit;
		this.timelimit = timelimit;
		this.registryevent = createEvent();
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
						createEvent();
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
	public abstract void notifyObservers(ARegistryEvent event);

	/**
	 *  Call when local observation should be terminated.
	 */
	public void terminate()
	{
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
	 *  Create an event.
	 *  @return The event.
	 */
	public ARegistryEvent createEvent()
	{
		return new RegistryEvent(true, timelimit);
	}
}
