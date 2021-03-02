package jadex.bridge.service.types.servicepool;

import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.IPoolStrategy;
import jadex.commons.future.IFuture;

/**
 *  Service pool service that allows for adding and
 *  removing service types and handling strategies
 *  to the pool.
 */
@Service
@Security(roles=Security.UNRESTRICTED)
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
	public IFuture<Void> addServiceType(Class<?> servicetype, IPoolStrategy strategy, String componentmodel, CreationInfo info, PublishInfo pi, ServiceScope scope);

	/**
	 *  Remove a service type.
	 *  @param servicetype The service type.
	 */
	public IFuture<Void> removeServiceType(Class<?> servicetype);
	
	// todo: make available via NF props
	
	/**
	 *  Get the maximum capacity.
	 *  @param servicetype The service type.
	 *  @return The maximum capacity.
	 */
	public IFuture<Integer> getMaxCapacity(Class<?> servicetype);
	
	/**
	 *  Get the free capacity.
	 *  @param servicetype The service type.
	 *  @return The free capacity.
	 */
	public IFuture<Integer> getFreeCapacity(Class<?> servicetype);

}
