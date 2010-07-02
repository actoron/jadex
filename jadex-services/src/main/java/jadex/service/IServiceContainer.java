package jadex.service;

import jadex.commons.IFuture;

/**
 *  Internal interface for a service container. Allows
 *  fetching service per type (and name). 
 */
public interface IServiceContainer extends IServiceProvider
{
	/**
	 *  Get the name of the container
	 *  @return The name of this container.
	 */
	public String getName();
	
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
