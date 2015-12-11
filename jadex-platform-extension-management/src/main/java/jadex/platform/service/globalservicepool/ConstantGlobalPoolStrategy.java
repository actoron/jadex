package jadex.platform.service.globalservicepool;

import java.util.HashSet;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;

/**
 *  Simple strategy that demands a constant number of
 *  workers from the pool.
 */
public class ConstantGlobalPoolStrategy implements IGlobalPoolStrategy
{
	/** The worker platforms. */
	protected Set<IComponentIdentifier> workers = new HashSet<IComponentIdentifier>();
	
	/** The worker timeout. */
	protected long timeout;
	
	/** The max number of workers. */
	protected int maxcnt;
	
	/** The worker per proxy count. */
	protected int wpp;
	
	/**
	 *  Create a new constant strategy.
	 */
	public ConstantGlobalPoolStrategy()
	{
		this(1000*60*5, 5, 3);
	}
	
	/**
	 *  Create a new constant strategy.
	 */
	public ConstantGlobalPoolStrategy(long timeout, int maxcnt, int wpp)
	{
		this.timeout = timeout;
		this.maxcnt = maxcnt;
		this.wpp = wpp;
	}
	
	/**
	 *  Called when a new worker was added proactively to the pool.
	 *  @param cnt The number of new workers.
	 */
	public void workersAdded(IComponentIdentifier... workers)
	{
		for(IComponentIdentifier worker: workers)
		{
			this.workers.add(worker);
		}
	}
	
	/**
	 *  Called when workers have been removed.
	 *  @param workers The workers.
	 */
	public void workersRemoved(IComponentIdentifier... workers)
	{
		for(IComponentIdentifier worker: workers)
		{
			this.workers.remove(worker);
		}
	}
	
	/**
	 *  Notify the strategy that a timeout for a worker component has occurred,
	 *  i.e. it was not needed for serving some worker.
	 *  @return True, if the component be excluded from the pool.
	 */
	public boolean workerTimeoutOccurred(IComponentIdentifier worker)
	{
		return workers.size()>maxcnt/2;
	}
	
	/**
	 *  Get the component timeout.
	 *  @return The timeout for the component to wait for new workers in the pool. 
	 */
	public long getWorkerTimeout()
	{
		return timeout;
	}
	
	/**
	 *  Get the worker cnt.
	 */
	public int getWorkerCount()
	{
		return workers.size();
	}
	
	/**
	 *  Get the desired worker cnt.
	 */
	public int getDesiredWorkerCount()
	{
		return maxcnt;
	}
	
	/**
	 *  Ask strategy if a new worker should be added on a platform.
	 *  @param cid The platform.
	 *  @return True, if worker should be created.
	 */
	public boolean isCreateWorkerOn(IComponentIdentifier cid)
	{
		return workers.size()<maxcnt;
	}
	
	/**
	 *  Get the number of workers that should be used by each proxy.
	 *  @return The number of workers used by each proxy.
	 */
	public int getWorkersPerProxy()
	{
		return wpp;
	}
}
