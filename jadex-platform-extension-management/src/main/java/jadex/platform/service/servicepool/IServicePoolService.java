package jadex.platform.service.servicepool;

import jadex.commons.future.IFuture;

/**
 *  Service pool service that allows for adding and
 *  removing service types and handling strategies
 *  to the pool.
 */
public interface IServicePoolService
{
	/**
	 *  Add a new service type and a strategy.
	 *  @param servicetype The service type.
	 *  @param strategy The service pool strategy.
	 */
	public IFuture<Void> addServiceType(Class<?> servicetype, ServicePoolStrategy strategy);
	
	/**
	 *  Remove a service type.
	 *  @param servicetype The service type.
	 */
	public IFuture<Void> removeServiceType(Class<?> servicetype);
}
