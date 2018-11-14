package jadex.bridge.service.component;

import java.util.Collection;

import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

/**
 *  Component feature for required services.
 */
public interface IRequiredServicesFeature 
{
	//-------- accessors for declared services --------
	
	/**
	 *  Resolve a declared required service of a given name.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getService(String name);
	
	/**
	 *  Resolve a required service of a given type.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param type The service type.
	 *  @return The service.
	 */
	public <T> IFuture<T> getService(Class<T> type);
	
	/**
	 *  Resolve a required services of a given name.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param name The services name.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getServices(String name);
	
	/**
	 *  Resolve a required services of a given type.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param type The services type.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getServices(Class<T> type);
	
	/**
	 *  Resolve a declared required service of a given name.
	 *  Synchronous method only for locally available services.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> T getLocalService(String name);
	
	/**
	 *  Resolve a required service of a given type.
	 *  Synchronous method only for locally available services.
	 *  @param type The service type.
	 *  @return The service.
	 */
	public <T> T getLocalService(Class<T> type);
	
	/**
	 *  Resolve a required services of a given name.
	 *  Synchronous method only for locally available services.
	 *  @param name The services name.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> Collection<T> getLocalServices(String name);
	
	/**
	 *  Resolve a required services of a given type.
	 *  Synchronous method only for locally available services.
	 *  @param type The services type.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> Collection<T> getLocalServices(Class<T> type);

	//-------- methods for searching --------
	
	/**
	 *  Search for matching services and provide first result.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> IFuture<T> searchService(ServiceQuery<T> query);
	
	/**
	 *  Search for matching services and provide first result.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> T searchLocalService(ServiceQuery<T> query);
	
	/**
	 *  Search for all matching services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T>  ITerminableIntermediateFuture<T> searchServices(ServiceQuery<T> query);
	
	/**
	 *  Search for all matching services.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T> Collection<T> searchLocalServices(ServiceQuery<T> query);
	
	//-------- query methods --------

	/**
	 *  Add a service query.
	 *  Continuously searches for matching services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query);
	
//	//-------- old --------
//	
//	/**
//	 *  Get the required service infos.
//	 */
//	public RequiredServiceInfo[] getRequiredServiceInfos();
//	
//	/**
//	 *  Get the required service info.
//	 *  @param name The name.
//	 *  @return The required service info.
//	 */
//	public RequiredServiceInfo getRequiredServiceInfo(String name);
//	
//	/**
//	 *  Get a required service.
//	 *  @param name The required service name.
//	 *  @param rebind If false caches results.
//	 *  @return The service.
//	 */
//	public <T> IFuture<T> getRequiredService(String name, boolean rebind);
//	
//	/**
//	 *  Get a required services.
//	 *  @param name The required service name.
//	 *  @param rebind If false caches results.
//	 *  @return Each service as an intermediate result and a collection of services as final result.
//	 */
//	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind);
//	
//	/**
//	 *  Get a required service.
//	 *  @param name The required service name.
//	 *  @param rebind If false caches results.
//	 *  @param tags The async filter.
//	 *  @return The service.
//	 */
//	public <T> IFuture<T> getRequiredService(String name, boolean rebind, IAsyncFilter<T> filter);
//	
//	/**
//	 *  Get a required services.
//	 *  @param name The required service name.
//	 *  @param rebind If false caches results.
//	 *  @param tags The async filter.
//	 *  @return Each service as an intermediate result and a collection of services as final result.
//	 */
//	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind, IAsyncFilter<T> filter);
//	
//	/**
//	 *  Get a required service using tags.
//	 *  @param name The required service name.
//	 *  @param rebind If false caches results.
//	 *  @param tags The service tags.
//	 *  @return The service.
//	 */
//	public <T> IFuture<T> getRequiredService(String name, boolean rebind, String... tags);
//	
//	/**
//	 *  Get a required services using tags.
//	 *  @param name The required service name.
//	 *  @param rebind If false caches results.
//	 *  @param tags The service tags.
//	 *  @return Each service as an intermediate result and a collection of services as final result.
//	 */
//	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind, String... tags);
//	
//	/**
//	 *  Get the result of the last search.
//	 *  @param name The required service name.
//	 *  @return The last result.
//	 */
//	public <T> T getLastRequiredService(String name);
//	
//	/**
//	 *  Get the result of the last search.
//	 *  @param name The required services name.
//	 *  @return The last result.
//	 */
//	public <T> Collection<T> getLastRequiredServices(String name);
}