package jadex.tools.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQueryInfo;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.bridge.service.types.transport.ITransportInfoService;
import jadex.bridge.service.types.transport.PlatformData;
import jadex.commons.SReflect;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Publish;

@ProvidedServices(
	@ProvidedService(name="status", type=IStatusService.class, implementation=@Implementation(expression="$pojoagent"),
		publish=@Publish(publishtype=IPublishService.PUBLISH_RS, publishid="[http://localhost:8081/]status"
//		properties={
//			@NameValue(name="formats", value="new javax.ws.rs.core.MediaType[]{javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE, javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE}")
//		}
	))
)
@Agent
public class StatusAgent implements IStatusService
{
	@Agent
	protected IInternalAccess	agent;
	
	@AgentCreated
	protected IFuture<Void>	setup()
	{
		IWebPublishService	wps	= SServiceProvider.getLocalService(agent, IWebPublishService.class);
		return wps.publishResources("[http://localhost:8081/]", SReflect.getPackageName(getClass()).replace(".", "/")+"/static_content");
	}
	
	@Override
	public IIntermediateFuture<PlatformData>	getConnectedPlatforms()
	{
		final IntermediateFuture<PlatformData>	ret	= new IntermediateFuture<PlatformData>();
		FutureBarrier<Collection<PlatformData>>	fubar	= new FutureBarrier<Collection<PlatformData>>();
		for(ITransportInfoService tis: SServiceProvider.getLocalServices(agent, ITransportInfoService.class))
		{
			IIntermediateFuture<PlatformData>	fut	= tis.getConnections();
			fut.addIntermediateResultListener(new IntermediateDelegationResultListener<PlatformData>(ret)
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
				for(ISubscriptionIntermediateFuture<PlatformData> fut: futs)
				{
					fut.terminate();
				}
			}
		});
		for(ITransportInfoService tis: SServiceProvider.getLocalServices(agent, ITransportInfoService.class))
		{
			ISubscriptionIntermediateFuture<PlatformData>	fut	= tis.subscribeToConnections();
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
		IServiceRegistry	reg	= ServiceRegistry.getRegistry(agent.getComponentIdentifier());
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

	
	public static void main(String[] args)
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimalComm();
		config.addComponent(StatusAgent.class);
//		config.setLogging(true);
		config.getExtendedPlatformConfiguration().setRsPublish(true);
		Starter.createPlatform(config).get();
	}
}
