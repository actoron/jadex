package jadex.service.threadpool;

import jadex.commons.concurrent.IResultListener;
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
	public void start()
	{
		// Nothing to do.
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
	public void shutdown(IResultListener listener)
	{
		threadpool.dispose();
		if(listener!=null)
			listener.resultAvailable(null);
	}
	
	/**
	 *  Shutdown the thread pool.
	 */
	public void dispose()
	{
		threadpool.dispose();
	}
}
