package jadex.platform.service.clock;


import jadex.bridge.service.types.clock.IClock;
import jadex.commons.concurrent.IThreadPool;

/**
 *  Clock implementation that corresponds to the exact system clock.
 */
// hack!!! Should not be based on continuous clock?
// todo: What about ticktimers? Should starttime correspond to 0?
public class SystemClock extends ContinuousClock
{
	//-------- attributes --------
	
	/** The time when the clock was stopped (used to adjust timers before restarting the clock). */
	protected long	stoptime;

	//-------- constructors --------
	
	/**
	 *  Create a new clock.
	 *  @param oldclock The old clock.
	 */
	public SystemClock(IClock oldclock, IThreadPool threadpool)
	{
		this(null, 1, threadpool);
		copyFromClock(oldclock);
	}
	
	/**
	 *  Create a new clock.
	 */
	public SystemClock(String name, long delta, IThreadPool threadpool)
	{
		super(name, System.currentTimeMillis(), delta, threadpool);
	}

	/**
	 *  Transfer state from another clock to this clock.
	 */
	protected void copyFromClock(IClock oldclock)
	{
		super.copyFromClock(oldclock);

		this.stoptime	= oldclock.getTime();
	}

	//-------- methods --------
	
	/**
	 *  Get the type of the clock.   
	 */
	public String getType()
	{
		return TYPE_SYSTEM;
	}

	/**
	 *  The current time is always the same as the system time.
	 */
	public long getTime()
	{
		return System.currentTimeMillis();
	}
	
	/**
	 *  The dilation is always 1.0.
	 */
	public double getDilation()
	{
		return 1.0;
	}
	
	/**
	 *  Start the clock. 
	 * /
	public synchronized void start()
	{
		if(stoptime!=0)
		{
			long offset	= getTime() - stoptime;
			for(Iterator it=timers.iterator(); it.hasNext();)
			{
				Timer	timer	= (Timer)it.next();
				timer.time	= timer.time + offset;
				// Ticktimer is also moved, to stay aligend with non-tick timers.
			}
		}
		super.start();
	}*/

	/**
	 *  Stop the clock.
	 */
	public synchronized void stop()
	{
		super.stop();
		this.stoptime	= getTime();
	}
	
	/**
	 *  Create a notificator thread for continuously informing listeners.
	 * /
	protected Executor	createNotificator()
	{
		// Overwritten, because notification should also occur when the clock is stopped. 
		return new Executor(threadpool, new IExecutable()
		{
			public boolean execute()
			{
				try
				{
					Thread.sleep(100);
				}
				catch(InterruptedException e) {}
				
				notifyListeners();

				return hasListeners();
			}
		});		
	}*/
}
