package jadex.commons.concurrent;

/**
 *  This strategy has two parameters:
 *  - The desired number of free threads in the pool.
 *  - The maximum timeout for threads to wait for new tasks.
 */
public class DefaultThreadPoolStrategy implements IThreadPoolStrategy
{
	//-------- attributes --------
	
	/** The number of threads in the pool. */
	protected int threadcnt;
	
	/** The number of free threads. */
	protected int capacity;
	
	/** The desired number of free threads. */
	protected int desfree;
	
	/** The max wait time for threads. */
	protected long maxwait;
	
	/** The maximum number of allowed threads. */
	protected int maxthreadcnt;
	
	//-------- constructors --------
	
	/**
	 *  Create a new default threadpool strategy.
	 */
	public DefaultThreadPoolStrategy(int threadcnt, int desfree, long maxwait, int maxthreadcnt)
	{
		this.threadcnt = threadcnt;
		this.capacity = threadcnt;
		this.desfree = desfree;
		this.maxwait = maxwait;
		this.maxthreadcnt = maxthreadcnt;
	}

	//-------- methods --------
	
	/**
	 *  Called when a new task was added to the pool.
	 *  @return True, if a new thread should be added to the pool. 
	 */
	public synchronized boolean taskAdded()
	{
		boolean ret = false;
		
		// Create a new thread if capacity is lower than desired capacity.
		if(capacity<=0 && (maxthreadcnt<=0 || threadcnt<maxthreadcnt))
		{
			ret = true;
			threadcnt++;
//			System.out.println("Capacity(tA1): "+capacity+" "+threadcnt);
		}
		else
		{
			capacity--;
//			System.out.println("Capacity(tA2): "+capacity+" "+threadcnt);
		}
		
		return ret;
	}
	
	/**
	 *  Called when a task is finished.
	 *  @return True, if executing thread be excluded from the pool. 
	 */
	public synchronized boolean taskFinished()
	{
		boolean ret = false;

		// If more free threads than desired capacity let thread end.
		if(capacity>=desfree)
		{
			ret = true;
			threadcnt--;
//			System.out.println("Capacity(tF1): "+capacity+" "+threadcnt);
		}
		else
		{
			capacity++;
//			System.out.println("Capacity(tF2): "+capacity+" "+threadcnt);
		}
		
		return ret;
	}
	
	/**
	 *  Get the thread timeout.
	 *  @return The timeout for the thread to wait for new tasks in the pool. 
	 */
	public synchronized long getThreadTimeout()
	{
		long ret;
		
		if(maxwait<=0)
		{
			ret = 0;
		}
		else
		{
			double ratio = Math.exp(-3.0*(((double)capacity)/desfree));
			ret = (long)(ratio*maxwait);
//			System.out.println("Wait time: "+ret+" "+capacity+" "+desfree);
		}
		
		return ret;
	}
	
	/**
	 *  Notify the strategy that a timeout for a thread has occurred,
	 *  i.e. it was not needed for serving some task.
	 *  @return True, if the thread be excluded from the pool.
	 */
	public synchronized boolean threadTimeoutOccurred()
	{
		threadcnt--;
		capacity--;
//		System.out.println("Capacity(tTO): "+capacity+" "+threadcnt);
		return true;
		
//		boolean ret = false;
//		
//		if(capacity>desfree)
//		{
//			ret = true;
//			threadcnt--;
////			System.out.println("Capacity: "+capacity+" "+threadcnt);
//		}
//		
//		return ret;
	}
	
	/**
	 *  Get the threadcnt.
	 */
	public int getThreadCount()
	{
		return threadcnt;
	}
}
