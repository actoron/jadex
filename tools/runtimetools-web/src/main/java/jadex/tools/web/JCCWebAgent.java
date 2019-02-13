package jadex.tools.web;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.commons.Boolean3;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
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

@ProvidedServices(@ProvidedService(name="webjcc", type=IWebJCCService.class,
		scope=ServiceScope.PLATFORM,
		publish=@Publish(publishtype=IPublishService.PUBLISH_RS, publishid="[http://localhost:8080/]webjcc"
	))
)
@Agent(autostart=Boolean3.TRUE,
	predecessors="jadex.extension.rs.publish.JettyRSPublishAgent") // Hack! could be other publish agent :-(
public class JCCWebAgent implements IWebJCCService
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
	public IFuture<Map<String, String>> getPluginFragments()
	{
		// todo search for running webjcc services and ask for plugin fragment
		
		Map<String, String> ret = new HashMap<>();
		
		ret.put("starter", loadTag("jadex/tools/web/starter.tag"));
		ret.put("security", loadTag("jadex/tools/web/security.tag"));
		
		//System.out.println("fragments: "+ret);
		
		return new Future<Map<String, String>>(ret);
	}

	/**
	 * 
	 */
	public String loadTag(String name)
	{
		String ret;
		
		Scanner sc = null;
		try
		{
			InputStream is = SUtil.getResource0(name, agent.getClassLoader());
			sc = new Scanner(is);
			ret = sc.useDelimiter("\\A").next();
			
	//		System.out.println(ret);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally
		{
			if(sc!=null)
			{
				sc.close();
			}
		}
		
		return ret;
	}
	
}	
