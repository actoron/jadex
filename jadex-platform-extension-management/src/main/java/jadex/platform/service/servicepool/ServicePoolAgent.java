package jadex.platform.service.servicepool;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.ComponentServiceContainer;
import jadex.commons.IPoolStrategy;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *  The service pool agent can be used to handle services in a pooled manner.
 *  
 *  Via its service pool service interface new service types can be registered (and removed).
 *  For each registered service the agent will create a proxy service that is found via search.
 *  Incoming service invocations are routed towards the concrete handler agents that are either
 *  already in the pool or are created up to a creation limit. 
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IServicePoolService.class, implementation=@Implementation(expression="$component")))
public class ServicePoolAgent extends MicroAgent implements IServicePoolService
{
	//-------- attributes --------
	
	/** The registered service types. */
	protected Map<Class<?>, ServiceHandler> servicetypes;
	
	//-------- interface methods --------
	
	/**
	 *  Add a new service type and a strategy.
	 *  @param servicetype The service type.
	 *  @param strategy The service pool strategy.
	 */
	public IFuture<Void> addServiceType(Class<?> servicetype, IPoolStrategy strategy, String componentname)
	{
		if(servicetypes==null)
			servicetypes = new HashMap<Class<?>, ServiceHandler>();
		ServiceHandler handler = new ServiceHandler(this, servicetype, strategy, componentname);
		servicetypes.put(servicetype, handler);

		// add service proxy
		Object service = Proxy.newProxyInstance(getClassLoader(), new Class<?>[]{servicetype}, handler);
		return addService(null, servicetype, service);
	}
	
	
	/**
	 *  Remove a service type.
	 *  @param servicetype The service type.
	 */
	public IFuture<Void> removeServiceType(Class<?> servicetype)
	{
		Future<Void> ret = new Future<Void>();
		IService ser = null;
		if(servicetypes!=null)
		{
			servicetypes.remove(servicetype);
			// remove service proxy
			ser = getServiceContainer().getProvidedService(servicetype);
			if(ser!=null)
			{
				removeService(ser.getServiceIdentifier());
				ret.setResult(null);
			}
		}
		
		if(ser==null)
			ret.setException(new IllegalArgumentException("Service type not found: "+servicetype));

		return ret;
	}
	
	/**
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IServiceContainer createServiceContainer(Map<String, Object> args)
	{
		return new ComponentServiceContainer(getAgentAdapter(), getModel().getType(), this, getInterpreter().isRealtime())
		{
			/**
			 *  Get the children container.
			 *  @return The children container.
			 *  
			 *  Returns no children to avoid finding them via search and pool manages these resources.
			 */
			public IFuture<Collection<IServiceProvider>>	getChildren()
			{
				return new Future<Collection<IServiceProvider>>(Collections.EMPTY_LIST);
			}
		};
	}
	
	
}
