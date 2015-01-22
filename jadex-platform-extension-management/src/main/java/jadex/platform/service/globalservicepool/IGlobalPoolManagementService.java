package jadex.platform.service.globalservicepool;

import jadex.bridge.ClassInfo;
import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

import java.util.Map;

/**
 *  The global pool management service is exposed by the global pool
 *  and used by the intelligent proxies.
 */
public interface IGlobalPoolManagementService 
{
	/**
	 *  Get a set of services managed by the pool.
	 *  @param type The service type.
	 *  @return A number of services from the pool.
	 */
	public IIntermediateFuture<IService> getPoolServices(ClassInfo type);

	/**
	 *  Inform about service usage.
	 *  @param The usage infos per service class.
	 */
	public IFuture<Void> sendUsageInfo(Map<ClassInfo, UsageInfo> infos);
}
