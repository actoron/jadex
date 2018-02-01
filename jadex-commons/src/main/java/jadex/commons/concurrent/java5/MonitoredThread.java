package jadex.commons.concurrent.java5;

/**
 *  Thread class used by MonitoredThreadPoolExecutor,
 *  contains additional state about the thread to monitor thread behavior.
 *
 */
public class MonitoredThread extends Thread
{
	/** Thread pool executor that created the thread. */
	protected MonitoredThreadPoolExecutor origin;
	
	/** The thread number assigned to the thread. */
	protected int number;
	
	/** Departure time of the thread from the pool. */
	protected volatile long departure = Long.MAX_VALUE;
	
	/** Flag if the thread was borrowed. */
	protected volatile boolean borrowed;
	
	/**
	 *  Creates the thread.
	 * 
	 *  @param r The runnable to execute.
	 *  @param origin The originating thread pool.
	 */
	public MonitoredThread(Runnable r, MonitoredThreadPoolExecutor origin)
	{
		super(r);
		this.origin = origin;
	}
	
	/**
	 *  Gets the thread number.
	 *  
	 *  @return The thread number.
	 */
	public int getNumber()
	{
		return number;
	}
	
	/**
	 *  Sets the thread number.
	 *  
	 *  @param num The thread number.
	 */
	public void setNumber(int num)
	{
		number = num;
	}
	
	/**
	 *  Gets the time the thread departed from the pool.
	 *  
	 *  @return The time the thread departed from the pool.
	 */
	public long getDeparture()
	{
		return departure;
	}
	
	/**
	 *  Sets the time the thread departed from the pool.
	 *  
	 *  @param departure The time the thread departed from the pool.
	 */
	public void setDeparture(long departure)
	{
		this.departure = departure;
	}
	
	/**
	 *  Notify the pool that the thread is borrowed and return
	 *  the return to the pool is expected to be delayed.
	 */
	protected void borrow()
	{
		borrowed = true;
		departure = Long.MAX_VALUE;
		origin.borrow();
	}
	
	/**
	 *  Returns if the thread is currently borrowed.
	 *  
	 *  @return True, if borrowed.
	 */
	public boolean isBorrowed()
	{
		return borrowed;
	}
	
	/**
	 *  Returns if the thread is currently blocked.
	 *  
	 *  @return True, if blocked.
	 */
	public boolean isBlocked()
	{
		State threadstate = getState();
		return State.BLOCKED == threadstate || State.WAITING == threadstate || State.TIMED_WAITING == threadstate;
	}
	
	/**
	 *  Try to borrow the thread.
	 *  If thread is non-monitored,
	 *  this does nothing.
	 */
	public static final void tryBorrow()
	{
		Thread t = Thread.currentThread();
		if (t instanceof MonitoredThread)
			((MonitoredThread) t).borrow();
	}
}
