package jadex.bridge.service.component;

import java.util.Collection;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

/**
 *  Component feature for required services.
 */
public interface IRequiredServicesFeature extends IExternalRequiredServicesFeature
{
	//-------- accessors for declared services --------
	
	/**
	 *  Resolve a declared required service of a given name.
	 *  Synchronous method only for locally available services.
	 *  @param name The service name.
	 *  @return The service or ServiceNotFoundException
	 */
	public <T> T getLocalService(String name);
	
	/**
	 *  Resolve a declared required service of a given type.
	 *  Synchronous method only for locally available services.
	 *  @param type The service type.
	 *  @return The service or ServiceNotFoundException.
	 */
	public <T> T getLocalService(Class<T> type);
	
	/**
	 *  Resolve a declared required service of a given type.
	 *  Synchronous method only for locally available services.
	 *  @param type The service type.
	 *  @return The service or null.
	 */
	public <T> T getLocalService0(Class<T> type);
	
	/**
	 *  Resolve a declared required services of a given name.
	 *  Synchronous method only for locally available services.
	 *  @param name The services name.
	 *  @return A collection of services.
	 */
	public <T> Collection<T> getLocalServices(String name);
	
	/**
	 *  Resolve a declared required services of a given type.
	 *  Synchronous method only for locally available services.
	 *  @param type The services type.
	 *  @return A collection of services.
	 */
	public <T> Collection<T> getLocalServices(Class<T> type);
	
	/**
	 *  Lookup matching services and provide first result.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return The corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> T getLocalService(ServiceQuery<T> query);
	
	/**
	 *  Lookup all matching services.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return A collection of services.
	 */
	public <T> Collection<T> getLocalServices(ServiceQuery<T> query);
	
	//-------- methods for searching --------
	
	/**
	 *  Performs a sustained search for a service. Attempts to find a service
	 *  for a maximum duration until timeout occurs.
	 *  
	 *  @param query The search query.
	 *  @param timeout Maximum time period to search, 0 for default timeout, -1 for no wait.
	 *  @return Service matching the query, exception if service is not found.
	 */
	public <T> IFuture<T> searchService(ServiceQuery<T> query, long timeout);
	
	
	// Would be nice having methods below in external variant but requires special required proxy handling
	
	/**
	 *  Add a query for a declared required service.
	 *  Continuously searches for matching services.
	 *  @param name The name of the required service declaration.
	 *  @return Future providing the corresponding services as intermediate results.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(String name);
	
	/**
	 *  Add a query for a declared required service.
	 *  Continuously searches for matching services.
	 *  @param name The name of the required service declaration.
	 *  @return Future providing the corresponding services as intermediate results.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query, long timeout);
	
	/**
	 *  Add a query for a declared required service.
	 *  Continuously searches for matching services.
	 *  @param type The type of the required service declaration.
	 *  @return Future providing the corresponding services as intermediate results.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(Class<T> type);
	
	/**
	 *  Resolve a declared required service of a given name.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param name The service name.
	 *  @return Future with the service or ServiceNotFoundException
	 */
	public <T> IFuture<T> getService(String name);
	
	/**
	 *  Resolve a declared required service of a given type.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param type The service type.
	 *  @return Future with the service or ServiceNotFoundException
	 */
	public <T> IFuture<T> getService(Class<T> type);
	
	/**
	 *  Resolve a declared required services of a given name.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param name The services name.
	 *  @return Each service as an intermediate result or a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getServices(String name);
	
	/**
	 *  Resolve a declared required services of a given type.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param type The services type.
	 *  @return Each service as an intermediate result or a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getServices(Class<T> type);
	
	/**
	 *  Create the user-facing object from the received search or query result.
	 *  Result may be service object, service identifier (local or remote), or event.
	 *  User object is either event or service (with or without required proxy).
	 */
	public IService getServiceProxy(IServiceIdentifier sid, RequiredServiceInfo info);
	
	/**
	 *  Get a service query for a required service info (as defined in the agent under that name).
	 *  @param name The name.
	 *  @return The service query.
	 */
	public ServiceQuery<?> getServiceQuery(String name);
	
}