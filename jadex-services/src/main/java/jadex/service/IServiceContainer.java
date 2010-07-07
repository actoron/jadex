package jadex.service;

import jadex.commons.IFuture;

/**
 *  Internal interface for a service container. Allows
 *  fetching service per type (and name). 
 */
public interface IServiceContainer extends IServiceProvider
{	
	//-------- internal methods --------
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture start();
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture shutdown();
}
