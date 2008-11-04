package jadex.adapter.base.clock;

import jadex.bridge.ITimedObject;

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
