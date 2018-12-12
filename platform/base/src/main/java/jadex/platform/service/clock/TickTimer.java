package jadex.platform.service.clock;

import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.ITimedObject;


/**
 *  A timer for waiting on clock ticks.
 */
public class TickTimer extends Timer
{
	//-------- constructors --------

	/**
	 *  Create a new timer.
	 */
	protected TickTimer(IClock clock, ITimedObject to)
	{
		super(0, clock, to);
	}
	
	/**
	 *  Cancel the timer.
	 */
	public void cancel()
	{
		clock.removeTickTimer(this);
	}
	
	/**
	 *  Change notification time.
	 *  @param The notification time.
	 */
	public void setNotificationTime(long time)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Timer( "+number+" )";
	}
}
