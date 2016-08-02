package jadex.bridge.service.search;

import java.util.Collection;
import java.util.Iterator;

import jadex.base.PlatformConfiguration;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.commons.IAsyncFilter;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

/**
 *  Synchronized version of the multi service registry.
 */
public class SynchronizedMultiServiceRegistry extends MultiServiceRegistry
{
	//-------- methods --------
	
	/**
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	public synchronized IFuture<Void> addService(ClassInfo key, IService service)
	{
		return super.addService(key, service);
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	public synchronized void removeService(ClassInfo key, IService service)
	{
		super.removeService(key, service);
	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public synchronized <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		return super.addQuery(query);
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	public synchronized <T> void removeQuery(ServiceQuery<T> query)
	{
		super.removeQuery(query);
	}
	
	/**
	 *  Check the persistent queries for a new service.
	 *  @param ser The service.
	 */
	protected synchronized IFuture<Void> checkQueries(IService ser)
	{
		return super.checkQueries(ser);
	}
	
	/**
	 *  Check the persistent queries against a new service.
	 *  @param it The queries.
	 *  @param service the service.
	 */
	protected synchronized IFuture<Void> checkQueriesLoop(final Iterator<ServiceQueryInfo<?>> it, final IService service)
	{
		return super.checkQueriesLoop(it, service);
	}
	
	/**
	 *  Check a persistent query with one service.
	 *  @param queryinfo The query.
	 *  @param service The service.
	 *  @return True, if services matches to query.
	 */
	protected synchronized IFuture<Boolean> checkQuery(final ServiceQueryInfo<?> queryinfo, final IService service)
	{
		return super.checkQuery(queryinfo, service);
	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	public synchronized void removeQueries(IComponentIdentifier owner)
	{
		super.removeQueries(owner);
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope)
	{
		return super.searchService(type, cid, scope);
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope)
	{
		return super.searchServices(type, cid, scope);
	}
	
	/**
	 *  Search for service.
	 */
	public synchronized <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		return super.searchService(type, cid, scope, filter);
	}
	
	/**
	 *  Search for service.
	 */
	public synchronized <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		return super.searchServices(type, cid, scope, filter);
	}
	
	/**
	 *  Search for service.
	 */
	public synchronized <T> IFuture<T> searchService(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		return super.searchService(type, cid, scope, filter);
	}
	
	/**
	 *  Perform the search in a loop.
	 *  @param filter The filter.
	 *  @param it The iterator.
	 *  @param cid The component id.
	 *  @param scope The scope.
	 */
	protected synchronized <T> IFuture<T> searchLoopService(final IAsyncFilter<T> filter, final Iterator<T> it, final IComponentIdentifier cid, final String scope)
	{
		return super.searchLoopService(filter, it, cid, scope);
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> ISubscriptionIntermediateFuture<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		return super.searchServices(type, cid, scope, filter);
	}
	
	/**
	 *  Perform the search in a loop.
	 */
	protected synchronized <T> ISubscriptionIntermediateFuture<T> searchLoopServices(final IAsyncFilter<T> filter, final Iterator<T> it, final IComponentIdentifier cid, final String scope)
	{
		return super.searchLoopServices(filter, it, cid, scope);
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> IFuture<T> searchGlobalService(final Class<T> type, IComponentIdentifier cid, final IAsyncFilter<T> filter)
	{
		return super.searchGlobalService(type, cid, filter);
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> ITerminableIntermediateFuture<T> searchGlobalServices(Class<T> type, IComponentIdentifier cid, IAsyncFilter<T> filter)
	{
		return super.searchGlobalServices(type, cid, filter);
	}
	
	/**
	 *  Get the registry from a component.
	 */
	public static SynchronizedMultiServiceRegistry getRegistry(IComponentIdentifier platform)
	{
		return (SynchronizedMultiServiceRegistry)PlatformConfiguration.getPlatformValue(platform, PlatformConfiguration.DATA_SERVICEREGISTRY);
	}
	
	/**
	 *  Get the registry from a component.
	 */
	public static SynchronizedMultiServiceRegistry getRegistry(IInternalAccess ia)
	{
		return getRegistry(ia.getComponentIdentifier());
	}
}
