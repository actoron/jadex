package jadex.bdi.runtime.impl;

import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IResultSelector;
import jadex.bridge.service.ISearchManager;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.IVisitDecider;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

import java.util.ArrayList;
import java.util.List;

public class ServiceContainerProxy implements IServiceContainer
{
	//-------- attributes --------
	
	/** The plan. */
	protected AbstractPlan	plan;
	
	//-------- constructors --------
	
	/**
	 *  Create a service container proxy.
	 */
	public ServiceContainerProxy(AbstractPlan plan)
	{
		this.plan	= plan;
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
	 */
	public IFuture	addService(IInternalService service)
	{
		return plan.getInterpreter().getServiceContainer().addService(service);
	}
	

	/**
	 *  Removes a service from the container (shutdowns also the service if the container is running).
	 *  @param service The service identifier.
	 */
	public IFuture	removeService(IServiceIdentifier sid)
	{
		return plan.getInterpreter().getServiceContainer().removeService(sid);
	}
	
	//-------- internal user methods --------
	
	/**
	 *  Get provided (declared) service.
	 *  @param class The interface.
	 *  @return The service.
	 */
	public IService getProvidedService(Class clazz)
	{
		return plan.getInterpreter().getServiceContainer().getProvidedService(clazz);
	}
	
	/**
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name)
	{
		String prefix = findServicePrefix();
		return plan.getInterpreter().getServiceContainer().getRequiredService(prefix+name);
	}

	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return The service.
	 */
	public IIntermediateFuture getRequiredServices(String name)
	{
		String prefix = findServicePrefix();
		return plan.getInterpreter().getServiceContainer().getRequiredServices(prefix+name);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name, boolean rebind)
	{
		String prefix = findServicePrefix();
		return plan.getInterpreter().getServiceContainer().getRequiredService(prefix+name, rebind);
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public IIntermediateFuture getRequiredServices(String name, boolean rebind)
	{
		String prefix = findServicePrefix();
		return plan.getInterpreter().getServiceContainer().getRequiredServices(prefix+name, rebind);
	}
	
	/**
	 *  Add a service interceptor.
	 *  @param interceptor The interceptor.
	 *  @param service The service.
	 *  @param pos The position (0=first).
	 */
	public void addInterceptor(IServiceInvocationInterceptor interceptor, IService service, int pos)
	{
		plan.getInterpreter().getServiceContainer().addInterceptor(interceptor, service, pos);
	}

	/**
	 *  Remove a service interceptor.
	 *  @param interceptor The interceptor.
	 *  @param service The service.
	 */
	public void removeInterceptor(IServiceInvocationInterceptor interceptor, IService service)
	{
		plan.getInterpreter().getServiceContainer().removeInterceptor(interceptor, service);
	}

	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IIntermediateFuture	getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector)
	{
		return plan.getInterpreter().getServiceContainer().getServices(manager, decider, selector);
	}
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public IFuture	getParent()
	{
		return plan.getInterpreter().getServiceContainer().getParent();
	}
	
	/**
	 *  Get the children container.
	 *  @return The children container.
	 */
	public IFuture	getChildren()
	{
		return plan.getInterpreter().getServiceContainer().getChildren();
	}
	
	/**
	 *  Get the globally unique id of the provider.
	 *  @return The id of this provider.
	 */
	public Object	getId()
	{
		return plan.getInterpreter().getServiceContainer().getId();
	}
	
	/**
	 *  Get the type of the service provider (e.g. enclosing component type).
	 *  @return The type of this provider.
	 */
	public String	getType()
	{
		return plan.getInterpreter().getServiceContainer().getType();
	}
	
	//-------- helper methods --------
	
	/**
	 *  The prefix is the name of the capability starting from the agent.
	 */
	protected String findServicePrefix()
	{
		List	path	= new ArrayList();
		plan.getInterpreter().findSubcapability(plan.getInterpreter().getAgent(), plan.getRCapability(), path);
		String prefix	= "";
		for(int i=0; i<path.size(); i++)
		{
			prefix	+= plan.getState().getAttributeValue(path.get(i), OAVBDIRuntimeModel.capabilityreference_has_name)+ ".";
		}
		return prefix;
	}
}
