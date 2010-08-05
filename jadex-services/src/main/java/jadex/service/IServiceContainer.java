package jadex.service;

import jadex.commons.IFuture;

/**
 *  Internal interface for a service container. Allows
 *  adding and removing services. 
 */
public interface IServiceContainer extends IServiceProvider
{	
	//-------- internal methods --------
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	// todo: remove, only call from platform
	public IFuture start();
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	// todo: remove, only call from platform
	public IFuture shutdown();
	
	/**
	 *  Add a service to the container.
	 *  The service is started, if the container is already running.
	 *  @param service The service.
	 */
	public IFuture	addService(BasicService service);

	/**
	 *  Removes a service from the container (shutdowns also the service if the container is running).
	 *  @param service The service.
	 */
	public IFuture	removeService(BasicService service);
}
