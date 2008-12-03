package jadex.commons.concurrent.java5;

import jadex.commons.concurrent.IThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *  A thread pool based on the java.util.concurrent package.
 */
public class JavaThreadPool implements IThreadPool
{
	//-------- attributes --------
	
	/** The executor service. */
	protected ExecutorService	executor;
	
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
	 *  Shutdown the thread pool.
	 */
	public void dispose()
	{
		executor.shutdown();
	}
}
