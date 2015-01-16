package jadex.platform.service.globalservicepool;


import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.platform.service.servicepool.IServicePoolService;
import jadex.platform.service.servicepool.PoolServiceInfo;
import jadex.platform.service.servicepool.ServiceHandler;
import jadex.platform.service.servicepool.ServicePoolAgent;

import java.util.HashMap;
import java.util.Map;

/**
 *  The service pool agent can be used to handle services in a pooled manner.
 */
@Agent
@Service
@Arguments(
{
	@Argument(name="serviceinfos", clazz=PoolServiceInfo[].class, description="The array of service pool infos.")
})
@ProvidedServices(
{
	@ProvidedService(type=IGlobalServicePoolService.class),
	@ProvidedService(type=IPoolManagementService.class)
})
@ComponentTypes(@ComponentType(name="pool", clazz=ServicePoolAgent.class))
@Configurations(@Configuration(name="def", components=@Component(type="pool")))
public class GlobalServicePoolAgent implements IGlobalServicePoolService, IPoolManagementService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The registered service types. */
	protected Map<Class<?>, ServiceHandler> servicetypes;
	
	/** The pool manager. */
	protected Map<Class<?>, PoolServiceManager> managers = new HashMap<Class<?>, PoolServiceManager>();
	
	//-------- interface methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		final Future<Void> ret = new Future<Void>();
		this.managers = new HashMap<Class<?>, PoolServiceManager>();
		
		PoolServiceInfo[] psis = (PoolServiceInfo[])agent.getArguments().get("serviceinfos");
		
		if(psis!=null)
		{
			CounterResultListener<Void> lis = new CounterResultListener<Void>(psis.length, true, new DelegationResultListener<Void>(ret));
			for(PoolServiceInfo psi: psis)
			{
//				IPoolStrategy str = psi.getPoolStrategy()==null? new DefaultPoolStrategy(Runtime.getRuntime().availableProcessors()+1, 
//					Runtime.getRuntime().availableProcessors()+1): psi.getPoolStrategy();
				CreationInfo ci = psi.getArguments()!=null? new CreationInfo(psi.getArguments()): null;
				addServiceType(psi.getServicetype().getType(agent.getClassLoader(), agent.getModel().getAllImports()), psi.getWorkermodel(), ci).addResultListener(lis);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		
//		return IFuture.DONE;
		return ret;
	}
	
	/**
	 *  Add a new service type and a strategy.
	 *  @param servicetype The service type.
	 *  @param componentmodel The component model.
	 */
	public IFuture<Void> addServiceType(Class<?> servicetype, String componentmodel, CreationInfo info)
	{
		IServicePoolService ser = SServiceProvider.getLocalService(agent.getServiceProvider(), IServicePoolService.class);
		PoolServiceManager manager = new PoolServiceManager(agent, servicetype, componentmodel, info);
		managers.put(servicetype, manager);
		return ser.addServiceType(servicetype, componentmodel, info);
	}
	
	/**
	 *  Remove a service type.
	 *  @param servicetype The service type.
	 */
	public IFuture<Void> removeServiceType(Class<?> servicetype)
	{
		IServicePoolService ser = SServiceProvider.getLocalService(agent.getServiceProvider(), IServicePoolService.class);
		managers.remove(servicetype);
		return ser.removeServiceType(servicetype);
	}	
	
	/**
	 *  Get a set of services managed by the pool.
	 *  @param type The service type.
	 *  @return A number of services from the pool.
	 */
	public IIntermediateFuture<IService> getPoolServices(Class<?> type)
	{
		PoolServiceManager manager = managers.get(type);
		return manager.getPoolServices(type);
	}
}
