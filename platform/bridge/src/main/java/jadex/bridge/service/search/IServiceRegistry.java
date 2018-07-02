package jadex.bridge.service.search;

import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceIdentifier;
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
	 *  Search for a service.
	 */
	public <T> T searchService(ServiceQuery<T> query);
	
	/**
	 *  Search for services.
	 */
	public <T> Set<T> searchServices(ServiceQuery<T> query);
	
	/**
	 *  Add a service to the registry.
	 *  @param service The service.
	 */
	// write
	public void addService(IServiceIdentifier service);
	
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
	 *  Remove all service queries of a specific platform from the registry.
	 *  @param platform The platform from which the query owner comes.
	 */
	// write
	public void removeQueriesFromPlatform(IComponentIdentifier platform);
	
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
	public boolean isIncluded(IComponentIdentifier cid, IServiceIdentifier ser);
	
	/**
	 *  Get all services.
	 *  @return All services (copy).
	 */
	public Set<IServiceIdentifier> getAllServices();
	
	/**
	 *  Get all queries.
	 *  @return All queries (copy).
	 */
	public Set<ServiceQueryInfo<IServiceIdentifier>> getAllQueries();
}
