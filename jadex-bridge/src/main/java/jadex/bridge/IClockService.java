package jadex.bridge;


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
	 * /
	public long getStarttime();*/
	
	/**
	 *  Get the clock delta.
	 *  @return The clock delta.
	 */
	public long getDelta();
	
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
	
}
