package jadex.service.threadpool;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IThreadPool;
import jadex.service.IService;

/**
 *  Service wrapper for a threadpool.
 */
public class ThreadPoolService implements IThreadPool, IService
{
	//-------- attributes --------
	
	/** The threadpool. */
	protected IThreadPool threadpool;
	
	//-------- constructors -------- 
	
	/**
	 *  Create a new threadpool service.
	 */
	public ThreadPoolService(IThreadPool threadpool)
	{
		this.threadpool = threadpool;
	}
	
	//-------- methods -------- 

	/**
	 *  Start the service.
	 */
	public IFuture	startService()
	{
		// Nothing to do.
		return new Future(null); // Already done.
	}
	
	/**
	 *  Execute a task in its own thread.
	 *  @param task The task to execute.
	 */
	public void execute(Runnable task)
	{
		threadpool.execute(task);
	}

	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public IFuture	shutdownService()
	{
		threadpool.dispose();
		return new Future(null); // Already done.
	}
	
	/**
	 *  Shutdown the thread pool.
	 */
	public void dispose()
	{
		threadpool.dispose();
	}
}
