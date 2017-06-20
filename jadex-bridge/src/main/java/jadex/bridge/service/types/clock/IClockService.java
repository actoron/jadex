package jadex.bridge.service.types.clock;

import java.util.TimerTask;

import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Uncached;
import jadex.commons.IChangeListener;
import jadex.commons.concurrent.IThreadPool;


/**
 *  The clock service.
 */
@Excluded
@Service(system=true)
public interface IClockService extends IService
{
	//-------- constants --------
	
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
	@Uncached
	public long getTime();
	
	/**
	 *  Get the current tick.
	 *  @return The current tick.
	 */
	@Uncached
	public double getTick();
	
	/**
	 *  Get the clocks start time.
	 *  @return The start time.
	 */
	@Uncached
	public long getStarttime();
	
	/**
	 *  Get the clock delta.
	 *  @return The clock delta.
	 */
	@Uncached
	public long getDelta();
	
	/**
	 *  Get the clock state.
	 *  @return The clock state.
	 */
	@Uncached
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
	@Uncached
	public double getDilation();
	
	/**
	 *  Set the clocks dilation.
	 *  @param dilation The clocks dilation.
	 *  // Hack. Remove? only for continuous
	 */
	public void setDilation(double dilation);
	
	/**
	 *  Start the clock.
	 */
	public void start();

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
	 *  Create a new realtime timer.
	 *  
	 *  @param timespan The relative timespan after which the timed object should be notified.
	 *  @param to The timed object.
	 */
	public TimerTask createRealtimeTimer(final long time, final ITimedObject to);
	
	/**
	 *  Get the next timer.
	 *  @return The next timer.
	 */
	@Uncached
	public ITimer getNextTimer();

	/**
	 *  Get all active timers.
	 *  @return The active timers.
	 */
	@Uncached
	public ITimer[] getTimers();
	
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
	 *  Get the clock type.
	 *  @return The clock type.
	 */
	@Uncached
	public String getClockType();
	
	/**
	 *  Advance one event.
	 *  @return True, if clock could be advanced.
	 */
	@Uncached
	public boolean advanceEvent();

	/**
	 *  Set the clock.
	 *  @param clock The new clock.
	 */
	// Hack!!! Remove?
	public void setClock(String type, IThreadPool tp);
	
}
