package jadex.bridge.service;

import java.lang.reflect.Proxy;
import java.util.HashMap;

import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;

/**
 *  Internal interface for a service container. Allows
 *  adding and removing services. 
 */
public interface IServiceContainer extends IServiceProvider
{	
	//-------- internal admin methods --------
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	// todo: remove, only call from platform
	public IFuture start();
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	// todo: remove, only call from platform
	public IFuture shutdown();
	
	/**
	 *  Add a service to the container.
	 *  The service is started, if the container is already running.
	 *  @param service The service.
	 */
	public IFuture	addService(IInternalService service);

	/**
	 *  Removes a service from the container (shutdowns also the service if the container is running).
	 *  @param service The service identifier.
	 */
	public IFuture	removeService(IServiceIdentifier sid);
	
	//-------- internal user methods --------
	
	/**
	 *  Get provided (declared) service.
	 *  @param class The interface.
	 *  @return The service.
	 */
	public IService getProvidedService(Class clazz);
	
	/**
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name);
	
	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return The service.
	 */
	public IIntermediateFuture getRequiredServices(String name);
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name, boolean rebind);
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public IIntermediateFuture getRequiredServices(String name, boolean rebind);
	
	/**
	 *  Add a service interceptor.
	 *  @param interceptor The interceptor.
	 *  @param service The service.
	 *  @param pos The position (0=first).
	 */
	public void addInterceptor(IServiceInvocationInterceptor interceptor, IService service, int pos);

	/**
	 *  Remove a service interceptor.
	 *  @param interceptor The interceptor.
	 *  @param service The service.
	 */
	public void removeInterceptor(IServiceInvocationInterceptor interceptor, IService service);
	
	
//	/**
//	 *  Add a provided service interceptor (at first position in the chain).
//	 *  @param clazz The interface of the provided service.
//	 *  @param pos The position in the chain (0=first).
//	 *  @param interceptor The interceptor.
//	 *  @return Null using future when done.
//	 */
//	public IFuture addProvidedServiceInterceptor(Class clazz, IServiceInvocationInterceptor interceptor, int pos);
//	
//	/**
//	 *  Remove a provided service interceptor.
//	 *  @param clazz The interface of the provided service.
//	 *  @param interceptor The interceptor.
//	 *  @return Null using future when done.
//	 */
//	public IFuture removeProvidedServiceInterceptor(Class clazz, IServiceInvocationInterceptor interceptor);
//
//	/**
//	 *  Add a required service interceptor (at first position in the chain).
//	 *  @param name The name of the required service.
//	 *  @param pos The position in the chain (0=first).
//	 *  @param interceptor The interceptor.
//	 *  @return Null using future when done.
//	 */
//	public IFuture addRequiredServiceInterceptor(String name, IServiceInvocationInterceptor interceptor, int pos);
//	
//	/**
//	 *  Remove a required service interceptor.
//	 *  @param clazz The interface of the provided service.
//	 *  @param interceptor The interceptor.
//	 *  @return Null using future when done.
//	 */
//	public IFuture removeRequiredServiceInterceptor(String name, IServiceInvocationInterceptor interceptor);
}
