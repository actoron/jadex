package jadex.platform.service.clock;

import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.MonitoredThread;

/**
 *  A continuous clock represents a real time clock that
 *  is based on the hardware clock. Can be made faster or
 *  slower and also has an adjustable start time. 
 */
public class ContinuousClock extends AbstractClock implements IContinuousClock
{
	//-------- attributes --------

	/** The dilation. */
	protected double dilation;
	
	/** The last starting real time measurement point. */
	protected long laststart;
	
	/** The elapsed time. */
	protected long elapsed;
	
	/** The active timer watcher. */
	protected Executor executor;
	
	/** The threadpool. */
	protected IThreadPool threadpool;
	
	//-------- constructors --------
	
	/**
	 *  Create a new clock. Delta (tick size) is default (see AbstractClock).
	 *  @param name The clock name.
	 *  @param starttime The start time.
	 *  @param dilation The dilation.
	 */
	public ContinuousClock(String name, long starttime, double dilation, IThreadPool threadpool)
	{
		this(name, starttime, dilation, DEFAULT_DELTA, threadpool);
	}
	
	/**
	 *  Create a new clock.
	 *  @param name The clock name.
	 *  @param starttime The start time.
	 *  @param dilation The dilation.
	 *  @param delta The tick size (in millis).
	 */
	public ContinuousClock(String name, long starttime, double dilation, long delta, IThreadPool threadpool)
	{
		super(name, starttime, delta);
		
		this.threadpool = threadpool;
		
		this.dilation = dilation;
		
		// Active executor for managing timers.
		this.executor = createExecutor();
		
		// Notification generator.
//		this.notificator = createNotificator();
		
		// If a delta is set a continuous tick timer will be installed.
//		System.out.println("Delta: "+delta);
//		if(delta>0)
//		{
//			createTickTimer(new ITimedObject()
//			{
//				public void timeEventOccurred(long currenttime)
//				{
//					synchronized(this)
//					{
////						System.out.println("tick: "+getTime()+" "+getTick());
//						createTickTimer(this);
//					}
//				}
//			});
//		}
	}
	
	/**
	 *  Create a new clock.
	 *  @param oldclock The old clock.
	 */
	public ContinuousClock(IClock oldclock, IThreadPool threadpool)
	{
		this(null, 0, 1, threadpool);
		copyFromClock(oldclock);
	}
	
	
	/**
	 *  Transfer state from another clock to this clock.
	 */
	protected void copyFromClock(IClock oldclock)
	{
		super.copyFromClock(oldclock);
		
		// Todo: adjust own settings based on dilation!?
		this.elapsed = oldclock.getTime() - oldclock.getStarttime();
	}
	

	/**
	 *  Called, when the clock is no longer used.
	 */
	public void dispose()
	{
		executor.shutdown();
		// Wake up timer thread, if currently waiting.
		synchronized(this)
		{
			timers.clear();	// Todo: should still send notifications?
			this.notify();
		}
//		notificator.shutdown(null);
	}

	//-------- methods --------
	
	/**
	 *  Get the clocks name.
	 *  @return The name.
	 */
	public long getTime()
	{
		computeNextTimepoint();
		return super.getTime();
	}
	
	/**
	 *  Get the clocks dilation.
	 *  @return The clocks dilation.
	 */
	public double getDilation()
	{
		return dilation;
	}
	
	/**
	 *  Set the clocks dilation.
	 *  @param dilation The clocks dilation.
	 */
	public void setDilation(double dilation)
	{
		synchronized(this)
		{
			if(STATE_RUNNING.equals(state))
			{
				long ct = System.currentTimeMillis();
				this.elapsed += (ct-laststart)*getDilation();
				this.laststart = ct;
			}
			this.dilation = dilation;
			
			this.notify();
			executor.execute();
		}
		
		notifyListeners(new ChangeEvent(this, EVENT_TYPE_NEW_DILATION));
	}
	
	/**
	 *  Get the clock type.
	 *  @return The clock type.
	 */
	public String getType()
	{
		return IClock.TYPE_CONTINUOUS;
	}
	
	/**
	 *  Start the clock.
	 */
	public void start()
	{
		boolean notify = false;
		
		synchronized(this)
		{
			if(!STATE_RUNNING.equals(state))
			{
				this.state = STATE_RUNNING;
				this.laststart = System.currentTimeMillis();
				executor.execute();
				notify = true;
			}
		}
		
		if(notify)
		{
			notifyListeners(new ChangeEvent(this, EVENT_TYPE_STARTED));
		}
	}
	
	/**
	 *  Stop the clock.
	 */
	public void stop()
	{
		boolean notify = false;
		
		synchronized(this)
		{
			if(STATE_RUNNING.equals(state))
			{
				this.state = STATE_SUSPENDED;
				//this.elpased += System.currentTimeMillis()-laststart;
				this.elapsed += (System.currentTimeMillis()-laststart)*dilation;
				notify = true;
			}
		}
		
		if(notify)
			notifyListeners(new ChangeEvent(this, EVENT_TYPE_STOPPED));
	}
	
	/**
	 *  Reset the clock.
	 */
	public void reset()
	{
		synchronized(this)
		{
			if(STATE_RUNNING.equals(state))
				this.state = STATE_SUSPENDED;
			this.elapsed = 0;
			this.laststart = 0;
			this.currenttime = starttime;
		}
		
		notifyListeners(new ChangeEvent(this, EVENT_TYPE_RESET));
	}
	
	/**
	 *  Compute the next timepoint.
	 */
	protected synchronized void computeNextTimepoint()
	{
		if(STATE_RUNNING.equals(state))
		{
			long ct = System.currentTimeMillis();
			long lastelapsed = ct-laststart;
			currenttime = starttime + this.elapsed +(long)(lastelapsed*dilation);
		}
	}
		
	/**
	 *  Add a timer.
	 *  @param timer The timer.
	 */
	public void addTimer(ITimer timer)
	{
		super.addTimer(timer);
		
		// naked notify due to notifications in addTimer() must not be synchronized.
		synchronized(this)
		{
			this.notify();	
		}
		executor.execute();
	}
	
	/**
	 *  Remove a timer.
	 *  @param timer The timer.
	 */
	public void removeTimer(ITimer timer)
	{
		super.removeTimer(timer);
		
		// naked notify due to notifications in removeTimer() must not be synchronized.
		synchronized(this)
		{
			this.notify();	
		}
		executor.execute();
	}
	
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	public void addChangeListener(IChangeListener listener)
	{
		super.addChangeListener(listener);
//		notificator.execute();
	}
	
	//-------- helper methods --------
	
	/**
	 *  Create new executor.
	 */
	protected Executor createExecutor()
	{
		return new Executor(threadpool, new IExecutable()
		{
			/**
			 *  Execute the executable.
			 *  @return True, if the object wants to be executed again.
			 */
			public boolean execute()
			{
				Timer	next = null;
				long	diff;
				long	currenttime = getTime();
	
				// Getting entry and waiting has to be synchronized
				// to avoid new (earlier) entries being added in between.
				synchronized(ContinuousClock.this)
				{
					//System.out.println("timers: "+timers.size()+" "+jadex.util.SUtil.arrayToString(timers.toArray()));
					
					// Exit thread when timetable is empty.
					//if(timers.isEmpty() || STATE_SUSPENDED.equals(state))
					//	return false;
	
					//System.out.println("timer: "+timetable);
	
					// Exit thread when timetable is empty.
					if(timers.isEmpty() || STATE_SUSPENDED.equals(state))
						return false;
					
					// Get next entry from timetable.
					next = (Timer)timers.first();
					diff = (long)((next.time - currenttime)/getDilation());
//						System.out.println("diff: "+diff+" "+next.time+" "+getTime()+" "+next);

					// Wait until next entry is due
					// (must check diff>0 as wait(0) performs endless wait())
					if(diff>0)
					{
						try
						{
							// Notify advanced thread pool that the thread is
							// borrowed / about to be blocked.
							MonitoredThread.tryBorrow();
							
//							System.out.println("timer waiting for "+diff+" millis at "+System.currentTimeMillis());
							ContinuousClock.this.wait(diff);
//							System.out.println("timer awake at "+System.currentTimeMillis());
						}
						catch(InterruptedException e)
						{
//							System.out.println("timer awake at "+System.currentTimeMillis());							
						}
						catch(Throwable t)
						{
							t.printStackTrace();
						}
					}
				}
	
				// Handle due entry (must not be synchronized to avoid
				// deadlock when timed object concurrently accesses timetable).
				// Problem: Timed object may concurrently remove/change its entry
				// (timed object is notified anyways, and too much notifications don't hurt?)
				if(diff<=0)
				{
					// It is important to remove the entry before calling the notifyDue() method,
					// as from this method a new timing entry might be added,
					// which will otherwise be removed afterwards.
					removeTimer(next);
				}
				
				if(diff<=0)
				{
					// Now notify the agent.
//					if(next==null)
//						throw new RuntimeException("next is null");
//					else if(next.getTimedObject()==null)
//						throw new RuntimeException("next.to is null");
					
					try
					{
//						System.out.println("notifying: "+next.getTimedObject());
						next.getTimedObject().timeEventOccurred(currenttime);
//						System.out.println("notified: "+next);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
	
//				System.out.println("Exit"+timers.isEmpty());
				notifyListeners(new ChangeEvent(this, EVENT_TYPE_NEXT_TIMEPOINT));
	
				return !timers.isEmpty();
			}
		});
	}
	
	/**
	 *  Main for testing.
	 * /
	public static void main(String[] args)
	{
		ContinuousClock c = new ContinuousClock("clock", 0, 1);
		c.start();
		
		System.out.println("0: "+c.getTime());
		
		try{Thread.sleep(2000);}
		catch(InterruptedException e){}
		
		System.out.println("1: "+c.getTime());
		
		c.setDilation(2);
		
		System.out.println("2: "+c.getTime());
		
		try{Thread.sleep(2000);}
		catch(InterruptedException e){}
		
		System.out.println("3: "+c.getTime());
	}*/
}
