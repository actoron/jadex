package jadex.tools.web;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.bridge.service.types.transport.PlatformData;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Publish;

@ProvidedServices(@ProvidedService(name="webjcc", type=IWebJCCService.class,
		scope=ServiceScope.PLATFORM,
		//implementation=@Implementation(expression="$pojoagent"),
		publish=@Publish(publishtype=IPublishService.PUBLISH_RS, publishid="[http://localhost:8080/]webjcc"
	))
)
@Agent(autostart=Boolean3.TRUE,
	predecessors="jadex.extension.rs.publish.JettyRSPublishAgent") // Hack! could be other publish agent :-(
public class JCCWebAgent 
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentCreated
	protected IFuture<Void>	setup()
	{
		IWebPublishService wps = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IWebPublishService.class));
		return wps.publishResources("[http://localhost:8080/]", "META-INF/resources2");
	}
	
	/**
	 *  Get the established connections.
	 *  @return A list of connections.
	 */
	public IFuture<Collection<IComponentIdentifier>> getPlatforms()
	{
		agent.addQuery(new ServiceQuery<>(IExternalAccess.class));
		return null;
	}
	
	/**
	 *  Get events about known platforms.
	 *  @return Events for platforms.
	 */
	public ISubscriptionIntermediateFuture<PlatformData> subscribeToPlatforms()
	{
		return null;
	}

}	
