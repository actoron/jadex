package jadex.bdiv3.runtime.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.search.IResultSelector;
import jadex.bridge.service.search.ISearchManager;
import jadex.bridge.service.search.IVisitDecider;
import jadex.commons.IFilter;
import jadex.commons.IRemoteFilter;
import jadex.commons.IResultCommand;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

import java.util.Collection;

/**
 *  For prefixed access of required services in capability.
 */
public class ServiceContainerProxy implements IServiceContainer
{
	//-------- attributes --------
	
	/** The interpreter. */
	protected BDIAgentInterpreter	interpreter;
	
	/** The fully qualified capability name (or null for the agent). */
	protected String	capa;
	
	//-------- constructors --------
	
	/**
	 *  Create a service container proxy.
	 */
	public ServiceContainerProxy(BDIAgentInterpreter interpreter, String capa)
	{
		this.interpreter	= interpreter;
		this.capa	= capa;
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
		return interpreter.getServiceContainer().getProvidedService(capa!=null ? capa+BDIAgentInterpreter.CAPABILITY_SEPARATOR+name : name);
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
		return interpreter.getServiceContainer().getRequiredService(capa!=null ? capa+BDIAgentInterpreter.CAPABILITY_SEPARATOR+name : name);
	}

	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return The service.
	 */
	public ITerminableIntermediateFuture getRequiredServices(String name)
	{
		return interpreter.getServiceContainer().getRequiredServices(capa!=null ? capa+BDIAgentInterpreter.CAPABILITY_SEPARATOR+name : name);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name, boolean rebind)
	{
		return interpreter.getServiceContainer().getRequiredService(capa!=null ? capa+BDIAgentInterpreter.CAPABILITY_SEPARATOR+name : name);
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public ITerminableIntermediateFuture getRequiredServices(String name, boolean rebind)
	{
		return interpreter.getServiceContainer().getRequiredServices(capa!=null ? capa+BDIAgentInterpreter.CAPABILITY_SEPARATOR+name : name, rebind);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name, boolean rebind, IRemoteFilter filter)
	{
		return interpreter.getServiceContainer().getRequiredService(capa!=null ? capa+BDIAgentInterpreter.CAPABILITY_SEPARATOR+name : name, rebind, filter);
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public ITerminableIntermediateFuture getRequiredServices(String name, boolean rebind, IRemoteFilter filter)
	{
		return interpreter.getServiceContainer().getRequiredServices(capa!=null ? capa+BDIAgentInterpreter.CAPABILITY_SEPARATOR+name : name, rebind, filter);
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
}
