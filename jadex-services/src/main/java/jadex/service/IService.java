package jadex.service;

import jadex.commons.IFuture;

/**
 *  The interface for platform services.
 */
public interface IService
{
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture	startService();
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture	shutdownService();
}
