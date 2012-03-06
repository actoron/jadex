package jadex.bridge.service;

import jadex.bridge.IResourceIdentifier;
import jadex.commons.future.IFuture;

/**
 *  Internal service interface for managing services in service container.
 */
public interface IInternalService extends IService
{
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture<Void>	startService();
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture<Void>	shutdownService();
	
	/**
	 *  Set the service identifier.
	 */
	public void createServiceIdentifier(String name, Class implclazz, IResourceIdentifier rid, Class<?> type);
	
}
