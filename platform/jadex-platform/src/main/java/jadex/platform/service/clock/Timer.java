package jadex.platform.service.clock;

import java.io.Serializable;

import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;

/**
 *  A timer for being notified at a specified timepoint.
 */
public class Timer implements ITimer, Serializable
{
	//-------- static attributes --------
	
	/** The instance count. */
	protected static int count;
	
	//-------- attributes --------
	
	/** The time delta before the alarm. */ 
	protected long time;
	
	/** The clock. */
	protected IClock clock;
	
	/** The timed object. */
	protected ITimedObject to;
	
	/** The instance number. */
	protected int number;
	
	//-------- constructors --------

	/**
	 *  Create a new timer.
	 */
	protected Timer(long time, IClock clock, ITimedObject to)
	{
		this.time = time;
		this.clock = clock;
		this.to = to;
		synchronized(Timer.class)
		{
			this.number = count++;
		}
	}

	//-------- methods --------
	
	/**
	 *  Get the next absolute alarm timepoint.
	 *  @return The next timepoint.
	 */
	public long getNotificationTime()
	{
		return time;
	}
	
	/**
	 *  Get the timed object.
	 *  @return The timed object.
	 */
	public ITimedObject getTimedObject()
	{
		return to;
	}
	
	/**
	 *  Change notification time.
	 *  @param The notification time.
	 */
	public void setNotificationTime(long time)
	{
		clock.removeTimer(this);
		this.time = time;
//		System.out.println("Noti time: "+time+" "+this);
		clock.addTimer(this);
	}
	
	/**
	 *  Cancel the timer.
	 */
	public void cancel()
	{
		clock.removeTimer(this);
	}
	
	/**
	 *  Get the number.
	 *  @return The number.
	 */
	protected int getNumber()
	{
		return number;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 * /
	public String toString()
	{
		//return "Timer( "+number+" ,"+(time-clock.getTime())+")";
		return "Timer( "+number+" ,"+new Date(time)+")";
	}*/
}
