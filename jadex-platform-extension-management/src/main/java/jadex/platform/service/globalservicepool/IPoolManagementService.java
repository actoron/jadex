package jadex.platform.service.globalservicepool;

import jadex.bridge.ClassInfo;
import jadex.bridge.service.IService;
import jadex.commons.future.IIntermediateFuture;

/**
 * 
 */
public interface IPoolManagementService 
{
	/**
	 *  Get a set of services managed by the pool.
	 *  @param type The service type.
	 *  @return A number of services from the pool.
	 */
	public IIntermediateFuture<IService> getPoolServices(ClassInfo type);

}
