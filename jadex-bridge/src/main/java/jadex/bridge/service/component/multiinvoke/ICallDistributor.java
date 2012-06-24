package jadex.bridge.service.component.multiinvoke;

import jadex.commons.future.IIntermediateFuture;

/**
 *  Interface for multiplex call distributor.
 *  
 *  It is fed with:
 *  - the services found one by one (addService)
 *  - when the search has finished (serviceSearchFinished) 
 *  
 *  It determines:
 *  - which services are called
 *  - with which arguments
 *  - when finished
 */
public interface ICallDistributor
{
	/**
	 *  Start the call distributor.
	 */
	public IIntermediateFuture<Object> start();
	
	/**
	 *  Add a new service.
	 *  @param service The service.
	 */
	public void addService(Object service);
	
	/**
	 *  Search for services has finished.
	 */
	public void serviceSearchFinished();
	
}
