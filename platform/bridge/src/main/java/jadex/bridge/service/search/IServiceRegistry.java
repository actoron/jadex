package jadex.bridge.service.search;

import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Interface for a service registry.
 *  
 *  todo: further cleanup the interface
 */
public interface IServiceRegistry
{
	/**
	 *  Search for a service.
	 */
	public IServiceIdentifier searchService(ServiceQuery<?> query);
	
	/**
	 *  Search for services.
	 */
	public Set<IServiceIdentifier> searchServices(ServiceQuery<?> query);
	
	/**
	 *  Add a service to the registry.
	 *  @param service The service.
	 */
	// write
	public void addService(IServiceIdentifier service);
	
	/**
	 *  Add a local service to the registry.
	 *  @param service The local service.
	 */
	// write
	public void addLocalService(IService service);
	
	/**
	 *  Remove a service from the registry.
	 *  @param service The service.
	 */
	// write
	public void removeService(IServiceIdentifier service);
	
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
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	// write
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query);
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	// write
	public void removeQueries(IComponentIdentifier owner);
	
	/**
	 *  Remove all service queries of a specific platform from the registry.
	 *  @param platform The platform from which the query owner comes.
	 */
	// write
	public void removeQueriesOfPlatform(IComponentIdentifier platform);
	
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
	public void removeExcludedComponent(IComponentIdentifier cid);
	
	/**
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	// read
	//public boolean isIncluded(IComponentIdentifier cid, IServiceIdentifier ser);
	
	/**
	 *  Get all services.
	 *  @return All services (copy).
	 */
	public Set<IServiceIdentifier> getAllServices();
	
	/**
	 *  Get all queries.
	 *  @return All queries (copy).
	 */
	public Set<ServiceQueryInfo<?>> getAllQueries();
}
