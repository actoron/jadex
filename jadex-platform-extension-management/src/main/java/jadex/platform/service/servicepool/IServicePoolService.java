package jadex.platform.service.servicepool;

import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.IPoolStrategy;
import jadex.commons.future.IFuture;

/**
 *  Service pool service that allows for adding and
 *  removing service types and handling strategies
 *  to the pool.
 */
public interface IServicePoolService
{
//	/** The pool broadcast flag. */
//	public static final String POOL_BROADCAST = "pool_broadcast";
	
	/**
	 *  Add a new service type.
	 *  @param servicetype The service type.
	 *  @param componentmodel The component model.
	 */
	public IFuture<Void> addServiceType(Class<?> servicetype, String componentmodel);
	
	/**
	 *  Add a new service type and a strategy.
	 *  @param servicetype The service type.
	 *  @param componentmodel The component model.
	 *  @param info The creation info.
	 */
	public IFuture<Void> addServiceType(Class<?> servicetype, String componentmodel, CreationInfo info);
	
	/**
	 *  Add a new service type and a strategy.
	 *  @param servicetype The service type.
	 *  @param strategy The service pool strategy.
	 *  @param componentmodel The component model.
	 */
	public IFuture<Void> addServiceType(Class<?> servicetype, IPoolStrategy strategy, String componentmodel);
//	
//	/**
//	 *  Add a new service type and a strategy.
//	 *  @param servicetype The service type.
//	 *  @param strategy The service pool strategy.
//	 *  @param componentmodel The component model.
//	 */
//	public IFuture<Void> addServiceType(Class<?> servicetype, IPoolStrategy strategy, String componentmodel, CreationInfo info, PublishInfo pi);
	
	/**
	 *  Add a new service type and a strategy.
	 *  @param servicetype The service type.
	 *  @param strategy The service pool strategy.
	 *  @param componentmodel The component model.
	 *  @param info The worker creation info.
	 *  @param pi The worker publish info. 
	 *  @param scope The service publication scope.
	 */
	public IFuture<Void> addServiceType(Class<?> servicetype, IPoolStrategy strategy, String componentmodel, CreationInfo info, PublishInfo pi, String scope);

	
	/**
	 *  Remove a service type.
	 *  @param servicetype The service type.
	 */
	public IFuture<Void> removeServiceType(Class<?> servicetype);
}
