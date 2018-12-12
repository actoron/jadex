package jadex.commons.concurrent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;


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

	//-------- attributes --------

	/** Flag indicating if the thread is running. */
	protected boolean running;

	/** Flag indicating if the thread wants to run. */
	protected boolean wanttorun;

	/** Flag indicating that the executor shuts down. */
	private boolean	shutdown;
	
	/** Flag indicating that the executor has shutdowned. */
	private boolean	shutdowned;
	
	/** The thread pool. */
	protected IThreadPool threadpool;
	
	/** The executable. */
	protected IExecutable executable;
	
	/** The shutdown futures. */
	protected List<Future<Void>> shutdownfutures;
	
	/** The monitor to synchronize with at thread start (if any). */
	protected Object monitor; 
	
	/** The number of current threads for this executor. */
	protected int	exethreadcnt;
	
	/** The monitors of blocked threads that need to be reactivated. */
	protected List<Object>	switchtos;
	
	/** The exceptions (if any) to be thrown in threads that need to be reactivated. */
	protected Map<Object, Throwable>	throwables;
	
//	protected Thread thread;
		
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
//		System.out.println("create: "+executable+" "+threadpool.getClass());
	}
		
	//-------- methods --------
	
	/**
	 *  Execute the code. 
	 */
	public void run()
	{
		synchronized(this)
		{
			exethreadcnt++;
		}
		
		if(monitor!=null)
		{
			//FINDBUGS: The monitor is used here as a barrier to wait for another thread.
			synchronized(monitor){}
		}
		
		EXECUTOR.set(this);
				
		// running is already set to true in execute()
		
		boolean	iwanttorun	= true;
		Object	switchto	= null;
		
		while(iwanttorun && !shutdown)
		{
			try
			{
				iwanttorun = code();
			}
			catch(ThreadDeath e)
			{
				// ignore, just stop task
				iwanttorun	= false;
			}
			catch(Throwable e)
			{
				// Print exception and stop task
				System.err.println("Exception in executable "+executable+": "+SUtil.getExceptionStacktrace(e));
				iwanttorun	= false;
			}

			synchronized(this)
			{
				if(switchtos!=null && switchtos.size()>0)
				{
					switchto = switchtos.remove(0);
				}
			}
			
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
				iwanttorun	= false;
			}
		}

		// Notify shutdown listeners when execution has ended.
		List<Future<Void>> futures = null;
		synchronized(this)
		{
			if(shutdown && !shutdowned && (switchtos==null || switchtos.isEmpty()))
			{
				futures = new ArrayList<Future<Void>>(shutdownfutures);
				shutdownfutures.clear();
				shutdowned = true;
			}
			
			if(switchto==null && switchtos!=null && switchtos.size()>0)
			{
				switchto = switchtos.remove(0);
			}
		}
		if(futures!=null)
		{
			for(int i=0; i<futures.size(); i++)
				futures.get(i).setResult(null);
		}
		
		EXECUTOR.set(null);
				
		synchronized(this)
		{
			exethreadcnt--;
		}
		
		if(switchto!=null)
		{
			synchronized(switchto)
			{
				switchto.notify();
			}
		}
	}

	/**
	 *  Make sure a thread is executing the code.
	 */
	public boolean	execute()
	{
		boolean	execute	= false;
		
		synchronized(this)
		{
			if(!shutdown)
			{
				if(running)
				{
					// Indicate that thread should continue to run (if running).
					wanttorun	= true;
				}
				else
				{
					// Invoke the code of the executor object using the thread pool,
					// which allows thread to be shared, when code is idle.
					running	= true;
					execute	= true;
				}
			}
			
//			if(toString().indexOf("Leaker")!=-1)
//			{
//				System.out.println("execute(): "+this+", "+execute+", "+running+", "+shutdown+", "+wanttorun);
//			}
		}

		if(execute)
		{
			threadpool.execute(this);
		}
		
		return execute;
	}

	/**
	 *  Shutdown the executor.
	 */
	public IFuture<Void>	shutdown()
	{
		Future<Void>	ret	= new Future<Void>();
		
//		if(toString().indexOf("Tester@")!=-1)
//		{
//			System.out.println("shutdown exe: ");
//		}
		
		boolean directnotify = false;
		synchronized(this)
		{
			if(!shutdowned)
			{
				shutdownfutures.add(ret);
				execute();
			}
			else
			{
				directnotify = true;
			}
			shutdown = true;
		}
		
		if(directnotify)
		{
			ret.setResult(null);
		}
		
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
	 *  Get the number of threads (running blocked)
	 *  for this executor.
	 */
	protected int	getThreadCount()
	{
		return exethreadcnt;
	}

	/**
	 *  Cease execution of the current thread and
	 *  switch to another thread waiting for the given monitor.
	 *  @param monitor	The monitor to be notified.
	 *  @param t	The exception to be thrown on the unblocked thread (null for continuing normal execution).
	 */
	public void	switchThread(Object monitor, Throwable t)
	{
//		System.out.println("switchThread: "+monitor+", "+t);

		synchronized(this)
		{
//			System.out.println("switchThread1: "+monitor+", "+t);
			if(switchtos==null)
			{
				switchtos = new LinkedList<Object>();
			}
			switchtos.add(monitor);
			
			if(t!=null)
			{
				if(throwables==null)
				{
					throwables = new HashMap<Object, Throwable>();
				}
				throwables.put(monitor, t);
			}
		}
		
		// Make sure execution is running.
		execute();
	}

	/**
	 *  Adjust to execution of current thread to be blocked.
	 */
	public void	blockThread(Object monitor)
	{
//		System.out.println("Executor.blockThread "+Thread.currentThread());
		
		synchronized(monitor)
		{
			this.running = false;
			this.monitor = monitor;

			// Todo: decide if a new thread is needed
			// Hack!!! create new thread anyways
			execute();
			
			// Notify advanced thread pool that the thread is
			// borrowed / about to be blocked.
			MonitoredThread.tryBorrow();
			
			try
			{
				monitor.wait();
//				if(toString().indexOf("Leaker")!=-1)
//				{
//					System.out.println("after wait()");
//				}
			}
			catch(InterruptedException e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				this.running = true;
//				if(toString().indexOf("Leaker")!=-1)
//				{
//					System.out.println("resumed: "+monitor+", "+(throwables!=null ? throwables.get(monitor):""));
//				}
			}

			if(throwables!=null)
			{
				Throwable	t	= throwables.remove(monitor);
				if(t!=null)
				{
					SUtil.throwUnchecked(t);
				}
			}			
		}
		
//		System.out.println("Executor.blockThreadFinished "+Thread.currentThread());
	}

	/**
	 *  String representation.
	 */
	public String toString()
	{
		return "Executor("+executable+")";
	}
	
	/**
	 *  Check if the executor wants to switch threads.
	 */
	public boolean	isSwitching()
	{
		return switchtos!=null && !switchtos.isEmpty();
	}
}
