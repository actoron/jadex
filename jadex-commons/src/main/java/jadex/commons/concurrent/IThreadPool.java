package jadex.commons.concurrent;

import jadex.commons.IChangeListener;

/**
 *  Common interface for different thread pool implementations.
 */
public interface IThreadPool
{
	/**
	 *  Execute a task in its own thread.
	 *  @param task The task to execute.
	 */
	public void execute(Runnable task);

	/**
	 *  Shutdown the thread pool.
	 */
	public void dispose();
	
	/**
	 *  Test if the thread pool is running.
	 */
	public boolean	isRunning();
	
	/**
	 *  Add a finished listener.
	 */
	public void addFinishListener(IChangeListener<Void> listener);
}
