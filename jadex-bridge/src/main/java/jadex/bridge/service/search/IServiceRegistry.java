package jadex.bridge.service.search;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.registry.IRegistryListener;
import jadex.commons.IAsyncFilter;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Interface for a service registry.
 *  
 *  todo: further cleanup the interface
 */
public interface IServiceRegistry
{
	/**
	 *  Get services per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return First matching service or null.
	 */
	public Iterator<IService> getServices(ClassInfo type);
	
	/**
	 *  todo: WARNING: dangerous method that exposes the internal data structure
	 *  Get the service map.
	 *  @return The full service map.
	 */
	// read
	public Map<ClassInfo, Set<IService>> getServiceMap();

	/**
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	// write
	public IFuture<Void> addService(ClassInfo key, IService service);
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	// write
	public void removeService(ClassInfo key, IService service);
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	// write
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query);
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	// write
	public <T> void removeQuery(ServiceQuery<T> query);
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	// write
	public void removeQueries(IComponentIdentifier owner);
	
	/**
	 *  Get queries per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return The queries.
	 */
	// read
	public <T> Set<ServiceQueryInfo<T>> getQueries(ClassInfo type);
	
	/**
	 *  Search for services.
	 */
	// read
	public <T> T searchService(ClassInfo type, IComponentIdentifier cid, String scope);
	
	/**
	 *  Search for services.
	 */
	// read
	public <T> Collection<T> searchServices(ClassInfo type, IComponentIdentifier cid, String scope);
	
	/**
	 *  Search for service.
	 */
	public <T> T searchService(ClassInfo type, IComponentIdentifier cid, String scope, IFilter<T> filter);
	
	/**
	 *  Search for service.
	 */
	public <T> Collection<T> searchServices(ClassInfo type, IComponentIdentifier cid, String scope, IFilter<T> filter);
	
	/**
	 *  Search for service.
	 */
	public <T> IFuture<T> searchService(ClassInfo type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter);
	
	/**
	 *  Search for services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> searchServices(ClassInfo type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter);
	
	/**
	 *  Search for services.
	 */
	// read
	public <T> IFuture<T> searchGlobalService(final ClassInfo type, IComponentIdentifier cid, final IAsyncFilter<T> filter);
	
	/**
	 *  Search for services.
	 */
	// read
	public <T> ISubscriptionIntermediateFuture<T> searchGlobalServices(ClassInfo type, IComponentIdentifier cid, IAsyncFilter<T> filter);
	
	
	
	/**
	 *  Add an excluded component. 
	 *  @param The component identifier.
	 */
	// write
	public void addExcludedComponent(IComponentIdentifier cid);
	
	/**
	 *  Remove an excluded component. 
	 *  @param The component identifier.
	 */
	// write
	public IFuture<Void> removeExcludedComponent(IComponentIdentifier cid);
	
	/**
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	// read
	public boolean isIncluded(IComponentIdentifier cid, IService ser);
	
	/**
	 *  Add an event listener.
	 *  @param listener The listener.
	 */
	public void addEventListener(IRegistryListener listener);
	
	/**
	 *  Remove an event listener.
	 *  @param listener The listener.
	 */
	public void removeEventListener(IRegistryListener listener);
	
	/**
	 *  Get a subregistry.
	 *  @param cid The platform id.
	 *  @return The registry.
	 */
	// read
	public IServiceRegistry getSubregistry(IComponentIdentifier cid);
	
	/**
	 *  Remove a subregistry.
	 *  @param cid The platform id.
	 */
	// write
	public void removeSubregistry(IComponentIdentifier cid);

}

///**
// *  Search for services.
// */
//// read
//public <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope);
//
///**
// *  Search for services.
// */
//// read
//public <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope);
//
///**
// *  Search for service.
// */
//public <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter);
//
///**
// *  Search for service.
// */
//public <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter);
//
///**
// *  Search for service.
// */
//public <T> IFuture<T> searchService(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter);
//
///**
// *  Search for services.
// */
//public <T> ISubscriptionIntermediateFuture<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter);
//
///**
// *  Search for services.
// */
//// read
//public <T> IFuture<T> searchGlobalService(final Class<T> type, IComponentIdentifier cid, final IAsyncFilter<T> filter);
//
///**
// *  Search for services.
// */
//// read
//public <T> ISubscriptionIntermediateFuture<T> searchGlobalServices(Class<T> type, IComponentIdentifier cid, IAsyncFilter<T> filter);
//
