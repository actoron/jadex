package jadex.bridge.service;

import jadex.commons.future.IFuture;

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
	 *  Set the service identifier.
	 */
	public void createServiceIdentifier(String name, Class implclazz);
}
