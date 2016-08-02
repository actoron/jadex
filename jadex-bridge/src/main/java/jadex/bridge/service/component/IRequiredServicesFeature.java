package jadex.bridge.service.component;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SynchronizedServiceRegistry;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

/**
 *  Component feature for required services.
 */
public interface IRequiredServicesFeature 
{
	/**
	 *  Get the required service infos.
	 */
	public RequiredServiceInfo[] getRequiredServiceInfos();
	
	/**
	 *  Get the required service info.
	 *  @param name The name.
	 *  @return The required service info.
	 */
	public RequiredServiceInfo getRequiredServiceInfo(String name);
	
//	/**
//	 *  Set the required services.
//	 *  @param required services The required services to set.
//	 */
//	public void setRequiredServiceInfos(RequiredServiceInfo[] requiredservices);
//	
//	/**
//	 *  Add required services for a given prefix.
//	 *  @param prefix The name prefix to use.
//	 *  @param required services The required services to set.
//	 */
//	public void addRequiredServiceInfos(RequiredServiceInfo[] requiredservices);
	
//	/**
//	 *  Get the required service property provider for a service.
//	 */
//	public INFMixedPropertyProvider getRequiredServicePropertyProvider(IServiceIdentifier sid);
//	
//	/**
//	 *  Has the service a property provider.
//	 */
//	public boolean hasRequiredServicePropertyProvider(IServiceIdentifier sid);
	
	/**
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name);
	
	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name);
	
	/**
	 *  Get a required service.
	 *  @param name The required service name.
	 *  @param rebind If false caches results.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind);
	
	/**
	 *  Get a required services.
	 *  @param name The required service name.
	 *  @param rebind If false caches results.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind);
	
//	/**
//	 *  Get a required service of a given name.
//	 *  @param name The service name.
//	 *  @return The service.
//	 */
//	public <T> T getLocalRequiredService(String name);
//	
//	/**
//	 *  Get a required services of a given name.
//	 *  @param name The services name.
//	 *  @return Each service as an intermediate result and a collection of services as final result.
//	 */
//	public <T> T getLocalRequiredServices(String name);
//	
//	/**
//	 *  Get a required service.
//	 *  @return The service.
//	 */
//	public <T> T getLocalRequiredService(String name, boolean rebind);
//	
//	/**
//	 *  Get a required services.
//	 *  @return Each service as an intermediate result and a collection of services as final result.
//	 */
//	public <T> T getLocalRequiredServices(String name, boolean rebind);
	
	/**
	 *  Get a required service.
	 *  @param name The required service name.
	 *  @param rebind If false caches results.
	 *  @param tags The async filter.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind, IAsyncFilter<T> filter);
	
	/**
	 *  Get a required services.
	 *  @param name The required service name.
	 *  @param rebind If false caches results.
	 *  @param tags The async filter.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind, IAsyncFilter<T> filter);
	
	/**
	 *  Get a required service using tags.
	 *  @param name The required service name.
	 *  @param rebind If false caches results.
	 *  @param tags The service tags.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind, String... tags);
	
	/**
	 *  Get a required services using tags.
	 *  @param name The required service name.
	 *  @param rebind If false caches results.
	 *  @param tags The service tags.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind, String... tags);
	
	/**
	 *  Get the result of the last search.
	 *  @param name The required service name.
	 *  @return The last result.
	 */
	public <T> T getLastRequiredService(String name);
	
	/**
	 *  Get the result of the last search.
	 *  @param name The required services name.
	 *  @return The last result.
	 */
	public <T> Collection<T> getLastRequiredServices(String name);
	
	// extra methods for searching
	
	/**
	 *  Get one service of a type from a specific component.
	 *  @param type The class.
	 *  @param cid The component identifier of the target component.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchService(Class<T> type, IComponentIdentifier cid);
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchService(Class<T> type);
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchService(Class<T> type, String scope);
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> IIntermediateFuture<T> searchServices(Class<T> type);
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> IIntermediateFuture<T> searchServices(Class<T> type, String scope);
	
	/**
	 *  Add a service query.
	 *  @param type The service type.
	 *  @param scope The scope.
	 *  @param filter The filter.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(Class<T> type, String scope, IAsyncFilter<T> filter);
}


