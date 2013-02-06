package jadex.commons;

/**
 *  This strategy has two parameters:
 *  - The desired number of free workers in the pool.
 *  - The maximum timeout for workers to wait for new tasks.
 */
public class DefaultPoolStrategy implements IPoolStrategy
{
	//-------- attributes --------
	
	/** The number of workers in the pool. */
	protected int workercnt;
	
	/** The number of free workers. */
	protected int capacity;
	
	/** The desired number of free workers. */
	protected int desfree;
	
	/** The max wait time for workers. */
	protected long maxwait;
	
	/** The maximum number of allowed workers. */
	protected int maxcnt;
	
	//-------- constructors --------
	
	/**
	 *  Create a new default pool strategy.
	 */
	public DefaultPoolStrategy()
	{
	}
	
	/**
	 *  Create a new default pool strategy.
	 */
	public DefaultPoolStrategy(int desfree, int maxcnt)
	{
		this(0, desfree, -1, maxcnt);
	}
	
	/**
	 *  Create a new default pool strategy.
	 */
	public DefaultPoolStrategy(int desfree, long maxwait, int maxcnt)
	{
		this(0, desfree, maxwait, maxcnt);
	}
	
	/**
	 *  Create a new default pool strategy.
	 */
	public DefaultPoolStrategy(int workercnt, int desfree, long maxwait, int maxcnt)
	{
		this.workercnt = workercnt;
		this.capacity = workercnt;
		this.desfree = desfree;
		this.maxwait = maxwait;
		this.maxcnt = maxcnt;
	}

	//-------- methods --------
	
	/**
	 *  Called when a new task was added to the pool.
	 *  @return True, if a new worker should be added to the pool. 
	 */
	public synchronized boolean taskAdded()
	{
		boolean ret = false;
		
		// Create a new worker if capacity is lower than desired capacity.
		if(capacity<=0 && (maxcnt<=0 || workercnt<maxcnt))
		{
			ret = true;
			workercnt++;
//			System.out.println("Capacity(tA1): "+capacity+" "+workercnt);
		}
		else
		{
			capacity--;
//			System.out.println("Capacity(tA2): "+capacity+" "+workercnt);
		}
		
		return ret;
	}
	
	/**
	 *  Called when a task is finished.
	 *  @return True, if executing worker be excluded from the pool. 
	 */
	public synchronized boolean taskFinished()
	{
		boolean ret = false;

		// If more free workers than desired capacity let worker end.
		if(capacity>=desfree)
		{
			ret = true;
			workercnt--;
//			System.out.println("Capacity(tF1): "+capacity+" "+workercnt);
		}
		else
		{
			capacity++;
//			System.out.println("Capacity(tF2): "+capacity+" "+workercnt);
		}
		
		return ret;
	}
	
	/**
	 *  Get the worker timeout.
	 *  @return The timeout for the worker to wait for new tasks in the pool. 
	 */
	public synchronized long getWorkerTimeout()
	{
		long ret;
		
		if(maxwait<=0)
		{
			ret = 0;
		}
		else
		{
			// negative e^x used to get maximum wait time when
			// few free workers and high capacity
			double ratio = Math.exp(-3.0*(((double)capacity)/desfree));
			ret = (long)(ratio*maxwait);
//			if(maxwait==35000)
//				System.out.println("Wait time: "+ret+" "+capacity+" "+desfree);
		}
		
		return ret;
	}
	
	/**
	 *  Notify the strategy that a timeout for a worker has occurred,
	 *  i.e. it was not needed for serving some task.
	 *  @return True, if the worker be excluded from the pool.
	 */
	public synchronized boolean workerTimeoutOccurred()
	{
		workercnt--;
		capacity--;
//		System.out.println("Capacity(tTO): "+capacity+" "+workercnt);
		return true;
		
//		boolean ret = false;
//		
//		if(capacity>desfree)
//		{
//			ret = true;
//			workercnt--;
////			System.out.println("Capacity: "+capacity+" "+workercnt);
//		}
//		
//		return ret;
	}
	
	//-------- getter/setter --------
	
	/**
	 *  Get the worker cnt.
	 */
	public int getWorkerCount()
	{
		return workercnt;
	}

	/**
	 *  Set the worker cnt.
	 *  @param workercnt The workercnt to set.
	 */
	public void setWorkerCount(int workercnt)
	{
		this.workercnt = workercnt;
	}

	/**
	 *  Get the capacity.
	 *  @return The capacity.
	 */
	public int getCapacity()
	{
		return capacity;
	}

	/**
	 *  Set the capacity.
	 *  @param capacity The capacity to set.
	 */
	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}

	/**
	 *  Get the desfree.
	 *  @return The desfree.
	 */
	public int getDesiredFree()
	{
		return desfree;
	}

	/**
	 *  Set the desfree.
	 *  @param desfree The desfree to set.
	 */
	public void setDesiredFree(int desfree)
	{
		this.desfree = desfree;
	}

	/**
	 *  Get the maxwait.
	 *  @return The maxwait.
	 */
	public long getMaxWait()
	{
		return maxwait;
	}

	/**
	 *  Set the maxwait.
	 *  @param maxwait The maxwait to set.
	 */
	public void setMaxWait(long maxwait)
	{
		this.maxwait = maxwait;
	}

	/**
	 *  Get the max cnt.
	 *  @return The max cnt.
	 */
	public int getMaxCount()
	{
		return maxcnt;
	}

	/**
	 *  Set the max cnt.
	 *  @param maxservicecnt The max cnt to set.
	 */
	public void setMaxCount(int maxcnt)
	{
		this.maxcnt = maxcnt;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "DefaultPoolStrategy(workercnt=" + workercnt + ", capacity="
			+ capacity + ", desfree=" + desfree + ", maxwait=" + maxwait
			+ ", maxcnt=" + maxcnt+")";
	}
	
	
}
