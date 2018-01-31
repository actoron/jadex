package jadex.platform.service.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SUtil;
import jadex.commons.concurrent.java5.JavaThreadPool;
import jadex.commons.concurrent.java5.MonitoredThreadPoolExecutor;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;


/**
 *  A thread pool based on the java.util.concurrent package.
 */
public class AdvancedThreadPoolService implements IThreadPoolService, IDaemonThreadPoolService
{
	/** The global executor. */
	protected static volatile ExecutorService GLOBAL_EXECUTOR;
	
	//-------- attributes --------
	
	/** The executor service. */
	protected ExecutorService	executor;
	
	/** The finished listeners. */
	protected List<IChangeListener<Void>> listeners;
	
	/** Future for pool shutdown. */
	protected Future<Void> shutdown;
	
	//-------- constructors --------
	
	/**
	 *  Create a new ThreadPool5.
	 */
	public AdvancedThreadPoolService()
	{
		this(false);
	}
	
	/**
	 *  Create a new ThreadPool5.
	 */
	public AdvancedThreadPoolService(boolean daemon)
	{
		if (GLOBAL_EXECUTOR == null)
		{
			synchronized(JavaThreadPool.class)
			{
				if (GLOBAL_EXECUTOR == null)
					GLOBAL_EXECUTOR = new MonitoredThreadPoolExecutor();
			}
		}
		
		executor = GLOBAL_EXECUTOR;
		
		shutdown = new Future<Void>();
		shutdown.addResultListener(new IResultListener<Void>()
		{
			public void exceptionOccurred(Exception exception)
			{
			}
			
			public void resultAvailable(Void result)
			{
				notifyFinishListeners();
			}
		});
	}
	
	//-------- IThreadPool interface --------

	/**
	 *  Execute a task in its own thread.
	 *  @param task The task to execute.
	 */
	public void execute(final Runnable task)
	{
		executor.execute(task);
	}
	
	/**
	 *  Execute a task in its own thread.
	 *  The pool expects the thread executing the task to never return.
	 *  Preferably use this method if you want to permanently retrieve
	 *  a thread e.g. for repeated blocking operations.
	 *  
	 *  @param task The task to execute.
	 */
	public void executeForever(Runnable task)
	{
		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}
	
	@ServiceShutdown
	public IFuture<Void> shutdown()
	{
		dispose();
		return shutdown;
	}

	/**
	 *  Shutdown the thread pool.
	 */
	public void dispose()
	{
		shutdown.setResultIfUndone(null);
	}
	
	
	/**
	 *  Test if the thread pool is running.
	 */
	public boolean	isRunning()
	{
		return !executor.isShutdown();
	}
	
	/**
	 *  Add a finish listener;
	 */
	public synchronized void addFinishListener(IChangeListener<Void> listener)
	{
		if(listeners==null)
			listeners = new ArrayList<IChangeListener<Void>>();
		listeners.add(listener);
	}
	
	/**
	 *  Notify the finish listeners.
	 */
	@SuppressWarnings("unchecked")
	protected void notifyFinishListeners()
	{
		IChangeListener<Void>[] lisar;
		synchronized(this)
		{
			lisar = listeners==null? null: listeners.toArray(new IChangeListener[listeners.size()]);
		}
		
		// Do not notify listeners in synchronized block
		if(lisar!=null)
		{
			ChangeEvent<Void> ce = new ChangeEvent<Void>(null);
			for(IChangeListener<Void> lis: lisar)
			{
				lis.changeOccurred(ce);
			}
		}
	}
	
	public static void main(String[] args)
	{
		int count = 200;
		ThreadPoolExecutor pool = new MonitoredThreadPoolExecutor();
		
		for (int i = 0; i < count; ++i)
		{
			final int num = i;
			pool.execute(new Runnable()
			{
				public void run()
				{
					System.out.println("Running: " + num);
					if (Math.random() < 0.5)
					{
						System.out.println("Blocking: " + num);
						SUtil.sleep(100000000);
					}
				}
			});
		}
		
		SUtil.sleep(5000);
//		pool.setMaximumPoolSize(count);
//		pool.setCorePoolSize(count);
		SUtil.sleep(500000);
	}
}
