package jadex.commons.concurrent.java5;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import jadex.commons.SUtil;

/**
 *  Thread pool executor based on the Java 5 ThreadPoolExecutor class.
 *  Uses a monitoring thread to monitor pool thread behavior to adjust
 *  pool size.
 *
 */
public class MonitoredThreadPoolExecutor extends ThreadPoolExecutor
{
	/** Print debug messages */
	protected static final boolean DEBUG = false;
	
	/** If true, be more aggressive when creating threads. */
	protected static final boolean AGGRESSIVE = true;
	
	/** Threshold for activating monitoring. */
	protected static final int MONIT_THRESHOLD = Runtime.getRuntime().availableProcessors();
	
	/** Starting number of threads. */
	protected static final int BASE_TCNT = (MONIT_THRESHOLD << 1) + 2;
	
	/** Min. wait time between monitoring cycles. */
	protected static final long MONIT_CYCLE = 500;
	
	/** Threshold after which a _blocking_ thread is considered stolen. */
	protected static final long LOSS_THRESHOLD = 1000;
	
	/** Threshold after which a _non-blocking_ thread is considered stolen. */
	protected static final long LOSS_THRESHOLD_BUSY = 10000;
	
	/** Number of idle threads in the pool. */
	protected AtomicInteger idle;
	
	/** The threads in the pool. */
	protected volatile MonitoredThread[] threads;
	
	/** The lock for the monitoring thread. */
	protected volatile Semaphore monitoringlock = new Semaphore(0);
	
	/** Flag whether monitoring lock is enabled. */
//	protected volatile boolean lockenabled = true;
	
	/** The monitoring thread. */
	protected Thread monitthread;
	
	/** 
	 *  Lock used if the monitoring thread should wait before next round,
	 *  This is set released manually for borrowing events, so a replacement
	 *  thread can be issued quickly.
	 */
//	protected Object monitoringwaitlock = new Object();
	
	/** Flag for monitoring thread activity. */
	protected boolean monitoring = true;
	
	public MonitoredThreadPoolExecutor()
	{
		super(BASE_TCNT, BASE_TCNT,
			  Long.MAX_VALUE, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>());
		
		idle = new AtomicInteger(BASE_TCNT);
		
		threads = new MonitoredThread[BASE_TCNT];
		setThreadFactory(new ThreadFactory()
		{
			public Thread newThread(final Runnable r)
			{
				Runnable innerr = new Runnable()
				{
					public void run()
					{
						r.run();
						if (DEBUG)
							System.out.println("Thread exit: " + currentThread().getNumber());
						synchronized(MonitoredThreadPoolExecutor.this)
						{
							int i = currentThread().getNumber();
							if (i < threads.length)
								threads[i] = null;
						}
					}
				};
				MonitoredThread t = new MonitoredThread(innerr, MonitoredThreadPoolExecutor.this);
				t.setDaemon(true);
				
				synchronized(MonitoredThreadPoolExecutor.this)
				{
					for (int i = 0; i < threads.length; ++i)
					{
						if (threads[i] == null)
						{
							t.setNumber(i);
							threads[i] = t;
							break;
						}
					}
				}
				return t;
			}
		});
		
		monitthread = new Thread(new Runnable()
		{
			public void run()
			{
				while (monitoring)
				{
					if (DEBUG)
						System.out.println("ThreadPool Monitoring Wait");
					LockSupport.parkUntil(System.currentTimeMillis() + MONIT_CYCLE);
					if (DEBUG)
						System.out.println("ThreadPool Monitoring started, state idle=:" + idle.get() + " queue=" + getQueue().size() + " pool=" + getMaximumPoolSize());
					
					try
					{
//						if (lockenabled)
						Semaphore mlock = monitoringlock;
						if (mlock != null)
							mlock.acquire();
						int perms = monitoringlock.drainPermits();
						if (DEBUG)
							System.out.println("ThreadPool Monitoring Unlocked, drained " + perms + " permits.");
					}
					catch (Exception e)
					{
					}
					
					int borrowed = 0;
					long thres = System.currentTimeMillis() - LOSS_THRESHOLD;
					
					if (AGGRESSIVE)
						thres = thres / (getQueue().size() / MONIT_THRESHOLD + 1);
					
					long thresbusy = System.currentTimeMillis() - LOSS_THRESHOLD_BUSY;
					for (int i = 0; i < threads.length; ++i)
					{
						MonitoredThread thread = threads[i];
						if (thread != null)
						{
							if (!thread.isBorrowed())
							{
								if ((thread.getDeparture() < thres && thread.isBlocked()) ||
									 thread.getDeparture() < thresbusy)
								{
									borrowNoUnpark(thread);
									if (DEBUG)
										System.out.println(SUtil.getStackTraceString("Thread stolen: " + thread, thread.getStackTrace()));
								}
							}
							
							if (thread.isBorrowed())
							{
								++borrowed;
							}
							
							if (thread.getDeparture() != Long.MAX_VALUE)
								SUtil.getStackTraceString("", thread.getStackTrace());
						}
					}
					
					int adjustment = -(getMaximumPoolSize() - BASE_TCNT - borrowed);
					if (adjustment != 0)
					{
						int newsize = getMaximumPoolSize() + adjustment;
						
						if (DEBUG)
						{
							System.out.println("Adjusting pool by " + adjustment);
							System.out.println("Old size="+getMaximumPoolSize() + " new size=" +newsize);
						}
						
						if (newsize > threads.length)
						{
							synchronized(MonitoredThreadPoolExecutor.this)
							{
								MonitoredThread[] newthreads = new MonitoredThread[newsize];
								System.arraycopy(threads, 0, newthreads, 0, threads.length);
								threads = newthreads;
							}
						}
						
						if (idle.addAndGet(adjustment) > MONIT_THRESHOLD)
							monitoringlock = new Semaphore(1);
						
						if (adjustment < 0)
						{
							//Shrink
							setCorePoolSize(newsize);
							setMaximumPoolSize(newsize);
						}
						else
						{
							//Grow
							setMaximumPoolSize(newsize);
							setCorePoolSize(newsize);
						}
					}
				}
			}
		});
		monitthread.setDaemon(true);
		monitthread.start();
	}
	
	public void execute(final Runnable command)
	{
		super.execute(new Runnable()
		{
			public void run()
			{
				currentThread().setDeparture(System.currentTimeMillis());
				
				if (idle.decrementAndGet() < MONIT_THRESHOLD)
				{
					Semaphore mlock = monitoringlock;
					monitoringlock = null;
					if (mlock != null)
						releaseLock(mlock);
				}
				
				command.run();
				
				currentThread().setDeparture(Long.MAX_VALUE);
				
				if (currentThread().isBorrowed())
				{
					currentThread().borrowed = false;
					releaseLock(monitoringlock);
					LockSupport.unpark(monitthread);
				}
				
				if (idle.incrementAndGet() > MONIT_THRESHOLD && monitoringlock == null)
					monitoringlock = new Semaphore(1);
			}
		});
	}
	
	/**
	 *  Borrows the thread.
	 * @param thread Thre thread being borrowed.
	 */
	protected void borrow(MonitoredThread thread)
	{
		borrowNoUnpark(thread);
		LockSupport.unpark(monitthread);
		if (DEBUG)
			System.out.println("Borrowed: " + thread + " " + thread.getNumber());
	}
	
	/**
	 *  Borrows the thread without unparking.
	 * @param thread Thre thread being borrowed.
	 */
	protected void borrowNoUnpark(MonitoredThread thread)
	{
		thread.borrowed = true;
		releaseLock(monitoringlock);
	}
	
	/**
	 *  Releases the semaphore, includes null check.
	 *  @param lock The lock.
	 */
	protected static final void releaseLock(Semaphore lock)
	{
		if (lock != null)
			lock.release();
	}
	
	/**
	 *  Gets the current MonitoredThread, for convenience.
	 *  
	 *  @return Current MonitoredThread.
	 */
	protected static final MonitoredThread currentThread()
	{
		return (MonitoredThread) Thread.currentThread();
	}
}
