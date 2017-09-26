package jadex.bridge.service.search;

import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
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
	 *  Add a service to the registry.
	 *  @param service The service.
	 */
	// write
	public IFuture<Void> addService(IService service);
	
	/**
	 *  Remove a service from the registry.
	 *  @param service The service.
	 */
	// write
	public void removeService(IService service);
	
	/**
	 *  Remove services of a platform from the registry.
	 *  @param platform The platform.
	 */
	// write
	public void removeServices(IComponentIdentifier platform);
	
	/**
	 *  Remove services except from a platform from the registry.
	 *  @param platform The platform.
	 */
	// write
	public void removeServicesExcept(IComponentIdentifier platform);
	
	/**
	 *  Search for services.
	 */
	public <T> T searchServiceSync(ServiceQuery<T> query);
	
	/**
	 *  Search for services.
	 */
	public <T> Set<T> searchServicesSync(ServiceQuery<T> query);
	
	/**
	 *  Search for services.
	 */
	public <T> IFuture<T> searchServiceAsync(ServiceQuery<T> query);
	
	/**
	 *  Search for services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> searchServicesAsync(ServiceQuery<T> query);
	
	/**
	 *  Search for services.
	 *  Is deprecated because only used by relay.
	 */
//	// read
	@Deprecated
	public <T> T searchService(ServiceQuery<T> query, boolean excluded);
	
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
	public <T> IFuture<Void> removeQuery(ServiceQuery<T> query);
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	// write
	public IFuture<Void> removeQueries(IComponentIdentifier owner);
	
	/**
	 *  Remove all service queries of a specific platform from the registry.
	 *  @param platform The platform from which the query owner comes.
	 */
	// write
	public IFuture<Void> removeQueriesFromPlatform(IComponentIdentifier platform);
	
	/**
	 *  Get queries per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return The queries.
	 */
	// read
//	public <T> Set<ServiceQueryInfo<T>> getQueries(ClassInfo type);
	
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
	 *  Get the superpeer.
	 *  @param force If trues forces fresh search.
	 *  @return The superpeer.
	 */
	public IFuture<IComponentIdentifier> getSuperpeer(boolean force);
	
	/**
	 *  Get all services.
	 *  @return All services (copy).
	 */
	public Set<IService> getAllServices();
	
	/**
	 *  Get all queries.
	 *  @return All queries (copy).
	 */
	public Set<ServiceQueryInfo<IService>> getAllQueries();
}
