package jadex.tools.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQueryInfo;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.memstat.IMemstatService;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.bridge.service.types.registryv2.ISuperpeerService;
import jadex.bridge.service.types.transport.ITransportInfoService;
import jadex.bridge.service.types.transport.PlatformData;
import jadex.commons.Boolean3;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Autostart;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Publish;

/**
 *  An agent to provide a platform status view in the web.
 */
@ProvidedServices(
	@ProvidedService(name="status", type=IStatusService.class,
		scope=ServiceScope.PLATFORM,
		implementation=@Implementation(expression="$pojoagent"),
		publish=@Publish(publishtype=IPublishService.PUBLISH_RS, publishid="[http://localhost:8081/]status"
//		properties={
//			@NameValue(name="formats", value="new javax.ws.rs.core.MediaType[]{javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE, javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE}")
//		}
	))
)
// TODO: service dependencies instead of predecessors
@Agent(autostart=@Autostart(value=Boolean3.FALSE, predecessors="jadex.extension.rs.publish.JettyRSPublishAgent"))
public class StatusAgent implements IStatusService
{
	@Agent
	protected IInternalAccess	agent;
	
	@AgentCreated
	protected IFuture<Void>	setup()
	{
		IWebPublishService	wps	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IWebPublishService.class));
		return wps.publishResources("[http://localhost:8081/]", "META-INF/resources");
	}
	
	@Override
	public IIntermediateFuture<PlatformData>	getConnectedPlatforms()
	{
		final IntermediateFuture<PlatformData>	ret	= new IntermediateFuture<PlatformData>();
		FutureBarrier<Collection<PlatformData>>	fubar	= new FutureBarrier<Collection<PlatformData>>();
		for(ITransportInfoService tis: agent.getFeature(IRequiredServicesFeature.class).searchLocalServices(new ServiceQuery<>(ITransportInfoService.class)))
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
	public ISubscriptionIntermediateFuture<PlatformData>	subscribeToConnections()
	{
		final List<ISubscriptionIntermediateFuture<PlatformData>>	futs	= new ArrayList<ISubscriptionIntermediateFuture<PlatformData>>();
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
		for(final ITransportInfoService tis: agent.getFeature(IRequiredServicesFeature.class).searchLocalServices(new ServiceQuery<>(ITransportInfoService.class)))
		{
			ISubscriptionIntermediateFuture<PlatformData>	fut	= tis.subscribeToConnections();
			fut.addResultListener(new IIntermediateResultListener<PlatformData>()	// Do not use delegation listener (ignore forward commands like update timer)
			{
				@Override
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
				}
				
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
		IServiceRegistry	reg	= ServiceRegistry.getRegistry(agent.getId());
		for(ServiceQueryInfo<?> sqi: reg.getAllQueries())
		{
			if(scopes==null || scopes.contains(sqi.getQuery().getScope()))
			{
				ret.addIntermediateResult(sqi.getQuery());
			}
		}
		ret.setFinished();

		return ret;
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
	public ISubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>>	subscribeToServices()
	{
		ISuperpeerService	sps	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISuperpeerService.class));
		return sps.addQuery(new ServiceQuery<>((Class<IServiceIdentifier>)null)
			.setEventMode()
			.setOwner(agent.getId())
			.setNetworkNames((String[])null)
			.setScope(ServiceScope.GLOBAL));
	}
	
	/**
	 *  Get all memory stats. cf IMemstatService
	 */
	// No intermediate for easier REST?
	public IFuture<Collection<Map<String, Object>>>	getMemInfo()
	{
		Collection<IMemstatService>	stats	= agent.getFeature(IRequiredServicesFeature.class).searchLocalServices(new ServiceQuery<>(IMemstatService.class, ServiceScope.PLATFORM));
		FutureBarrier<Map<String, Object>>	fubar	= new FutureBarrier<Map<String,Object>>();
		for(IMemstatService stat: stats)
		{
			fubar.addFuture(stat.getMemInfo());
		}
		
		return fubar.waitForResultsIgnoreFailures(null);
	}


	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimalComm();
		config.addComponent(StatusAgent.class);
//		config.setLogging(true);

//		config.getExtendedPlatformConfiguration().setRsPublish(true);
		config.setValue("jettyrspublish", true);
		
		Starter.createPlatform(config).get();
	}
}
