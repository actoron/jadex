package jadex.commons.concurrent;

/**
 *  Interface for thread pool strategies.
 */
public interface IThreadPoolStrategy
{
	/**
	 *  Called when a new task was added to the pool.
	 *  @return True, if a new thread should be added to the pool. 
	 */
	public boolean taskAdded();
	
	/**
	 *  Called when a task is finished.
	 *  @return True, if executing thread be excluded from the pool. 
	 */
	public boolean taskFinished();
	
	/**
	 *  Get the thread timeout.
	 *  @return The timeout for the thread to wait for new tasks in the pool. 
	 */
	public long getThreadTimeout();
	
	/**
	 *  Notify the strategy that a timeout for a thread has occurred,
	 *  i.e. it was not needed for serving some task.
	 *  @return True, if the thread be excluded from the pool.
	 */
	public boolean threadTimeoutOccurred();

	/**
	 *  Get the threadcnt.
	 */
	public int getThreadCount();
	
}
