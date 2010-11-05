package jadex.commons.service;

import jadex.commons.IFuture;

/**
 * 
 */
public interface IInternalService extends IService
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
	
	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 */
	public boolean isValid();
}
