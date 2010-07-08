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
	
	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param name The name.
	 *  @param service The service.
	 */
	public IFuture addService(Class type, Object service);

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param name The name.
	 *  @param service The service.
	 */
	public IFuture removeService(Class type, Object service);
}
