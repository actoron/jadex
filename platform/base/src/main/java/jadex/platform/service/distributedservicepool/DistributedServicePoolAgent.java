package jadex.platform.service.distributedservicepool;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

@Agent
@Service
@Arguments(
{
	@Argument(name="serviceinfo", clazz=ServiceQuery.class, description="The service pool info."),
	@Argument(name="publishinfo", clazz=PublishInfo.class, description="The info for service publication as e.g. rest."),
	@Argument(name="scope", clazz=ServiceScope.class, description="The publication scope.")
})
public class DistributedServicePoolAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	protected ServiceHandler handler;
	
	/**
	 *  Called once after agent creation.
	 */
	@OnInit
	public IFuture<Void> agentCreated()
	{
		final Future<Void> ret = new Future<Void>();

		//RequiredServiceInfo rsi = (RequiredServiceInfo)agent.getArguments().get("serviceinfo");
		ServiceQuery<IService> query = (ServiceQuery<IService>)agent.getArguments().get("serviceinfo");		
		PublishInfo pi = (PublishInfo)agent.getArguments().get("publishinfo");
		ServiceScope scope = (ServiceScope)agent.getArguments().get("scope");
		
		query.setOwner(agent.getId());
		query.setEventMode();

		if(query!=null)
		{
			addServiceType(query, pi, scope).delegate(ret);

			ISubscriptionIntermediateFuture<ServiceEvent> fut = (ISubscriptionIntermediateFuture)agent.addQuery(query);
				fut.next(event ->
			{
				//System.out.println("event: "+event);
				if(event.getType()==ServiceEvent.SERVICE_ADDED)
				{
					if(!((IService)event.getService()).getServiceId().getProviderId().equals(agent.getId()))
						handler.addService((IService)event.getService());
				}
				else if(event.getType()==ServiceEvent.SERVICE_REMOVED)
				{
					handler.removeService((IServiceIdentifier)event.getService());
				}
			})
				.catchEx(ex ->
			{
				System.out.println("Query error: "+ex);
				ex.printStackTrace();
			});
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
	 *  @param strategy The service pool strategy.
	 */
	public IFuture<Void> addServiceType(ServiceQuery<IService> query, PublishInfo pi, ServiceScope scope)
	{
		// remove old service
		if(handler!=null)
		{
			IService oldser = (IService)agent.getLocalService(query.getServiceType().getType(agent.getClassLoader()));
			agent.removeService(oldser.getServiceId());
		}
		
		this.handler = new ServiceHandler(agent);

		// add service proxy
		try
		{
			Class<?> servicetype = query.getServiceType().getType(agent.getClassLoader());
			Object service = ProxyFactory.newProxyInstance(agent.getClassLoader(), new Class<?>[]{servicetype}, handler);
			return agent.addService(null, servicetype, service, pi, scope);
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
		// remove service proxy
		ser = (IService)agent.getFeature(IProvidedServicesFeature.class).getProvidedService(servicetype);
		if(ser!=null)
		{
			agent.getFeature(IProvidedServicesFeature.class).removeService(ser.getServiceId());
			ret.setResult(null);
		}
		
		if(ser==null)
			ret.setException(new IllegalArgumentException("Service type not found: "+servicetype));

		return ret;
	}
}
