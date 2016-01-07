package jadex.bridge.service.types.clock;


import jadex.commons.IChangeListener;


/**
 *  Interface a for clock. Provides general methods for
 *  working with a clock such as starting, stopping, resetting,
 *  getting and setting the current time (stop time?).
 *  
 *  todo: do we want to support setTime() (how to handle timer, ticktimers i.e. keep their
 *  time or keep their deltas compared to the current time)
 */
public interface IClock
{
	//-------- constants --------
	
	/** The clock state running. */
	public static String STATE_RUNNING = "running";
	
	/** The clock state suspended. */
	public static String STATE_SUSPENDED = "suspended";

	/** The clock type system. */
	public static String TYPE_SYSTEM = "system";
	
	/** The clock type continuous. */
	public static String TYPE_CONTINUOUS = "continuous";
	
	/** The clock type event driven. */
	public static String TYPE_EVENT_DRIVEN = "event_driven";

	/** The clock type time driven. */
	public static String TYPE_TIME_DRIVEN = "time_driven";
	
	/** Change event type new delta. */
	public static final String EVENT_TYPE_NEW_DELTA = "new_delta";

	/** Change event type new dilation. */
	public static final String EVENT_TYPE_NEW_DILATION = "new_dilation";
	
	/** Change event type started. */
	public static final String EVENT_TYPE_STARTED = "started";

	/** Change event type stopped. */
	public static final String EVENT_TYPE_STOPPED = "stopped";

	/** Change event type reset. */
	public static final String EVENT_TYPE_RESET = "reset";

	/** Change event timer added. */
	public static final String EVENT_TYPE_TIMER_ADDED = "timer_added";

	/** Change event timer removed. */
	public static final String EVENT_TYPE_TIMER_REMOVED = "timer_removed";

	/** Change event next timepoint. */
	public static final String EVENT_TYPE_NEXT_TIMEPOINT = "next_timepoint";

	
	//-------- methods --------
	
	/**
	 *  Get the current time.
	 *  @return The current time.
	 */
	public long getTime();
	
	/**
	 *  Get the current tick.
	 *  @return The current tick.
	 */
	public double getTick();
	
	/**
	 *  Get the clocks start time.
	 *  @return The start time.
	 */
	public long getStarttime();
	
	/**
	 *  Set the clocks start time.
	 * @param starttime The start time.
	 */
	public void setStarttime(long starttime);
	
	/**
	 *  Get the clock delta.
	 *  @return The clock delta.
	 */
	public long getDelta();

	/**
	 *  Get the clocks name.
	 *  @return The name.
	 */
	public String getName();
	
	/**
	 *  Get the clock type.
	 *  @return The clock type.
	 */
	public String getType();
	
	/**
	 *  Get the clock state.
	 *  @return The clock state.
	 */
	public String getState();
	
	/**
	 *  Set the clock delta.
	 *  param delta The new clock delta.
	 */
	public void	setDelta(long delta);

	/**
	 *  Get all active timers.
	 *  @return The active timers.
	 */
	public ITimer[] getTimers();
	
	/**
	 *  Get all active tick timers.
	 *  @return The active tick timers.
	 */
	public ITimer[] getTickTimers();
	
	/**
	 *  Set all active timers.
	 *  @param timers The active timers.
	 */
//	public void setTimers(ITimer[] timers);
	
	/**
	 *  Start the clock.
	 */
	public void start();
	
	/**
	 *  Stop the clock.
	 */
	public void stop();
	
	/**
	 *  Reset the clock.
	 */
	public void reset();
	
	/**
	 *  Create a new timer.
	 *  The unit of the timespan value depends on the clock implementation.
	 *  For system clocks, the time value should adhere to the time representation
	 *  as used by {@link System#currentTimeMillis()}.
	 *  
	 *  @param timespan The relative timespan after which the timed object should be notified.
	 *  @param to The timed object.
	 */
	public ITimer createTimer(long time, ITimedObject to);
	
	/**
	 *  Create a new tick timer.
	 *  todo: @param tickcount The number of ticks.
	 *  @param to The timed object.
	 */
	public ITimer createTickTimer(ITimedObject to);
	
	/**
	 *  Get the next timer.
	 *  @return The next timer.
	 */
	public ITimer getNextTimer();
	
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	public void addChangeListener(IChangeListener listener);
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public void removeChangeListener(IChangeListener listener);

	/**
	 *  Called, when the clock is no longer used.
	 */
	public void dispose();
		
	/**
	 *  Add a timer.
	 *  @param timer The timer.
	 */
	public void addTimer(ITimer timer);
	
	/**
	 *  Remove a timer.
	 *  @param timer The timer.
	 */
	public void removeTimer(ITimer timer);
	
	/**
	 *  Add a tick timer.
	 *  @param timer The timer.
	 */
	public void addTickTimer(ITimer timer);
	
	/**
	 *  Remove a tick timer.
	 *  @param timer The timer.
	 */
	public void removeTickTimer(ITimer timer);
	
}
