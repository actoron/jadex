package jadex.tools.web;

import java.util.Collection;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.bridge.service.types.transport.ITransportInfoService;
import jadex.commons.SReflect;
import jadex.commons.Tuple3;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
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
	public IIntermediateFuture<Tuple3<IComponentIdentifier,String,Boolean>>	getConnectedPlatforms()
	{
		final IntermediateFuture<Tuple3<IComponentIdentifier,String,Boolean>>	ret	= new IntermediateFuture<Tuple3<IComponentIdentifier,String,Boolean>>();
		FutureBarrier<Collection<Tuple3<IComponentIdentifier, String, Boolean>>>	fubar	= new FutureBarrier<Collection<Tuple3<IComponentIdentifier, String, Boolean>>>();
		for(ITransportInfoService tis: SServiceProvider.getLocalServices(agent, ITransportInfoService.class))
		{
			IIntermediateFuture<Tuple3<IComponentIdentifier, String, Boolean>>	fut	= tis.getConnections();
			fut.addIntermediateResultListener(new IntermediateDelegationResultListener<Tuple3<IComponentIdentifier,String,Boolean>>(ret)
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

	
	public static void main(String[] args)
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimalComm();
		config.addComponent(StatusAgent.class);
//		config.setLogging(true);
		config.getExtendedPlatformConfiguration().setRsPublish(true);
		Starter.createPlatform(config).get();
	}
}
