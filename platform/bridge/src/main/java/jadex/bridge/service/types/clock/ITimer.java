package jadex.bridge.service.types.clock;

/**
 *  Interface for a timer.
 */
public interface ITimer
{
	/**
	 *  Cancel the timer.
	 */
	public void cancel();
	
	/**
	 *  Get the next notification time.
	 *  @return The notification time.
	 */
	public long getNotificationTime();
	
	/**
	 *  Change notification time.
	 *  @param The notification time.
	 */
	public void setNotificationTime(long time);
	
	/**
	 *  Get the timed object associated with this timer.
	 *  @return The timed object.
	 */
	public ITimedObject getTimedObject();
	
	/**
	 *  Get the optional object associated with this timer.
	 *  @param The user object.
	 * /
	public Object getInfo();*/
}
