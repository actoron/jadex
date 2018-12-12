package jadex.platform.service.globalservicepool;

import jadex.bridge.IComponentIdentifier;

/**
 *  Interface for a global pool strategy.
 *  Allows for:
 *  
 *  - worker management
 *  - proxy configuration
 */
public interface IGlobalPoolStrategy 
{
	/**
	 *  Called when workers have been created.
	 *  @param workers The workers.
	 */
	public void workersAdded(IComponentIdentifier... workers);
	
	/**
	 *  Called when workers have been removed.
	 *  @param workers The workers.
	 */
	public void workersRemoved(IComponentIdentifier... workers);
	
	/**
	 *  Notify the strategy that a timeout for a worker component has occurred,
	 *  i.e. it was not needed for serving some worker.
	 *  @return True, if the component be excluded from the pool.
	 */
	public boolean workerTimeoutOccurred(IComponentIdentifier worker);
	
	/**
	 *  Get the component timeout.
	 *  @return The timeout for the component to wait for new workers in the pool. 
	 */
	public long getWorkerTimeout();
	
	/**
	 *  Get the worker cnt.
	 */
	public int getWorkerCount();
	
	/**
	 *  Get the desired worker cnt.
	 */
	public int getDesiredWorkerCount();
	
	/**
	 *  Ask strategy if a new worker should be added on a platform.
	 *  @param cid The platform.
	 *  @return True, if worker should be created.
	 */
	public boolean isCreateWorkerOn(IComponentIdentifier cid);
	
	/**
	 *  Get the number of workers that should be used by each proxy.
	 *  @return The number of workers used by each proxy.
	 */
	public int getWorkersPerProxy();
}
