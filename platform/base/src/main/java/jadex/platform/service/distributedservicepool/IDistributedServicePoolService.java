package jadex.platform.service.distributedservicepool;

import jadex.bridge.service.IService;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IFuture;

/**
 *  Interface for the distributed pool.
 */
@Service
public interface IDistributedServicePoolService
{
	/**
	 *  Add a new service type and a strategy.
	 *  @param query The service query to find workers.
	 *  @param pi The publish info.
	 *  @param scope The publication scope of the pool service.
	 */
	public IFuture<Void> addServiceType(ServiceQuery<IService> query, PublishInfo pi, ServiceScope scope);

	/**
	 *  Remove a service type.
	 *  @param servicetype The service type.
	 */
	public IFuture<Void> removeServiceType(Class<?> servicetype);
}
