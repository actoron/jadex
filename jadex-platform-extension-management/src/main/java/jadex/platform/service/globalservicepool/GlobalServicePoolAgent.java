package jadex.platform.service.globalservicepool;


import jadex.bridge.ClassInfo;
import jadex.bridge.ITargetResolver;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.IService;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
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
import jadex.platform.service.servicepool.ServicePoolAgent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  The global service pool agent can be used to handle services in a pooled manner.
 *  
 *  A global pool consists of workers on different platforms. These workers typically
 *  are local service pools themselves.
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
	@ProvidedService(type=IGlobalPoolManagementService.class)
})
@ComponentTypes(@ComponentType(name="pool", clazz=ServicePoolAgent.class))
@Configurations(@Configuration(name="def", components=@Component(type="pool")))
public class GlobalServicePoolAgent implements IGlobalServicePoolService, IGlobalPoolManagementService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The pool manager. */
	protected Map<Class<?>, GlobalPoolServiceManager> managers = new HashMap<Class<?>, GlobalPoolServiceManager>();
	
	//-------- interface methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		final Future<Void> ret = new Future<Void>();
		this.managers = new HashMap<Class<?>, GlobalPoolServiceManager>();
		
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
		
		return ret;
	}
	
	/**
	 *  Add a new service type and a strategy.
	 *  @param servicetype The service type.
	 *  @param componentmodel The component model.
	 */
	public IFuture<Void> addServiceType(final Class<?> servicetype, final String componentmodel, CreationInfo info)
	{
		final Future<Void> ret = new Future<Void>();
		// Create one service manager per service type
		GlobalPoolServiceManager manager = new GlobalPoolServiceManager(agent, servicetype, componentmodel, info);
		managers.put(servicetype, manager);
		IServicePoolService ser = SServiceProvider.getLocalService(agent.getServiceProvider(), IServicePoolService.class);
		// todo: fix if more than one service type should be supported by one worker (not intended)
		if(info==null)
			info = new CreationInfo();
		ProvidedServiceInfo psi = new ProvidedServiceInfo(null, servicetype, null, RequiredServiceInfo.SCOPE_PARENT, null, null);
		info.setProvidedServiceInfos(new ProvidedServiceInfo[]{psi});
		ser.addServiceType(servicetype, null, componentmodel, info, null, RequiredServiceInfo.SCOPE_PARENT).addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result) 
			{
				// Add to global pool with magic targetresolver for intelligent proxy
				ProvidedServiceInfo psi = new ProvidedServiceInfo(null, servicetype, null, null, null, null);
				List<UnparsedExpression> props = new ArrayList<UnparsedExpression>();
				props.add(new UnparsedExpression(ITargetResolver.TARGETRESOLVER, GlobalServicePoolTargetResolver.class.getName()+".class"));
				psi.setProperties(props);
				Object service = Proxy.newProxyInstance(agent.getClassLoader(), new Class[]{servicetype}, new ForwardHandler(servicetype));
				agent.addService(null, servicetype, service, psi).addResultListener(new DelegationResultListener<Void>(ret));
			}
			
			public void exceptionOccurred(Exception exception) 
			{
				exception.printStackTrace();
				super.exceptionOccurred(exception);
			}
		});
		return ret;
	}
	
	/**
	 *  Remove a service type.
	 *  @param servicetype The service type.
	 */
	public IFuture<Void> removeServiceType(final Class<?> servicetype)
	{
		final Future<Void> ret = new Future<Void>();
		IServicePoolService ser = SServiceProvider.getLocalService(agent.getServiceProvider(), IServicePoolService.class);
		managers.remove(servicetype);
		ser.removeServiceType(servicetype).addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result) 
			{
				IService service = agent.getServiceContainer().getProvidedService(servicetype);
				if(service!=null)
				{
					agent.removeService(service.getServiceIdentifier()).addResultListener(new DelegationResultListener<Void>(ret));
				}
				else
				{
					ret.setException(new RuntimeException("Service could not be removed: "+servicetype));
				}
			}
		});
		return ret;
	}	
	
	//-------- interface methods --------
	
	/**
	 *  Get a set of services managed by the pool.
	 *  @param type The service type.
	 *  @return A number of services from the pool.
	 */
	public IIntermediateFuture<IService> getPoolServices(ClassInfo type)
	{
		Class<?> clazz = type.getType(agent.getClassLoader());
		GlobalPoolServiceManager manager = managers.get(clazz);
		return manager.getPoolServices(clazz);
	}
	
	/**
	 *  Inform about service usage.
	 *  @param The usage infos per service class.
	 */
	public IFuture<Void> sendUsageInfo(Map<ClassInfo, UsageInfo> infos)
	{
		for(ClassInfo type: infos.keySet())
		{
			Class<?> clazz = type.getType(agent.getClassLoader());
			GlobalPoolServiceManager manager = managers.get(clazz);
			manager.addUsageInfo(infos);
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Invocation handler that forwards to the contained local pool.
	 */
	public class ForwardHandler implements InvocationHandler
	{
		/** The service type. */
		protected Class<?> servicetype;
		
		/**
		 *  Create a new forward handler.
		 */
		public ForwardHandler(Class<?> servicetype)
		{
			this.servicetype = servicetype;
		}
		
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			IService poolser = (IService)SServiceProvider.getLocalService(agent.getServiceProvider(), IServicePoolService.class);
			IService ser = (IService)SServiceProvider.getLocalService(agent.getServiceProvider(), servicetype, poolser.getServiceIdentifier().getProviderId());
			return method.invoke(ser, args);
		}
	}
}
