package jadex.commons.concurrent.java5;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jadex.commons.SUtil;

public class MonitoredThreadPoolExecutor extends ThreadPoolExecutor
{
	/** Print debug messages */
	protected static final boolean DEBUG = true;
	
	/** Threshold for activating monitoring. */
	protected static final int MONIT_THRESHOLD = Runtime.getRuntime().availableProcessors();
	
	/** Starting number of threads. */
	protected static final int BASE_TCNT = Runtime.getRuntime().availableProcessors() + (MONIT_THRESHOLD << 1);
	
	/** Min. wait time between monitoring cycles. */
	protected static final long MONIT_CYCLE = 500;
	
	/** Threshold after which a _blocking_ thread is considered stolen. */
	protected static final long LOSS_THRESHOLD = 1000;
	
	/** Threshold after which a _running_ thread is considered stolen. */
	protected static final long LOSS_THRESHOLD_BUSY = 60000;
	
	/** Number of idle threads in the pool. */
	protected AtomicInteger idle;
	
	/** The threads in the pool. */
	protected volatile MonitoredThread[] threads;
	
	/** The lock for the monitoring thread. */
	protected volatile Semaphore monitoringlock = new Semaphore(0);
	
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
			public synchronized Thread newThread(final Runnable r)
			{
				Runnable innerr = new Runnable()
				{
					public void run()
					{
						r.run();
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
				idle.incrementAndGet();
				return t;
			}
		});
		
		Thread monit = new Thread(new Runnable()
		{
			public void run()
			{
				while (monitoring)
				{
					try
					{
						if (DEBUG)
							System.out.println("ThreadPool Monitoring Wait");
						SUtil.sleep(MONIT_CYCLE);
						monitoringlock.drainPermits();
						if (DEBUG)
							System.out.println("ThreadPool Monitoring Unlocked");
					}
					catch (Exception e)
					{
					}
					
					int exceeded = 0;
					long thres = System.currentTimeMillis() - LOSS_THRESHOLD;
					long thresbusy = System.currentTimeMillis() - LOSS_THRESHOLD_BUSY;
					for (int i = 0; i < threads.length; ++i)
					{
						MonitoredThread thread = threads[i];
						if (thread != null)
						{
						
							if ((thread.getDeparture() < thres && thread.isBlocked()) ||
								 thread.getDeparture() < thresbusy)
							{
								++exceeded;
								if (DEBUG)
								{
									System.out.println("Thread stolen: " + thread);
									StackTraceElement[] trace = thread.getStackTrace();
									for (StackTraceElement traceElement : trace)
										System.err.println("\tat " + traceElement);
								}
	//							System.out.println("Thread exceeded return time; " + threads[i]);
							}
						}
					}
					
					exceeded = -(getMaximumPoolSize() - BASE_TCNT - exceeded);
					if (exceeded > 0)
					{
						System.err.println("Thread loss detected, adjusting pool by " + exceeded);
						
						int newsize = getMaximumPoolSize() + exceeded;
						System.err.println("Old size="+getMaximumPoolSize() + " new size=" +newsize);
						
						if (newsize > threads.length)
						{
							synchronized(MonitoredThreadPoolExecutor.this)
							{
								MonitoredThread[] newthreads = new MonitoredThread[newsize];
								System.arraycopy(threads, 0, newthreads, 0, threads.length);
								threads = newthreads;
							}
						}
						
						setMaximumPoolSize(newsize);
						setCorePoolSize(newsize);
					}
				}
			}
		});
		monit.setDaemon(true);
		monit.start();
	}
	
	public void execute(final Runnable command)
	{
		super.execute(new Runnable()
		{
			public void run()
			{
				currentThread().setDeparture(System.currentTimeMillis());
				
				if (idle.decrementAndGet() < MONIT_THRESHOLD)
					monitoringlock.release();
				
				command.run();
				currentThread().setDeparture(Long.MAX_VALUE);
				idle.incrementAndGet();
			}
		});
	}
	
	protected void borrow()
	{
	}
	
	protected static MonitoredThread currentThread()
	{
		return (MonitoredThread) Thread.currentThread();
	}
}
