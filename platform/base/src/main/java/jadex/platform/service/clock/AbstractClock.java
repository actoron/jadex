package jadex.platform.service.clock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.collection.SCollection;
import jadex.commons.future.IResultListener;

/**
 *  Abstract base class for all clocks.
 */
public abstract class AbstractClock implements IClock
{
	//-------- constants --------
	
	/** The default delta (tick time). */
	public static final long	DEFAULT_DELTA	= 100;
	
	//-------- attributes --------

	/** The clock name. */
	protected String name;
	
	/** The start time (model time). */
	protected long starttime;

	/** The current time (last measurement). */
	protected long currenttime;
	
	/** The clock tick delta (relative to base time). */
	protected long delta;
	
	/** The clock state. */
	protected String state;
	
	/** The tick timers. */
	protected List ticktimers;

	/** The timers  (entries ordered by timepoint). */
	public SortedSet timers;
	
	/** The tick timer. */
	protected Timer ticktimer;
	
	/** The change listeners. */
	protected List listeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a new clock.
	 */
	public AbstractClock(String name, long starttime, long delta)
	{
		this.name = name;
		this.starttime = starttime;
		this.currenttime = starttime;
		this.delta = delta;
		this.state = STATE_SUSPENDED;
		this.listeners = Collections.synchronizedList(SCollection.createArrayList());
		
		// Sorted set for all entries ordered by due time.
		this.timers	= Collections.synchronizedSortedSet(new TreeSet(new Comparator()
		{
			/**
			 *  Compare two timers.
			 *  @param arg0 The first timer.
			 *  @param arg1 The second timer.
			 */
			public int compare(Object arg0, Object arg1)
			{
				Timer t1 = (Timer)arg0;
				Timer t2 = (Timer)arg1;	
				long ret	= t1.getNotificationTime() - t2.getNotificationTime();
				if(ret==0 && t1!=t2)
					ret	= t1.getNumber()-t2.getNumber();
				return ret>0 ? 1 : ret<0 ? -1 : 0;
			}
		}));
		
		this.ticktimers = SCollection.createArrayList();
		
		// The ticktimer is a normal timer which indicates when the next
		// tick is due. It then notifies all registered tick timers. 
		this.ticktimer = new Timer(0, this, new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
				TickTimer[] tts;
				
				synchronized(AbstractClock.this)
				{
//					System.out.println("Ticktimer notified: "+ticktimers);
					
					tts = (TickTimer[])ticktimers.toArray(new TickTimer[ticktimers.size()]);
					ticktimers.clear();
				}
				
				for(int i=0; i<tts.length; i++)
					tts[i].getTimedObject().timeEventOccurred(currenttime);
			}
			
			public String toString()
			{
				return "Tick Timer";
			}
		});
	}
	
	/**
	 *  Transfer state from another clock to this clock.
	 */
	protected void copyFromClock(IClock oldclock)
	{
		if(IClock.STATE_RUNNING.equals(state))
			throw new RuntimeException("Cannot copy entries, while clock is running.");
		
		this.name = oldclock.getName();
		this.starttime = oldclock.getStarttime();
		this.delta = oldclock.getDelta();
		this.currenttime = oldclock.getTime();
		
		ITimer[] ts = oldclock.getTimers();
		for(int i=0; i<ts.length; i++)
		{
//			if(ts[i]==oldclock.ticktimer)
//			{
//				timers.add(ticktimer);
//			}
//			else
//			{
				timers.add(new Timer(ts[i].getNotificationTime(), this, ((Timer)ts[i]).getTimedObject()));
//			}
		}
		
		// Add ticktimers from old clock
		ITimer[] tts = oldclock.getTickTimers();
		for(int i=0; i<tts.length; i++)
		{
			addTickTimer(new TickTimer(this, tts[i].getTimedObject()));
		}		
	}

	/**
	 *  Called, when the clock is no longer used.
	 */
	public void dispose()
	{		
	}
	
	//-------- methods --------
	
	/**
	 *  Get the current time.
	 *  @return The current time.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Get the clocks start time.
	 *  @return The start time.
	 */
	public long getStarttime()
	{
		return starttime;
	}
	
	/**
	 *  Set the clocks start time.
	 * @return 
	 *  @return The start time.
	 */
	public void setStarttime(long starttime)
	{
		this.starttime = starttime;
	}
	
	/**
	 *  Get the clocks name.
	 *  @return The name.
	 */
	public long getTime()
	{
		return currenttime;
	}
	
	/**
	 *  Get the current tick.
	 *  @return The current tick (can be in between ticks).
	 *  Returns -1 if no tick size was specified (delta==0).
	 */
	public double getTick()
	{
		return delta==0? -1: (getTime()-getStarttime())/(double)getDelta();
	}
	
	/**
	 *  Get the clock delta.
	 *  @return The clock delta.
	 */
	public long getDelta()
	{
		return delta;
	}
	
	/**
	 *  Set the clock delta.
	 *  param delta The new clock delta.
	 */
	public void setDelta(long delta)
	{
		synchronized(this)
		{
			this.delta	= delta;
			
			// Reset tick timer, if still in future
			if(timers.contains(ticktimer) && ticktimer.getNotificationTime()>getTime())
			{
				long num = (getTime()-getStarttime())/getDelta();
				long time = (num+1)*getDelta()+getStarttime();
				ticktimer.setNotificationTime(time);
//				System.out.println("Ticktimer at: "+time);
			}
		}
		
		notifyListeners(new ChangeEvent(this, EVENT_TYPE_NEW_DELTA));
	}

	/**
	 *  Get the next timer.
	 *  @return The next timer.
	 */
	public synchronized ITimer getNextTimer()
	{
		return timers.size()==0? null: (ITimer)timers.first();
	}
	
	/**
	 *  Get the clock state.
	 *  @return The clock state.
	 */
	public String getState()
	{
		return state;
	}
	
	/**
	 *  Start the clock.
	 */
	public void start()
	{
		if(!STATE_RUNNING.equals(state))
		{
			this.state = STATE_RUNNING;
			notifyListeners(new ChangeEvent(this, EVENT_TYPE_STARTED));
		}
	}
	
	/**
	 *  Stop the clock.
	 */
	public void stop()
	{
		if(STATE_RUNNING.equals(state))
		{
			this.state = STATE_SUSPENDED;
			notifyListeners(new ChangeEvent(this, EVENT_TYPE_STOPPED));
		}
	}
	
	/**
	 *  Reset the clock.
	 */
	public synchronized void reset()
	{
		if(STATE_RUNNING.equals(state))
			this.state = STATE_SUSPENDED;
		this.currenttime = starttime;
		this.timers.clear();
		this.ticktimers.clear();
		
		notifyListeners(new ChangeEvent(this, EVENT_TYPE_RESET));
	}
	
	/**
	 *  Create a new timer.
	 *  @param timespan The timespan.
	 *  @param to The timed object.
	 */
	public ITimer createTimer(long timespan, ITimedObject to)
	{
		Timer t = new Timer(getTime()+timespan, this, to);
		addTimer(t);
		return t;
	}
	
	/**
	 *  Create a new tick timer.
	 *  @param to The timed object.
	 *  @param info Optional info object.
	 */
	public ITimer createTickTimer(ITimedObject to)
	{
		TickTimer t = new TickTimer(this, to);
		addTickTimer(t);
		return t;
	}
	
	/**
	 *  Get all active timers.
	 *  @return The active timers.
	 */
	public ITimer[] getTimers()
	{
		ITimer[] ret;
		if(ticktimer!=null)
		{
			List clone = new ArrayList(timers);
			clone.remove(ticktimer);
			ret = (ITimer[])clone.toArray(new ITimer[0]);
		}
		else
		{
			ret = (ITimer[])timers.toArray(new ITimer[0]);
		}
		return ret;
	}
	
	/**
	 *  Get all active tick timers.
	 *  @return The active tick timers.
	 */
	public ITimer[] getTickTimers()
	{
		return (ITimer[])ticktimers.toArray(new ITimer[0]);
	}
	
	/**
	 *  Add a timer.
	 *  @param timer The timer.
	 */
	public void addTimer(ITimer timer)
	{
		synchronized(this)
		{
			timers.add(timer);
//			System.err.println("Added timer: "+timers);
		}
		
		notifyListeners(new ChangeEvent(this, EVENT_TYPE_TIMER_ADDED));
	}
	
	/**
	 *  Remove a timer.
	 *  @param timer The timer.
	 */
	public void removeTimer(ITimer timer)
	{
		synchronized(this)
		{
			boolean	removed	= timers.remove(timer);
//			if(!removed)
//				System.out.println("Could not remove timer: "+timer+" "+timers);
//			System.err.println("Removed timer: "+timers);
		}
		
		notifyListeners(new ChangeEvent(this, EVENT_TYPE_TIMER_REMOVED));
	}
	
	/**
	 *  Add a tick timer.
	 *  @param timer The timer.
	 */
	public void addTickTimer(ITimer timer)
	{
		synchronized(this)
		{
			ticktimers.add(timer);
			activateTickTimer();
			
//			if(!timers.contains(ticktimer))
//			{
//				long num = (getTime()-getStarttime())/getDelta();
//				long time = (num+1)*getDelta()+getStarttime();
//				ticktimer.setNotificationTime(time);
//				System.out.println("Ticktimer at: "+time+" "+getTime());
//			}
		}
		
		notifyListeners(new ChangeEvent(this, EVENT_TYPE_TIMER_ADDED));
	}

	/**
	 *  Activate the tick timer.
	 */
	protected void activateTickTimer()
	{
		if(!timers.contains(ticktimer))
		{
			long num = (getTime()-getStarttime())/getDelta();
			long time = (num+1)*getDelta()+getStarttime();
			ticktimer.setNotificationTime(time);
//			System.out.println("Ticktimer at: "+time+" "+getTime());
		}
	}
	
	/**
	 *  Remove a tick timer.
	 *  @param timer The timer.
	 */
	public void removeTickTimer(ITimer timer)
	{
		synchronized(this)
		{
			ticktimers.remove(timer);
			if(ticktimers.size()==0)
				removeTimer(ticktimer);
		}
		
		notifyListeners(new ChangeEvent(this, EVENT_TYPE_TIMER_REMOVED));
	}
	
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	public void addChangeListener(IChangeListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public void removeChangeListener(IChangeListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 *  Notify the listeners.
	 */
	protected void notifyListeners(ChangeEvent ce)
	{
//		System.out.println(""+this.getClass()+" "+ce);
		IChangeListener[]	cls	= (IChangeListener[])listeners.toArray(new IChangeListener[0]);
		for(int i=0; i<cls.length; i++)
			cls[i].changeOccurred(ce);
	}
	
	/**
	 *  Check if the clock has listeners.
	 */
	protected boolean	hasListeners()
	{
		return !listeners.isEmpty();
	}
	
	//-------- IPlatformService --------
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
		stop();
		if(listener!=null)
			listener.resultAvailable(null);
	}
}


