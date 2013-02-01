package jadex.commons.concurrent;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.ArrayList;
import java.util.List;


/**
 *  A helper class for running a single instance
 *  of code using the thread pool.
 *  The code to be executed has to be placed in the
 *  code() method.
 *  Once created, the execute() method may be called
 *  as often as desired. When no thread is currently
 *  executing the object, a new thread is used from the
 *  thread pool. Otherwise, the already existing thread
 *  continues execution.
 *  After shutdown() is called, the executor stops
 *  execution, even when execute() is called afterwards.
 */
public class Executor implements Runnable
{
	//-------- constants --------
	
	/** The executor belonging to a thread. */
	public static final ThreadLocal<Executor>	EXECUTOR	= new ThreadLocal<Executor>();

	/** The set to a monitor of a blocked thread to perform a context switch. */
	protected static final ThreadLocal<Object>	SWITCH_TO	= new ThreadLocal<Object>();
	
	//-------- attributes --------

	/** Flag indicating if the thread is running. */
	protected boolean	running;

	/** Flag indicating if the thread wants to run. */
	protected boolean	wanttorun;

	/** Flag indicating that the executor shuts down. */
	protected boolean	shutdown;
	
	/** Flag indicating that the executor has shutdowned. */
	protected boolean	shutdowned;
	
	/** The thread pool. */
	protected IThreadPool threadpool;
	
	/** The executable. */
	protected IExecutable executable;
	
	/** The shutdown futures. */
	protected List<Future<Void>> shutdownfutures;
	
	/** The monitor to synchronize with at thread start (if any). */
	protected Object monitor; 
		
	//--------- constructors --------

	/**
	 *  Create an executor object.
	 *  Constructor for subclasses overriding
	 *  the code() method.
	 */
	public Executor(IThreadPool threadpool)
	{
		this(threadpool, null);
	}
	
	/**
	 *  Create an executor object.
	 */
	public Executor(IThreadPool threadpool, IExecutable executable)
	{
		if(threadpool==null)
			throw new IllegalArgumentException("Threadpool must not null.");
		
		this.threadpool = threadpool;
		this.executable = executable;
		this.shutdownfutures = new ArrayList<Future<Void>>();
	}
		
	//-------- methods --------
	
	/**
	 *  Execute the code. 
	 */
	public void run()
	{
		if(monitor!=null)
		{
			System.out.println("Executor.run"+Thread.currentThread());
			synchronized(monitor){}
		}
		
		EXECUTOR.set(this);
		
		// running is already set to true in execute()
		
		boolean	iwanttorun	= true;
		while(iwanttorun && !shutdown)
		{
			iwanttorun	=	code();

			Object	switchto	= SWITCH_TO.get();
			if(switchto==null)
			{
				// Setting flags in synchronized block assures,
				// that execute is not called in between.
				// Separating running and myrunning allows that this thread
				// may terminate (myrunning==false) while a new thread
				// is already starting (running==true).
				synchronized(this)
				{
					//if(iwanttorun)	System.out.println("continuing: "+this);
					//else if(wanttorun)	System.out.println("forced to continue: "+this);
					iwanttorun	= iwanttorun || wanttorun;
					running	= iwanttorun;
					wanttorun	= false;	// reset until execute() is called again.
				}
			}
			else
			{
				SWITCH_TO.set(null);
				iwanttorun	= false;
				synchronized(switchto)
				{
					switchto.notify();
				}
			}
		}

		// Notify shutdown listeners when execution has ended.
		List<Future<Void>> futures = null;
		synchronized(this)
		{
			if(shutdown)
			{
				futures = new ArrayList<Future<Void>>(shutdownfutures);
				shutdownfutures.clear();
			}
		}
		if(futures!=null)
		{
			for(int i=0; i<futures.size(); i++)
				futures.get(i).setResult(null);
			
			synchronized(this)
			{
				shutdowned = true;
			}
		}
		
		EXECUTOR.set(null);
	}

	/**
	 *  Make sure a thread is executing the code.
	 */
	public void execute()
	{
		boolean	execute	= false;
		
		synchronized(this)
		{
//			Thread.dumpStack();
			if(!shutdown)
			{		
				// Indicate that thread should continue to run (if running).
				wanttorun	= true;
				
				if(!running)
				{
					running	= true;
					// Invoke the code of the executor object using the thread pool,
					// which allows thread to be shared, when code is idle.
					execute	= true;
				}
			}
//			System.out.println("executing: "+this+" "+running+", "+execute);
		}

		if(execute)
			threadpool.execute(this);
	}

	/**
	 *  Shutdown the executor.
	 */
	public IFuture<Void>	shutdown()
	{
		Future<Void>	ret	= new Future<Void>();
		
		boolean directnotify = false;
		synchronized(this)
		{
			shutdown = true;
			if(!shutdowned)
			{
				shutdownfutures.add(ret);
			}
			else
			{
				directnotify = true;
			}
		}
		
		if(directnotify)
			ret.setResult(null);
		
		return ret;
	}
	
	/**
	 *  Set the executable.
	 *  @param executable The executable.
	 */
	public void setExecutable(IExecutable executable)
	{
//		if(this.executable!=null)
//			throw new RuntimeException("Setting executable allowed only once.");
		this.executable = executable;
	}
	
	/**
	 *  The code to be run.
	 *  @return True, when execution should continue.
	 */
	protected boolean code()
	{
		return executable.execute();
	}

	/**
	 *  Check if the executor is running.
	 *  Should only be called, when access to executor is
	 *  correctly synchronized, otherwise inconsistent values might be returned.
	 */
	protected boolean isRunning()
	{
		return running;
	}

	/**
	 *  Cease execution of the current thread and
	 *  switch to another thread waiting for the given monitor.
	 */
	public void	switchThread(Object monitor)
	{
		System.out.println("Executor.switchThread"+Thread.currentThread());
		SWITCH_TO.set(monitor);
	}

	/**
	 *  Adjust to execution of current thread to be blocked.
	 */
	public void	blockThread(Object monitor)
	{
		System.out.println("Executor.threadBlocked "+Thread.currentThread());
		running	= false;
		this.monitor	= monitor;

		synchronized(monitor)
		{
			// Todo: decide if a new thread is needed
			// Hack!!! create new thread anyways
			execute();
			
			try
			{
				monitor.wait();
			}
			catch(InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		System.out.println("Executor.threadUnBlocked "+Thread.currentThread());
		running	= true;
	}
}
