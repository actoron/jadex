package jadex.tools.web.jcc;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.commons.Boolean3;
import jadex.commons.IResultCommand;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Publish;

/**
 *  Frontend controller web jcc agent.
 *  
 *  Uses invokeServiceMethod() to delegate calls to the corresponding platforms (accessed by frontend).
 */
@ProvidedServices(
{
	@ProvidedService(name="jccweb", type=IJCCWebService.class,
		scope=ServiceScope.PLATFORM,
		publish=@Publish(publishtype=IPublishService.PUBLISH_RS, publishid="[http://localhost:8080/]webjcc"))
	//@ProvidedService(name="starterweb", type=IStarterWebService.class)
})
@Agent(autostart=Boolean3.FALSE,
	predecessors="jadex.extension.rs.publish.JettyRSPublishAgent") // Hack! could be other publish agent :-(
public class JCCWebAgent implements IJCCWebService
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentCreated
	protected IFuture<Void>	setup()
	{
		getPlatforms();
		
		IWebPublishService wps = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IWebPublishService.class));
		return wps.publishResources("[http://localhost:8080/]", "META-INF/resources2");
	}
	
	/**
	 *  Get the established connections.
	 *  @return A list of connections.
	 */
	public IFuture<Collection<IComponentIdentifier>> getPlatforms()
	{
		Future<Collection<IComponentIdentifier>> ret = new Future<>();
		
		ITerminableIntermediateFuture<IExternalAccess> ret1 = agent.searchServices(new ServiceQuery<>(IExternalAccess.class, ServiceScope.NETWORK).setServiceTags(IExternalAccess.PLATFORM));
		ITerminableIntermediateFuture<IExternalAccess> ret2 = agent.searchServices(new ServiceQuery<>(IExternalAccess.class, ServiceScope.GLOBAL).setServiceTags(IExternalAccess.PLATFORM));
	
		FutureBarrier<Collection<IExternalAccess>> bar = new FutureBarrier<>();
		bar.addFuture(ret1);
		bar.addFuture(ret2);
		
		bar.waitFor().addResultListener(new ExceptionDelegationResultListener<Void, Collection<IComponentIdentifier>>(ret)
		{
			@Override
			public void customResultAvailable(Void result) throws Exception
			{
				Collection<IExternalAccess> col1 = bar.getResult(0);
				Collection<IExternalAccess> col2 = bar.getResult(1);
				Collection<IComponentIdentifier> col = new HashSet<>();
				for(IExternalAccess ex: col1)
					col.add(ex.getId());
				for(IExternalAccess ex: col2)
					col.add(ex.getId());
				
				System.out.println("found platforms: "+col);
				
				ret.setResult(col);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get events about known platforms.
	 *  @return Events for platforms.
	 */
	public ISubscriptionIntermediateFuture<ServiceEvent<IComponentIdentifier>> subscribeToPlatforms()
	{
		ISubscriptionIntermediateFuture<ServiceEvent<IExternalAccess>> net = agent.addQuery(new ServiceQuery<>(IExternalAccess.class, ServiceScope.NETWORK).setEventMode().setServiceTags(IExternalAccess.PLATFORM));
		ISubscriptionIntermediateFuture<ServiceEvent<IExternalAccess>> glo = agent.addQuery(new ServiceQuery<>(IExternalAccess.class, ServiceScope.GLOBAL).setEventMode().setServiceTags(IExternalAccess.PLATFORM));

		ISubscriptionIntermediateFuture<ServiceEvent<IComponentIdentifier>> ret = SFuture.combineSubscriptionFutures(agent, net, glo, new IResultCommand<ServiceEvent<IComponentIdentifier>, ServiceEvent<IExternalAccess>>()
		{
			@Override
			public ServiceEvent<IComponentIdentifier> execute(ServiceEvent<IExternalAccess> res)
			{
				return new ServiceEvent<IComponentIdentifier>(res.getService().getId(), res.getType());
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the JCC plugin html fragments.
	 */
	public IFuture<Map<String, String>> getPluginFragments(IComponentIdentifier cid)
	{
		Future<Map<String, String>> ret = new Future<>();
		
		// If not local platform
		if(cid!=null && !cid.getRoot().equals(agent.getId().getRoot()))
		{
			agent.searchService(new ServiceQuery<IJCCWebService>(IJCCWebService.class).setSearchStart(cid.getRoot()))
				.addResultListener(new ExceptionDelegationResultListener<IJCCWebService, Map<String, String>>(ret)
			{
				@Override
				public void customResultAvailable(IJCCWebService jccser) throws Exception
				{
					jccser.getPluginFragments(cid).addResultListener(new DelegationResultListener<>(ret));
				}
			});
		}
		else
		{
			Map<String, String> res = new HashMap<>();
			
			Collection<IJCCPluginService> pluginsers = agent.searchLocalServices(new ServiceQuery<IJCCPluginService>(IJCCPluginService.class, ServiceScope.PLATFORM));
			
			for(IJCCPluginService ser: pluginsers)
			{
				String name = ser.getPluginName().get();
				String tag = ser.getPluginComponent().get();
				res.put(name, tag);
			}
		
			//res.put("starter", loadTag("jadex/tools/web/starter.tag"));
			//res.put("security", loadTag("jadex/tools/web/security.tag"));
		
			ret.setResult(res);
		}
		
		//System.out.println("fragments: "+ret);
		
		return ret;
	}
	
	/**
	 *  Invoke a Jadex service on the managed platform.
	 */
	public IFuture<Object> invokeServiceMethod(IComponentIdentifier cid, ClassInfo servicetype, final String methodname, final Object[] args, final ClassInfo[] argtypes)
	{
		final Future<Object> ret = new Future<Object>();
		
		// Search service with startpoint of given platform 
		agent.searchService(new ServiceQuery<IService>(servicetype).setSearchStart(cid.getRoot()).setScope(ServiceScope.PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<IService, Object>(ret)
		{
			@Override
			public void customResultAvailable(IService ser) throws Exception
			{
				System.out.println("Invoking service method: "+ser+" "+methodname);
				ser.invokeMethod(methodname, argtypes, args).addResultListener(new DelegationResultListener<Object>(ret));
//				{
//					public void customResultAvailable(Object result)
//					{
//						System.out.println("result is: "+result);
//						super.customResultAvailable(result);
//					}
//				});
			}
		});
		
		return ret;
	}
}	
