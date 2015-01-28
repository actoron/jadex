package jadex.platform.service.globalservicepool;

import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.IFuture;

/**
 *  Service pool service that allows for adding and
 *  removing service types and handling strategies
 *  to the pool.
 */
public interface IGlobalServicePoolService
{
	/**
	 *  Add a new service type and a strategy.
	 *  @param servicetype The service type.
	 *  @param componentmodel The component model.
	 */
	public IFuture<Void> addServiceType(Class<?> servicetype, String componentmodel, CreationInfo info, IGlobalPoolStrategy strategy);
	
	/**
	 *  Remove a service type.
	 *  @param servicetype The service type.
	 */
	public IFuture<Void> removeServiceType(Class<?> servicetype);
}