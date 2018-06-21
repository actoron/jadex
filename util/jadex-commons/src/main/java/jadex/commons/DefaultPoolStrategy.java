package jadex.commons;

import java.util.LinkedList;
import java.util.List;

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

	/** Defer the creation of threads according to their distance from desfree. */ 
	protected boolean dodeferinc;
	
	/** Defer the deletion of threads according to their distance from desfree. */ 
	protected boolean dodeferdec;
	
	/** The defer factor to slow down thread creation. */
	protected int deferinc;
	protected int deferinctarget; 
	
	/** The defer factor to slow down thread deletion. */
	protected int deferdec;
	protected int deferdectarget; 
	
	/** The waiting times of the pool. */
	protected List<Double> waitings;
	
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
		this(maxcnt, desfree, -1, maxcnt);
	}
	
	/**
	 *  Create a new default pool strategy.
	 */
	public DefaultPoolStrategy(int desfree, long maxwait, int maxcnt)
	{
		this(maxcnt, desfree, maxwait, maxcnt);
	}
	
	/**
	 *  Create a new default pool strategy.
	 */
	public DefaultPoolStrategy(int workercnt, int desfree, long maxwait, int maxcnt)
	{
		this(workercnt, desfree, maxwait, maxcnt, true);
	}
	
	/**
	 *  Create a new default pool strategy.
	 */
	public DefaultPoolStrategy(int workercnt, int desfree, long maxwait, int maxcnt, boolean defer)
	{
		this(workercnt, desfree, maxwait, maxcnt, false, defer);
//		this.workercnt = workercnt;
//		this.capacity = workercnt;
//		this.desfree = desfree;
//		this.maxwait = maxwait;
//		this.maxcnt = maxcnt;
//		this.dodeferdec = defer;
//		this.waitings = new LinkedList<Double>();
//		
//		// Can't defer creation when threads are blocking as it might cause deadlocks
//		this.dodeferinc	= false;
	}

	/**
	 *  Create a new default pool strategy.
	 */
	public DefaultPoolStrategy(int workercnt, int desfree, long maxwait, int maxcnt, boolean deferinc, boolean deferdec)
	{
		this.workercnt = workercnt;
		this.capacity = workercnt;
		this.desfree = desfree;
		this.maxwait = maxwait;
		this.maxcnt = maxcnt;
		this.dodeferinc = deferinc;
		this.dodeferdec = deferdec;
		this.waitings = new LinkedList<Double>();
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
//		double wt = waitings.size()>0? waitings.get(waitings.size()-1): 1;
		boolean ok = false;
		if(capacity<=0 && (maxcnt<=0 || workercnt<maxcnt))
		{
			if(dodeferinc)
			{
				int dt = workercnt/desfree;
				if(dt>2)
				{
					if(deferinc==0)
					{
						deferinctarget = dt*10;
					}
					deferinc++;
					if(deferinc==deferinctarget)
					{
						deferinc=0;
						ok = true;
					}
				}
				else
				{
					ok = true;
					deferinc = 0;
				}
			}
			else
			{
				ok = true;
			}
		}
		
		if(ok)
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
		
//		System.out.println("Capacity: "+capacity+" "+workercnt);
		
		return ret;
	}
	
	/**
	 *  Called when a new worker was added proactively to the pool.
	 *  @param cnt The number of new workers.
	 */
	public synchronized void workersAdded(int cnt)
	{
		workercnt += cnt;
		capacity += cnt;
	}
	
	/**
	 *  Called when a new task was served from the pool.
	 *  @param waitdur The waiting time of the task.
	 */
	public synchronized void taskServed(long waitdur)
	{
//		long dur = Math.max(1, waitdur);
//		double last = waitings.isEmpty()? 1: waitings.get(waitings.size()-1);
//		waitings.add(new Double(last*0.4+dur*0.6));
//		if(waitings.size()>10)
//			waitings.remove(0);
		
//		System.out.println("waiting times: "+waitings);
	}
	
	/**
	 *  Called when a task is finished.
	 *  @return True, if executing worker be excluded from the pool. 
	 */
	public synchronized boolean taskFinished()
	{
		boolean ret = false;

		boolean ok = false;
		if(capacity>=desfree)
		{
			if(dodeferdec)
			{
				int dt = workercnt/desfree;
				if(dt>2)
				{
					if(deferdec==0)
					{
						int max = maxcnt>0? maxcnt: 1000;
						deferdectarget = Math.max(10, (int)(1.0/dt*max));
//						System.out.println("defertarget: "+deferdectarget+" "+workercnt+" "+desfree);
					}
					deferdec++;
					if(deferdec==deferinctarget)
					{
						deferdec=0;
						ok = true;
					}
				}
				else
				{
					ok = true;
					deferdec = 0;
				}
			}
			else
			{
				ok = true;
			}
		}
		
//		If more free workers than desired capacity let worker end.
		if(ok)
//		if(capacity>=desfree)
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
		
//		System.out.println("Capacity: "+capacity+" "+workercnt);
		
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
			ret = Math.max((long)(ratio*maxwait), maxwait/10);  // must have lower bound otherwise could be 0
//			System.out.println("waittime: "+ret);
//			ret = maxwait;
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
		boolean ret = false;

		boolean ok = false;
		if(capacity>desfree)
		{
			if(dodeferdec)
			{
				int dt = workercnt/desfree;
				if(dt>2)
				{
					if(deferdec==0)
					{
						int max = maxcnt>0? maxcnt: 1000;
						deferdectarget = Math.max(10, (int)(1.0/dt*max));
//						System.out.println("defertarget: "+deferdectarget+" "+workercnt+" "+desfree);
					}
					deferdec++;
					if(deferdec==deferinctarget)
					{
						deferdec=0;
						ok = true;
					}
				}
				else
				{
					ok = true;
					deferdec = 0;
				}
			}
			else
			{
				ok = true;
			}
		}
		
//		If more free workers than desired capacity let worker end.
		if(ok)
		{
			ret = true;
			workercnt--;
			capacity--;
		}
		
//		System.out.println("Capacity: "+capacity+" "+workercnt);
		
		return ret;
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
