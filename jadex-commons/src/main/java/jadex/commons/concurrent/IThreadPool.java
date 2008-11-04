package jadex.commons.concurrent;


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
}
