package jadex.bridge;

import javax.swing.event.ChangeListener;


/**
 *  The clock service.
 */
public interface IClockService extends IPlatformService
{
	/**
     * The Class object representing the class corresponding to
     * the this interface. Need due to JavaFlow Bug:
     * http://issues.apache.org/jira/browse/SANDBOX-111
     */
	public static final Class TYPE = IClockService.class;

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
	 *  Get the clock delta.
	 *  @return The clock delta.
	 */
	public long getDelta();
	
	/**
	 *  Get the clock state.
	 *  @return The clock state.
	 */
	public String getState();
	
	/**
	 *  Set the clock delta.
	 *  @param delta The new clock delta.
	 */
	public void setDelta(long delta);
	
	/**
	 *  Get the clocks dilation.
	 *  @return The clocks dilation.
	 *  // Hack. Remove? only for continuous
	 */
	public double getDilation();
	
	/**
	 *  Set the clocks dilation.
	 *  @param dilation The clocks dilation.
	 *  // Hack. Remove? only for continuous
	 */
	public void setDilation(double dilation);
	
	/**
	 *  Stop the clock.
	 */
	public void stop();
	
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
	 *  Get all active timers.
	 *  @return The active timers.
	 */
	public ITimer[] getTimers();
	
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	public void addChangeListener(ChangeListener listener);
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public void removeChangeListener(ChangeListener listener);
	
	/**
	 *  Get the clock type.
	 *  @return The clock type.
	 */
	public String getClockType();
	
	/**
	 *  Advance one event.
	 *  @return True, if clock could be advanced.
	 */
	public boolean advanceEvent();
	
}
