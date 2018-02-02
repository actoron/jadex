package jadex.commons.concurrent.java5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.concurrent.IThreadPool;


/**
 *  A thread pool based on the java.util.concurrent package.
 */
public class JavaThreadPool implements IThreadPool
{
	//-------- attributes --------
	
	/** The executor service. */
	protected ExecutorService	executor;
	
	/** The finished listeners. */
	protected List<IChangeListener<Void>> listeners; 
	
	/** Boolean if already finished. */
	protected boolean finished;
	
	//-------- constructors --------
	
	/**
	 *  Create a new ThreadPool5.
	 */
	public JavaThreadPool()
	{
		System.out.println("Using Java 5.0 ThreadPool");
		executor	= Executors.newCachedThreadPool();
//		executor	= Executors.newFixedThreadPool(20);
	}
	
	//-------- IThreadPool interface --------

	/**
	 *  Execute a task in its own thread.
	 *  @param task The task to execute.
	 */
	public void execute(Runnable task)
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

	/**
	 *  Shutdown the thread pool.
	 */
	public void dispose()
	{
		boolean notify = false;
		synchronized(this)
		{
			if(!finished)
			{
				finished = true;
				notify = true;
			}
		}
		
		if(notify)
		{
			executor.shutdown();
			
			new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						executor.awaitTermination(10000, TimeUnit.MILLISECONDS);	// Hack???
					}
					catch(Exception e)
					{
					}
					notifyFinishListeners();
				}
			}).start();
		}
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
}
