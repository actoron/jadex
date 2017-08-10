package jadex.platform.service.servicepool;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.DefaultPoolStrategy;
import jadex.commons.IPoolStrategy;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

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
@Arguments(
{
	@Argument(name="serviceinfos", clazz=PoolServiceInfo[].class, description="The array of service pool infos.")
})
@ProvidedServices(@ProvidedService(type=IServicePoolService.class))
public class ServicePoolAgent implements IServicePoolService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The registered service types. */
	protected Map<Class<?>, ServiceHandler> servicetypes;
	
	//-------- interface methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		final Future<Void> ret = new Future<Void>();

		PoolServiceInfo[] psis = (PoolServiceInfo[])agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("serviceinfos");
		
		if(psis!=null)
		{
			CounterResultListener<Void> lis = new CounterResultListener<Void>(psis.length, true, new DelegationResultListener<Void>(ret));
			for(PoolServiceInfo psi: psis)
			{
				IPoolStrategy str = psi.getPoolStrategy()==null? getDefaultStrategy(): (IPoolStrategy)psi.getPoolStrategy();
				CreationInfo ci = psi.getArguments()!=null? new CreationInfo(psi.getArguments()): null;
				Class<?> sertype = psi.getServicetype().getType(agent.getClassLoader(), agent.getModel().getAllImports());
				if(sertype==null)
					throw new RuntimeException("Could not resolve service class: "+psi.getServicetype());
				addServiceType(sertype, str, psi.getWorkermodel(), ci, psi.getPublishInfo()).addResultListener(lis);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
//	/**
//	 *  Execute the functional body of the agent.
//	 *  Is only called once.
//	 */
//	public IFuture<Void> executeBody()
//	{
//		System.out.println("body");
//		IComponentStep<Void> step = new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				if(servicetypes!=null)
//				{
//					for(ServiceHandler sh: servicetypes.values())
//					{
//						System.out.println("handler state: "+sh);
//					}
//					waitFor(3000, this);
//				}
//				return IFuture.DONE;
//			}
//		};
//		waitFor(3000, step);
//		return new Future<Void>();
//	}
	
	/**
	 *  Add a new service type and a strategy.
	 *  @param servicetype The service type.
	 *  @param componentmodel The component model.
	 */
	public IFuture<Void> addServiceType(Class<?> servicetype, String componentmodel)
	{
		return addServiceType(servicetype, null, componentmodel);
	}
	
	/**
	 *  Add a new service type and a strategy.
	 *  @param servicetype The service type.
	 *  @param componentmodel The component model.
	 */
	public IFuture<Void> addServiceType(Class<?> servicetype, String componentmodel, CreationInfo info)
	{
		return addServiceType(servicetype, null, componentmodel, info, null);
	}
	
	/**
	 *  Add a new service type and a strategy.
	 *  @param servicetype The service type.
	 *  @param strategy The service pool strategy.
	 */
	public IFuture<Void> addServiceType(Class<?> servicetype, IPoolStrategy strategy, String componentmodel)
	{
		return addServiceType(servicetype, strategy, componentmodel, null, null);
	}
	
	/**
	 *  Add a new service type and a strategy.
	 *  @param servicetype The service type.
	 *  @param strategy The service pool strategy.
	 */
	public IFuture<Void> addServiceType(Class<?> servicetype, IPoolStrategy strategy, String componentmodel, CreationInfo info, PublishInfo pi)
	{
		return addServiceType(servicetype, strategy, componentmodel, info, pi, null);
	}
	
	/**
	 *  Add a new service type and a strategy.
	 *  @param servicetype The service type.
	 *  @param strategy The service pool strategy.
	 */
	public IFuture<Void> addServiceType(Class<?> servicetype, IPoolStrategy strategy, String componentmodel, CreationInfo info, PublishInfo pi, String scope)
	{
		if(servicetypes==null)
			servicetypes = new HashMap<Class<?>, ServiceHandler>();
		if(strategy==null)
			strategy = getDefaultStrategy();
		ServiceHandler handler = new ServiceHandler(agent, servicetype, strategy, componentmodel, info);
		servicetypes.put(servicetype, handler);

		// add service proxy
		try
		{
			Object service = ProxyFactory.newProxyInstance(agent.getClassLoader(), new Class<?>[]{servicetype}, handler);
			return agent.getComponentFeature(IProvidedServicesFeature.class).addService(null, servicetype, service, pi, scope);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new Future<Void>(e);
//			throw new RuntimeException(e);
		}
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
			ser = (IService)agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(servicetype);
			if(ser!=null)
			{
				agent.getComponentFeature(IProvidedServicesFeature.class).removeService(ser.getServiceIdentifier());
				ret.setResult(null);
			}
		}
		
		if(ser==null)
			ret.setException(new IllegalArgumentException("Service type not found: "+servicetype));

		return ret;
	}
	
	/**
	 * 
	 */
	protected IPoolStrategy getDefaultStrategy()
	{
		return new DefaultPoolStrategy(5, 35000, 10);
//		return new DefaultPoolStrategy(Runtime.getRuntime().availableProcessors()+1, 
//			Runtime.getRuntime().availableProcessors()+1);
	}
	
	// Not necessary because service publication scope of workers is set to parent
//	/**
//	 *  Get the service container.
//	 *  @return The service container.
//	 */
//	public IServiceContainer createServiceContainer(Map<String, Object> args)
//	{
//		return new ComponentServiceContainer(getAgentAdapter(), getModel().getType(), this, getInterpreter().isRealtime(), getInterpreter().getServiceRegistry())
//		{
//			/**
//			 *  Get the children container.
//			 *  @return The children container.
//			 *  
//			 *  Returns no children to avoid finding them via search and pool manages these resources.
//			 */
//			public IFuture<Collection<IServiceProvider>>	getChildren()
//			{
//				return new Future<Collection<IServiceProvider>>(Collections.EMPTY_LIST);
//			}
//		};
//	}
}
