package jadex.bridge.service;


import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Internal interface for a service container. Allows
 *  adding and removing services. 
 */

// todo: remove IServiceProvider interface
// move search method from IServiceProvider to IExternalAccess 
// use external access for searching
// make this interface the public interface for internal usage of provider 
public interface IServiceContainer extends IServiceProvider
{	
	//-------- internal admin methods --------
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	// todo: remove, only call from platform
	public IFuture<Void> start();
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	// todo: remove, only call from platform
	public IFuture<Void> shutdown();
	
	/**
	 *  Add a service to the container.
	 *  The service is started, if the container is already running.
	 *  @param service The service.
	 *  @param info The provided service info.
	 *  @param componentfetcher	 Helper to fetch corrent object for component injection based on field type.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture<Void>	addService(IInternalService service, ProvidedServiceInfo info, IResultCommand<Object, Class<?>> componentfetcher);

	/**
	 *  Removes a service from the container (shutdowns also the service if the container is running).
	 *  @param service The service identifier.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture<Void>	removeService(IServiceIdentifier sid);
		
	/**
	 *  Get provided (declared) service.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public IService getProvidedService(String name);
	
	/**
	 *  Get provided (declared) service.
	 *  @param clazz The interface.
	 *  @return The service.
	 */
	public IService getProvidedService(Class<?> clazz);

	/**
	 *  Get provided (declared) service.
	 *  @param clazz The interface.
	 *  @return The service.
	 */
	public IService[] getProvidedServices(Class<?> clazz);
	
	/**
	 *  Get the provided service
	 */
	public Object getProvidedServiceRawImpl(Class<?> clazz);
	
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
	
	/**
	 *  Set the required services.
	 *  @param required services The required services to set.
	 */
	public void setRequiredServiceInfos(RequiredServiceInfo[] requiredservices);
	
	/**
	 *  Add required services for a given prefix.
	 *  @param prefix The name prefix to use.
	 *  @param required services The required services to set.
	 */
	public void addRequiredServiceInfos(RequiredServiceInfo[] requiredservices);
	
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
	public <T> IIntermediateFuture<T> getRequiredServices(String name);
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind);
	
	/**
	 *  Get a required services.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> IIntermediateFuture<T> getRequiredServices(String name, boolean rebind);
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind, IFilter<T> filter);
	
	/**
	 *  Get a required services.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> IIntermediateFuture<T> getRequiredServices(String name, boolean rebind, IFilter<T> filter);
	
	/**
	 *  Add a service interceptor.
	 *  @param interceptor The interceptor.
	 *  @param service The service.
	 *  @param pos The position (0=first).
	 */
	public void addInterceptor(IServiceInvocationInterceptor interceptor, Object service, int pos);

	/**
	 *  Remove a service interceptor.
	 *  @param interceptor The interceptor.
	 *  @param service The service.
	 */
	public void removeInterceptor(IServiceInvocationInterceptor interceptor, Object service);
	
	/**
	 *  Get the interceptors of a service.
	 *  @param service The service.
	 *  @return The interceptors.
	 */
	public IServiceInvocationInterceptor[] getInterceptors(Object service);
	
	/**
	 *  Get one service of a type from a specific component.
	 *  @param type The class.
	 *  @param cid The component identifier of the target component.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> getService(Class<T> type, IComponentIdentifier cid);
	
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
	
	// todo: remove! is just convenience for search(type, upwards)
	/**
	 *  Get one service of a type and only search upwards (parents).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchServiceUpwards(Class<T> type);

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
}
