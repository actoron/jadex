package jadex.bridge.service.component;

import java.lang.reflect.Method;

import jadex.bridge.sensor.service.IMethodInvocationListener;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.commons.MethodInfo;
import jadex.commons.future.IFuture;

/**
 *  Component feature for provided services.
 */
public interface IProvidedServicesFeature
{
	/**
	 *  Add a service to the container.
	 *  The service is started, if the container is already running.
	 *  @param service The service.
	 *  @param info The provided service info.
	 *  @return A future that is done when the service has completed starting.  
	 */
//	public void addService(IInternalService service, ProvidedServiceInfo info);
	public IFuture<Void> addService(String name, Class<?> type, Object service);

	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param type The public service interface.
	 *  @param service The service.
	 *  @param type The proxy type (@see{BasicServiceInvocationHandler}).
	 */
	public IFuture<Void> addService(String name, Class<?> type, Object service, String proxytype);
	
	/**
	 *  Add a service to the platform. 
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param type The public service interface.
	 *  @param service The service.
	 *  @param scope	The service scope.
	 */
	public IFuture<Void> addService(String name, Class<?> type, Object service, PublishInfo pi, String scope);
	
	/**
	 *  Sets the tags of a service.
	 *  
	 *  @param sid The Service identifier.
	 *  @param tags The tags.
	 *  @return New service identifier.
	 */
	public void setTags(IServiceIdentifier sid, String... tags);
	
	/**
	 *  Removes a service from the container (shutdowns also the service if the container is running).
	 *  @param service The service identifier.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture<Void> removeService(IServiceIdentifier sid);
		
	/**
	 *  Get provided (declared) service.
	 *  
	 *  @param name The service name.
	 *  @return The service.
	 */
	public IService getProvidedService(String name);
	
	/**
	 *  Get provided (declared) service.
	 *  
	 *  @param clazz The interface.
	 *  @return The service.
	 */
	public <T> T getProvidedService(Class<T> clazz);

	/**
	 *  Get the provided service implementation object by id.
	 *  
	 *  @param name The service identifier.
	 *  @return The service.
	 */
	public <T> T getProvidedService(IServiceIdentifier sid);
	
	/**
	 *  Get provided (declared) service.
	 *  
	 *  @param clazz The interface (null for all services).
	 *  @return The service.
	 */
	public <T> T[] getProvidedServices(Class<T> clazz);
	
	/**
	 *  Get the provided service implementation object by class.
	 *  
	 *  @param clazz The service clazz.
	 *  @return The service.
	 */
	public <T> T getProvidedServiceRawImpl(Class<T> clazz);
	
	/**
	 *  Get the provided service implementation object by name.
	 *  
	 *  @param name The service name.
	 *  @return The service.
	 */
	public Object getProvidedServiceRawImpl(String name);
	
	/**
	 *  Get the provided service implementation object by name.
	 *  
	 *  @param name The service identifier.
	 *  @return The service.
	 */
	public Object getProvidedServiceRawImpl(IServiceIdentifier sid);
	
	/**
	 *  Add a method invocation handler.
	 *  @param sid The service identifier.
	 *  @param mi The method info.
	 *  @param listener The method listener.
	 */
	public void addMethodInvocationListener(IServiceIdentifier sid, MethodInfo mi, IMethodInvocationListener listener);
	
	/**
	 *  Remove a method invocation handler.
	 *  @param sid The service identifier.
	 *  @param mi The method info.
	 *  @param listener The method listener.
	 */
	public void removeMethodInvocationListener(IServiceIdentifier sid, MethodInfo mi, IMethodInvocationListener listener);

	/**
	 *  Test if service and method has listeners.
	 *  @param sid The service identifier.
	 *  @param mi The method info.
	 */
	public boolean hasMethodListeners(IServiceIdentifier sid, MethodInfo mi);
	
	/**
	 *  Notify listeners that a service method has been called.
	 */
	public void notifyMethodListeners(IServiceIdentifier sid, boolean start, Object proxy, final Method method, final Object[] args, Object callid, ServiceInvocationContext context);
	
	/**
	 *  Add a service interceptor.
	 *  @param interceptor The interceptor.
	 *  @param service The service.
	 *  @param pos The position (0=first, -1=last-1, i.e. one before method invocation).
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
}
