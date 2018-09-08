package jadex.bridge.service.component;

import java.util.Collection;

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
	 *  @return The service or ServiceNotFoundException
	 */
	public <T> T getLocalService(Class<T> type);
	
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
	
	//-------- methods for searching --------
	
	/**
	 *  Search for matching services and provide first result.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return The corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> T searchLocalService(ServiceQuery<T> query);
	
	/**
	 *  Search for all matching services.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return A collection of services.
	 */
	public <T> Collection<T> searchLocalServices(ServiceQuery<T> query);
}