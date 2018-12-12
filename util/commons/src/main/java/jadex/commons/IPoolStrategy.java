package jadex.commons;

/**
 *  Interface for pool strategies.
 */
public interface IPoolStrategy
{
	/**
	 *  Called when a new task was added to the pool.
	 *  @return True, if a new worker should be added to the pool. 
	 */
	public boolean taskAdded();
	
	/**
	 *  Called when a new task was served from the pool.
	 *  @param waitdur The waiting time of the task.
	 */
	public void taskServed(long waitdur);
	
	/**
	 *  Called when a task is finished.
	 *  @return True, if executing worker should be removed from the pool. 
	 */
	public boolean taskFinished();
	
	/**
	 *  Called when a new worker was added proactively to the pool.
	 *  Workers are automatically removed by the strategy as result of taskFinished().
	 *  @param cnt The number of new workers.
	 */
	public void workersAdded(int cnt);
	
	/**
	 *  Get the component timeout.
	 *  @return The timeout for the component to wait for new workers in the pool. 
	 */
	public long getWorkerTimeout();
	
	/**
	 *  Notify the strategy that a timeout for a worker component has occurred,
	 *  i.e. it was not needed for serving some worker.
	 *  @return True, if the component be excluded from the pool.
	 */
	public boolean workerTimeoutOccurred();

	/**
	 *  Get the worker cnt.
	 */
	public int getWorkerCount();
	
	/**
	 *  Get the number of free workers.
	 */
	public int	getCapacity();
}
