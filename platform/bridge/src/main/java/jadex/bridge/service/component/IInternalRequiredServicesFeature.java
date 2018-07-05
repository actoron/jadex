package jadex.bridge.service.component;

import java.util.Collection;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

/**
 *  Interface for internal service access methods.
 */
public interface IInternalRequiredServicesFeature
{
	/**
	 *  Get the required service info for a name.
	 *  @param name	The required service name.
	 */
	// Hack!!! used by multi invoker?
	public RequiredServiceInfo	getServiceInfo(String name);
	
	/**
	 *  Get a service raw (i.e. w/o required proxy).
	 */
	// Hack???
	public <T>	T	getRawService(Class<T> type);

	/**
	 *  Get a service raw (i.e. w/o required proxy).
	 */
	// Hack???
	public <T>	Collection<T>	getRawServices(Class<T> type);

	/**
	 *  Search for matching services and provide first result.
	 *  @param query	The search query.
	 *  @param info	Used for required service proxy configuration -> null for no proxy.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> IFuture<T> resolveService(ServiceQuery<T> query, RequiredServiceInfo info);
	
	/**
	 *  Search for matching services and provide first result.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @param info	Used for required service proxy configuration -> null for no proxy.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> T resolveLocalService(ServiceQuery<T> query, RequiredServiceInfo info);
	
	/**
	 *  Search for all matching services.
	 *  @param query	The search query.
	 *  @param info	Used for required service proxy configuration -> null for no proxy.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T>  ITerminableIntermediateFuture<T> resolveServices(ServiceQuery<T> query, RequiredServiceInfo info);
	
	/**
	 *  Search for all matching services.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @param info	Used for required service proxy configuration -> null for no proxy.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T> Collection<T> resolveLocalServices(ServiceQuery<T> query, RequiredServiceInfo info);
	
	//-------- all declared services (e.g. JCC component details) --------
	
	/**
	 *  Get the required services.
	 *  @return The required services.
	 */
	public RequiredServiceInfo[] getServiceInfos();
}
