package jadex.bdi.runtime.impl;

import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.sensor.service.IMethodInvocationListener;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.search.IResultSelector;
import jadex.bridge.service.search.ISearchManager;
import jadex.bridge.service.search.IVisitDecider;
import jadex.commons.IRemoteFilter;
import jadex.commons.IResultCommand;
import jadex.commons.MethodInfo;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 *  For prefixed access of required services in capability.
 */
public class ServiceContainerProxy implements IServiceContainer
{
	//-------- attributes --------
	
	/** The interpreter. */
	protected BDIInterpreter	interpreter;
	
	/** The scope (rcapability). */
	protected Object	scope;
	
	//-------- constructors --------
	
	/**
	 *  Create a service container proxy.
	 */
	public ServiceContainerProxy(BDIInterpreter interpreter, Object scope)
	{
		this.interpreter	= interpreter;
		this.scope	= scope;
	}
	
	//-------- internal admin methods --------
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	// todo: remove, only call from platform
	public IFuture start()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	// todo: remove, only call from platform
	public IFuture shutdown()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Add a service to the container.
	 *  The service is started, if the container is already running.
	 *  @param service The service.
	 *  @param info The provided service info.
	 *  @param componentfetcher	 Helper to fetch corrent object for component injection based on field type.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture<Void>	addService(IInternalService service, ProvidedServiceInfo info, IResultCommand<Object, Class<?>> componentfetcher)
	{
		return interpreter.getServiceContainer().addService(service, info, componentfetcher);
	}
	
	/**
	 *  Removes a service from the container (shutdowns also the service if the container is running).
	 *  @param service The service identifier.
	 */
	public IFuture	removeService(IServiceIdentifier sid)
	{
		return interpreter.getServiceContainer().removeService(sid);
	}
	
	//-------- internal user methods --------
	
	/**
	 *  Get one service of a type from a specific component.
	 *  @param type The class.
	 *  @param cid The component identifier of the target component.
	 *  @return The corresponding service.
	 */
	public IFuture getService(final Class type, final IComponentIdentifier cid)
	{
		return interpreter.getServiceContainer().getService(type, cid);
	}
	
	/**
	 *  Get provided (declared) service.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public IService getProvidedService(String name)
	{
		// todo: possibly use prefix
		return interpreter.getServiceContainer().getProvidedService(name);
	}
	
	/**
	 *  Get provided (declared) service.
	 *  @param clazz The interface.
	 *  @return The service.
	 */
	public IService[] getProvidedServices(Class clazz)
	{
		return interpreter.getServiceContainer().getProvidedServices(clazz);
	}
	
	/**
	 *  Get the provided service raw implementation.
	 */
	public Object getProvidedServiceRawImpl(Class<?> clazz)
	{
		return interpreter.getServiceContainer().getProvidedServiceRawImpl(clazz);
	}
	
	/**
	 *  Get the provided service.
	 */
	public IService getProvidedService(Class<?> clazz)
	{
		return interpreter.getServiceContainer().getProvidedService(clazz);
	}
	
	/**
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name)
	{
		String prefix = interpreter.findServicePrefix(scope);
		return interpreter.getServiceContainer().getRequiredService(prefix+name);
	}

	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return The service.
	 */
	public ITerminableIntermediateFuture getRequiredServices(String name)
	{
		String prefix = interpreter.findServicePrefix(scope);
		return interpreter.getServiceContainer().getRequiredServices(prefix+name);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name, boolean rebind)
	{
		String prefix = interpreter.findServicePrefix(scope);
		return interpreter.getServiceContainer().getRequiredService(prefix+name, rebind);
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public ITerminableIntermediateFuture getRequiredServices(String name, boolean rebind)
	{
		String prefix = interpreter.findServicePrefix(scope);
		return interpreter.getServiceContainer().getRequiredServices(prefix+name, rebind);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name, boolean rebind, IRemoteFilter filter)
	{
		String prefix = interpreter.findServicePrefix(scope);
		return interpreter.getServiceContainer().getRequiredService(prefix+name, rebind, filter);
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public ITerminableIntermediateFuture getRequiredServices(String name, boolean rebind, IRemoteFilter filter)
	{
		String prefix = interpreter.findServicePrefix(scope);
		return interpreter.getServiceContainer().getRequiredServices(prefix+name, rebind, filter);
	}
	
	/**
	 *  Get the result of the last search.
	 *  @param name The required service name.
	 *  @return The last result.
	 */
	public <T> T getLastRequiredService(String name)
	{
		return interpreter.getServiceContainer().getLastRequiredService(name);
	}
	
	/**
	 *  Get the result of the last search.
	 *  @param name The required services name.
	 *  @return The last result.
	 */
	public <T> Collection<T> getLastRequiredServices(String name)
	{
		return interpreter.getServiceContainer().getLastRequiredServices(name);
	}
	
	/**
	 *  Add a service interceptor.
	 *  @param interceptor The interceptor.
	 *  @param service The service.
	 *  @param pos The position (0=first).
	 */
	public void addInterceptor(IServiceInvocationInterceptor interceptor, Object service, int pos)
	{
		interpreter.getServiceContainer().addInterceptor(interceptor, service, pos);
	}

	/**
	 *  Remove a service interceptor.
	 *  @param interceptor The interceptor.
	 *  @param service The service.
	 */
	public void removeInterceptor(IServiceInvocationInterceptor interceptor, Object service)
	{
		interpreter.getServiceContainer().removeInterceptor(interceptor, service);
	}
	
	/**
	 *  Get all service interceptors.
	 *  @return The interceptors.
	 */
	public IServiceInvocationInterceptor[] getInterceptors(Object service)
	{
		return interpreter.getServiceContainer().getInterceptors(service);
	}

	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public ITerminableIntermediateFuture<IService>	getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector)
	{
		return interpreter.getServiceContainer().getServices(manager, decider, selector);
	}
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public IFuture<IServiceProvider>	getParent()
	{
		return interpreter.getServiceContainer().getParent();
	}
	
	/**
	 *  Get the children container.
	 *  @return The children container.
	 */
	public IFuture<Collection<IServiceProvider>>	getChildren()
	{
		return interpreter.getServiceContainer().getChildren();
	}
	
	/**
	 *  Get the globally unique id of the provider.
	 *  @return The id of this provider.
	 */
	public IComponentIdentifier	getId()
	{
		return interpreter.getServiceContainer().getId();
	}
	
	/**
	 *  Get the type of the service provider (e.g. enclosing component type).
	 *  @return The type of this provider.
	 */
	public String	getType()
	{
		return interpreter.getServiceContainer().getType();
	}

	/**
	 *  Get the required service infos.
	 */
	public RequiredServiceInfo[] getRequiredServiceInfos()
	{
		return interpreter.getServiceContainer().getRequiredServiceInfos();
	}
	
	/**
	 *  Get the required service infos.
	 */
	public RequiredServiceInfo getRequiredServiceInfo(String name)
	{
		return interpreter.getServiceContainer().getRequiredServiceInfo(name);
	}

	/**
	 *  Set the required services.
	 *  @param required services The required services to set.
	 */
	public void setRequiredServiceInfos(RequiredServiceInfo[] requiredservices)
	{
		interpreter.getServiceContainer().setRequiredServiceInfos(requiredservices);
	}
	
	/**
	 *  Add required services for a given prefix.
	 *  @param prefix The name prefix to use.
	 *  @param required services The required services to set.
	 */
	public void addRequiredServiceInfos(RequiredServiceInfo[] requiredservices)
	{
		interpreter.getServiceContainer().addRequiredServiceInfos(requiredservices);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public IFuture searchService(Class type)
	{
		return interpreter.getServiceContainer().searchService(type);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public IFuture searchService(Class type, String scope)
	{
		return interpreter.getServiceContainer().searchService(type, scope);
	}
	
	// todo: remove
	/**
	 *  Get one service of a type and only search upwards (parents).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public IFuture searchServiceUpwards(Class type)
	{
		return interpreter.getServiceContainer().searchServiceUpwards(type);
	}

	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IIntermediateFuture searchServices(Class type)
	{
		return interpreter.getServiceContainer().searchServices(type);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IIntermediateFuture searchServices(Class type, String scope)
	{
		return interpreter.getServiceContainer().searchServices(type, scope);
	}
	
	/**
	 *  Get the required service property provider for a service.
	 */
	public INFMixedPropertyProvider getRequiredServicePropertyProvider(IServiceIdentifier sid)
	{
		return interpreter.getServiceContainer().getRequiredServicePropertyProvider(sid);
	}
	
	/**
	 *  Has the service a property provider.
	 */
	public boolean hasRequiredServicePropertyProvider(IServiceIdentifier sid)
	{
		return interpreter.getServiceContainer().hasRequiredServicePropertyProvider(sid);
	}
	
	/**
	 *  Add a method invocation handler.
	 */
	public void addMethodInvocationListener(IServiceIdentifier sid, MethodInfo mi, IMethodInvocationListener listener)
	{
		interpreter.getServiceContainer().addMethodInvocationListener(sid, mi, listener);
	}
	
	/**
	 *  Remove a method invocation handler.
	 */
	public void removeMethodInvocationListener(IServiceIdentifier sid, MethodInfo mi, IMethodInvocationListener listener)
	{
		interpreter.getServiceContainer().removeMethodInvocationListener(sid, mi, listener);
	}
	
	/**
	 *  Notify listeners that a service method has been called.
	 */
	public void notifyMethodListeners(IServiceIdentifier sid, boolean start, Object proxy, final Method method, final Object[] args, Object callid, ServiceInvocationContext context)
	{
		interpreter.getServiceContainer().notifyMethodListeners(sid, start, proxy, method, args, callid, context);
	}
	
	/**
	 *  Test if service and method has listeners.
	 */
	public boolean hasMethodListeners(IServiceIdentifier sid, MethodInfo mi)
	{
		return interpreter.getServiceContainer().hasMethodListeners(sid, mi);
	}
}
