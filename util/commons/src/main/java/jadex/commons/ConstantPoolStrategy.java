package jadex.commons;


/**
 *  Simple strategy with a constant number of workers.
 */
public class ConstantPoolStrategy implements IPoolStrategy
{
	/** The number of workers in the pool. */
	protected int workercnt;
	
	/** The number of free workers. */
	protected int capacity;
	
	/**
	 *  Create a new ConstantPoolStrategy.
	 */
	public ConstantPoolStrategy()
	{
		this(Runtime.getRuntime().availableProcessors()+1);
	}
	
	/**
	 *  Create a new ConstantPoolStrategy.
	 */
	public ConstantPoolStrategy(int workercnt)
	{
		this.workercnt = workercnt;
		this.capacity = workercnt;
	}

	/**
	 *  Called when a new task was added to the pool.
	 *  @return True, if a new worker should be added to the pool. 
	 */
	public boolean taskAdded()
	{
		capacity--;
		return false;
	}
	
	/**
	 *  Called when a new worker was added proactively to the pool.
	 *  @param cnt The number of new workers.
	 */
	public void workersAdded(int cnt)
	{
		throw new RuntimeException("Must not add workers in constant strategy");
	}
	
	/**
	 *  Called when a new task was served from the pool.
	 *  @param waitdur The waiting time of the task.
	 */
	public void taskServed(long waitdur)
	{
	}
	
	/**
	 *  Called when a task is finished.
	 *  @return True, if executing worker should be removed from the pool. 
	 */
	public boolean taskFinished()
	{
		capacity++;
		return false;
	}
	
	/**
	 *  Get the component timeout.
	 *  @return The timeout for the component to wait for new workers in the pool. 
	 */
	public long getWorkerTimeout()
	{
		return -1;
	}
	
	/**
	 *  Notify the strategy that a timeout for a component has occurred,
	 *  i.e. it was not needed for serving some worker.
	 *  @return True, if the component be excluded from the pool.
	 */
	public boolean workerTimeoutOccurred()
	{
		return false;
	}

	/**
	 *  Get the worker cnt.
	 */
	public int getWorkerCount()
	{
		return workercnt;
	}
	
	/**
	 *  Get the number of free workers.
	 */
	public int getCapacity()
	{
		return capacity;
	}
}
