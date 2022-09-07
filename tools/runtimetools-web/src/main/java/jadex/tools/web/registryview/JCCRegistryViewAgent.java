package jadex.tools.web.registryview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.QueryEvent;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQueryInfo;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.memstat.IMemstatService;
import jadex.bridge.service.types.registry.ISuperpeerService;
import jadex.bridge.service.types.transport.ITransportInfoService;
import jadex.bridge.service.types.transport.PlatformData;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateEmptyResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.tools.web.jcc.JCCPluginAgent;

/**
 *  An agent to provide a platform status view in the web.
 */
@ProvidedServices({@ProvidedService(name="registryview", type=IJCCRegistryViewService.class)})
@Agent(autostart=Boolean3.TRUE)
public class JCCRegistryViewAgent extends JCCPluginAgent implements IJCCRegistryViewService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Get the plugin name.
	 *  @return The plugin name.
	 */
	public IFuture<String> getPluginName()
	{
		return new Future<String>("Registry");
	}
	
	/**
	 *  Get the plugin priority.
	 *  @return The plugin priority.
	 */
	public IFuture<Integer> getPriority()
	{
		return new Future<Integer>(80);
	}
	
	/**
	 *  Get the plugin UI path.
	 *  @return The plugin ui path.
	 */
	public String getPluginUIPath()
	{
		return "jadex/tools/web/registryview/registryview.js";
	}
	
	/**
	 *  Get the plugin icon.
	 *  @return The plugin icon.
	 */
	public IFuture<byte[]> getPluginIcon()
	{
		return loadResource("jadex/tools/web/registryview/registryview.png");
	}
	
	@Override
	public IIntermediateFuture<PlatformData> getConnectedPlatforms()
	{
		final IntermediateFuture<PlatformData>	ret	= new IntermediateFuture<PlatformData>();
		FutureBarrier<Collection<PlatformData>>	fubar	= new FutureBarrier<Collection<PlatformData>>();
		for(ITransportInfoService tis: agent.getFeature(IRequiredServicesFeature.class).getLocalServices(new ServiceQuery<>(ITransportInfoService.class)))
		{
			IIntermediateFuture<PlatformData>	fut	= tis.getConnections();
			fut.addResultListener(new IntermediateDelegationResultListener<PlatformData>(ret)
			{
				@Override
				public void exceptionOccurred(Exception exception)
				{
					// ignore
				}
								
				@Override
				public void finished()
				{
					//ignore
				}
			});
			fubar.addFuture(fut);
		}
		fubar.waitFor().addResultListener(new IResultListener<Void>()
		{
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				ret.setFinished();
			}
			
			@Override
			public void resultAvailable(Void result)
			{
				ret.setFinished();
			}
		});
		return ret;
	}

	@Override
	public ISubscriptionIntermediateFuture<PlatformData> subscribeToPlatforms()
	{
		final List<ISubscriptionIntermediateFuture<PlatformData>>futs = new ArrayList<ISubscriptionIntermediateFuture<PlatformData>>();
		final SubscriptionIntermediateFuture<PlatformData>	ret	= new SubscriptionIntermediateFuture<PlatformData>(null, true);
		SFuture.avoidCallTimeouts(ret, agent);
		
		ret.setTerminationCommand(new TerminationCommand()
		{
			@Override
			public void terminated(Exception reason)
			{
//				System.out.println("terminated: "+reason);
				for(ISubscriptionIntermediateFuture<PlatformData> fut: futs)
				{
					fut.terminate();
				}
			}
		});
		
		// TODO: Use query for dynamically added transports
		for(final ITransportInfoService tis: agent.getFeature(IRequiredServicesFeature.class).getLocalServices(new ServiceQuery<>(ITransportInfoService.class)))
		{
			ISubscriptionIntermediateFuture<PlatformData> fut = tis.subscribeToConnections();
			fut.addResultListener(new IntermediateEmptyResultListener<PlatformData>()	// Do not use delegation listener (ignore forward commands like update timer)
			{
				/*@Override
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("status ex: "+exception);
					// ignore
				}
								
				@Override
				public void finished()
				{
//					System.out.println("status fini: "+tis);
					//ignore
				}*/
				
				@Override
				public void intermediateResultAvailable(PlatformData result)
				{
					ret.addIntermediateResult(result);
				}
				
				@Override
				public void resultAvailable(Collection<PlatformData> result)
				{
					// Shouldn't be called
					assert false;
				}
			});
			futs.add(fut);
		}
		return ret;
	}
	
	/**
	 *  Get registered queries of a given (set of) scope(s) or no scope for all queries.
	 *  @return A list of queries.
	 */
	// No intermediate for easier REST?
	// TODO: subscription in registry to get notified about new queries? -> please no polling!
	public IFuture<Collection<ServiceQuery<?>>>	getQueries(String... scope)
	{
		Set<String>	scopes	= scope==null ? null: new HashSet<String>(Arrays.asList(scope));
		IntermediateFuture<ServiceQuery<?>>	ret	= new IntermediateFuture<ServiceQuery<?>>();
		IServiceRegistry reg = ServiceRegistry.getRegistry(agent.getId());
		for(ServiceQueryInfo<?> sqi: reg.getAllQueries())
		{
			if(scopes==null || scopes.contains(sqi.getQuery().getScope().name()))
			{
				ret.addIntermediateResult(sqi.getQuery());
			}
		}
		ret.setFinished();

		return ret;
	}
	
	/**
	 *  Subscribe to query changes (query added / removed).
	 *  @return The subscription future.
	 */
	//protected int cnt = 0;
	public ISubscriptionIntermediateFuture<QueryEvent> subscribeToQueries()
	{
		//final int fcnt = cnt++;
		//System.out.println("subscribe to queries called: "+fcnt);
		ISubscriptionIntermediateFuture<QueryEvent> ret = ServiceRegistry.getRegistry(agent.getId()).subscribeToQueries();
		SubscriptionIntermediateDelegationFuture<QueryEvent> fut = new SubscriptionIntermediateDelegationFuture<QueryEvent>(ret);
		SFuture.avoidCallTimeouts(fut, agent);
		//fut.next(event -> if(event.get)System.out.println("query sub remove event: "+event));
		//fut.catchEx(ex -> System.out.println("query subscription ex: "+ex+" "+fcnt));
		//fut.finished(Void -> System.out.println("query sub finished "+fcnt));
		
		/*fut.addQuietListener(new IIntermediateResultListener<QueryEvent>() 
		{
			@Override
			public void exceptionOccurred(Exception exception) 
			{
			}
			
			@Override
			public void finished() 
			{
			}
			
			@Override
			public void intermediateResultAvailable(QueryEvent event) 
			{
				if(event.getType()==1)
					System.out.println("remove event: "+event);
			}
			
			@Override
			public void maxResultCountAvailable(int max) 
			{
			}
			
			@Override
			public void resultAvailable(Collection<QueryEvent> result) 
			{
			}
		});*/
		return fut;
	}
	
//	/**
//	 *  Get provided services of a given (set of) scope(s) or no scope for all services.
//	 *  @return A list of services.
//	 */
//	// No intermediate for easier REST?
//	// TODO: subscription in registry to get notified about new services? -> please no polling!
//	public IFuture<Collection<IServiceIdentifier>>	getServices(String... scope)
//	{
//		Set<String>	scopes	= scope==null ? null: new HashSet<String>(Arrays.asList(scope));
//		IntermediateFuture<IServiceIdentifier>	ret	= new IntermediateFuture<IServiceIdentifier>();
//		IServiceRegistry	reg	= ServiceRegistry.getRegistry(agent.getId());
//		for(IServiceIdentifier ser: reg.getAllServices())
//		{
//			if(scopes==null || scopes.contains(ser.getScope()))
//			{
//				ret.addIntermediateResult(ser);
//			}
//		}
//		ret.setFinished();
//
//		return ret;
//	}
	
	/**
	 *  Get the managed services, if this platform is a super peer (i.e. has an ISuperpeerService).
	 *  @return Service events for a self-updating list of services.
	 */
	public ISubscriptionIntermediateFuture<ServiceEvent> subscribeToServices()
	{
		// Returns the view of the own platform, i.e. the queries managed by this platform
		
		ISubscriptionIntermediateFuture<ServiceEvent> ret;
		
		try
		{
			ISuperpeerService sps = agent.getLocalService(new ServiceQuery<>(ISuperpeerService.class));
			ret = sps.addQuery(new ServiceQuery<>((Class<?>)null)
				.setEventMode()
				.setOwner(agent.getId())
				.setNetworkNames((String[])null)
				.setScope(ServiceScope.GLOBAL));
		}
		catch(Exception e)
		{
			// Get local services when superpeer mode is disabled
			
			ret = agent.addQuery(new ServiceQuery<>((Class<?>)null)
				.setEventMode()
				.setOwner(agent.getId())
				.setNetworkNames((String[])null)
				.setScope(ServiceScope.PLATFORM));
		}

		SubscriptionIntermediateDelegationFuture<ServiceEvent> fut = new SubscriptionIntermediateDelegationFuture<ServiceEvent>(ret);
		
//		IIntermediateResultListener<ServiceEvent<IServiceIdentifier>> lis = new IIntermediateResultListener<ServiceEvent<IServiceIdentifier>>()
//		{
//			public void intermediateResultAvailable(ServiceEvent<IServiceIdentifier> result)
//			{
//				System.out.println("ival: "+result);
//			}
//			
//		    public void finished()
//		    {
//		    }
//		    
//		    public void resultAvailable(Collection<ServiceEvent<IServiceIdentifier>> result)
//		    {
//		    }
//		    
//		    public void exceptionOccurred(Exception exception)
//		    {
//		    }
//		};
//		fut.addQuietListener(lis);
		
		SFuture.avoidCallTimeouts(fut, agent);
		
		return fut;
	}
	
	/**
	 *  Get all memory stats. cf IMemstatService
	 */
	// No intermediate for easier REST?
	public IFuture<Collection<Map<String, Object>>>	getMemInfo()
	{
		Collection<IMemstatService>	stats	= agent.getFeature(IRequiredServicesFeature.class).getLocalServices(new ServiceQuery<>(IMemstatService.class, ServiceScope.PLATFORM));
		FutureBarrier<Map<String, Object>>	fubar	= new FutureBarrier<Map<String,Object>>();
		for(IMemstatService stat: stats)
		{
			fubar.addFuture(stat.getMemInfo());
		}
		
		return fubar.waitForResultsIgnoreFailures(null);
	}
}
