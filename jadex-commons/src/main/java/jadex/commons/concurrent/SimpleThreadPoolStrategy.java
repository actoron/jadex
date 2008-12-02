package jadex.commons.concurrent;

/**
 * 
 */
public class SimpleThreadPoolStrategy implements IThreadPoolStrategy
{
	/** The number of threads in the pool. */
	protected int threadcnt;
	
	/** The number of free threads. */
	protected int capacity;
	
	/** The desired number of free threads. */
	protected int desfree;
	
	/** The thread pool. */
	protected StrategyThreadPool tp;
	
	/**
	 * 
	 */
	public SimpleThreadPoolStrategy(int threadcnt, int desfree)
	{
		this.threadcnt = threadcnt;
		this.capacity = threadcnt;
		this.desfree = desfree;
	}

	/**
	 * 
	 */
	public void setThreadPool(StrategyThreadPool tp)
	{
		this.tp = tp;
	}
	
	/**
	 * 
	 */
	public synchronized void taskAdded()
	{
		// Create a new thread if capacity is lower than desired capacity.
		if(capacity<desfree)
		{
			tp.addThreads(1);
			threadcnt++;
			System.out.println("Capacity: "+capacity+" "+threadcnt);
		}
		else
		{
			capacity--;
		}
	}
	
	/**
	 * 
	 */
	public synchronized boolean taskFinished()
	{
		boolean ret = false;

		// If more free threads than desired capacity let thread end.
		if(capacity>desfree)
		{
			ret = true;
			threadcnt--;
			System.out.println("Capacity: "+capacity+" "+threadcnt);
		}
		else
		{
			capacity++;
		}
		
		return ret;
	}
}
