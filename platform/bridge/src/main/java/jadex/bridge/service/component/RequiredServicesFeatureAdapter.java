package jadex.bridge.service.component;

import java.util.Collection;

import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

/**
 *  Adapter for the required services feature.
 */
public class RequiredServicesFeatureAdapter implements IRequiredServicesFeature
{
	//-------- attributes --------
	
	/** The delegate. */
	protected IRequiredServicesFeature delegate;
	
	//-------- constructors --------
	
	/**
	 *  Create a new adapter.
	 */
	public RequiredServicesFeatureAdapter(IRequiredServicesFeature delegate)
	{
		this.delegate = delegate;
	}

	//-------- accessors for declared services --------
	
	/**
	 *  Resolve a declared required service of a given name.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getService(String name)
	{
		return delegate.getService(rename(name));
	}
	
	/**
	 *  Resolve a required service of a given type.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param type The service type.
	 *  @return The service.
	 */
	public <T> IFuture<T> getService(Class<T> type)
	{
		return delegate.getService(type);
	}
	
	/**
	 *  Resolve a required services of a given name.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param name The services name.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getServices(String name)
	{
		return delegate.getServices(rename(name));
	}
	
	/**
	 *  Resolve a required services of a given type.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param type The services type.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getServices(Class<T> type)
	{
		return delegate.getServices(type);
	}
	
	/**
	 *  Resolve a declared required service of a given name.
	 *  Synchronous method only for locally available services.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> T getLocalService(String name)
	{
		return delegate.getLocalService(rename(name));
	}
	
	/**
	 *  Resolve a required service of a given type.
	 *  Synchronous method only for locally available services.
	 *  @param type The service type.
	 *  @return The service.
	 */
	public <T> T getLocalService(Class<T> type)
	{
		return delegate.getLocalService(type);
	}
	
	/**
	 *  Resolve a required services of a given name.
	 *  Synchronous method only for locally available services.
	 *  @param name The services name.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> Collection<T> getLocalServices(String name)
	{
		return delegate.getLocalServices(rename(name));
	}
	
	/**
	 *  Resolve a required services of a given type.
	 *  Synchronous method only for locally available services.
	 *  @param type The services type.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> Collection<T> getLocalServices(Class<T> type)
	{
		return delegate.getLocalServices(type);
	}

	//-------- methods for searching --------
	
	/**
	 *  Search for matching services and provide first result.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> IFuture<T> searchService(ServiceQuery<T> query)
	{
		return delegate.searchService(query);
	}
	
	/**
	 *  Search for matching services and provide first result.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> T searchLocalService(ServiceQuery<T> query)
	{
		return delegate.searchLocalService(query);
	}
	
	/**
	 *  Search for all matching services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T>  ITerminableIntermediateFuture<T> searchServices(ServiceQuery<T> query)
	{
		return delegate.searchServices(query);
	}
	
	/**
	 *  Search for all matching services.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T> Collection<T> searchLocalServices(ServiceQuery<T> query)
	{
		return delegate.searchLocalServices(query);
	}
	
	//-------- query methods --------

	/**
	 *  Add a query for a declared required service.
	 *  Continuously searches for matching services.
	 *  @param name The name of the required service declaration.
	 *  @return Future providing the corresponding services as intermediate results.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(String name)
	{
		return delegate.addQuery(rename(name));
	}

	/**
	 *  Add a query for a declared required service.
	 *  Continuously searches for matching services.
	 *  @param type The type of the required service declaration.
	 *  @return Future providing the corresponding services as intermediate results.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(Class<T> type)
	{
		return delegate.addQuery(type);		
	}
	
	/**
	 *  Add a service query.
	 *  Continuously searches for matching services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query)
	{
		return delegate.addQuery(query);
	}
	
	//-------- template methods --------
	
	/**
	 *  Rename if necessary.
	 */
	public String rename(String name)
	{
		return name;
	}
}
