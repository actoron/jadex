package org.activecomponents.webservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentServiceQuery;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.RequiredService;

/**
 *  Makes the web app available by providing the
 *  web resources such as index.html via IWebPublishService.
 *  
 *  Communication is handled via the websocket channel of the
 *  web server.
 */
@Agent
public class WebsocketTestStarterAgent
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Wait for the IWebPublishService and then publish the resources.
	 *  @param pubser The publish service.
	 */
	//@AgentServiceQuery
	@OnService(requiredservice = @RequiredService(min = 1, max = 1))
	protected void publish(IWebPublishService pubser)
	{
		agent.createComponent(new CreationInfo().setFilenameClass(WebsocketsTestAgent.class)).get();
		
		//System.out.println("publish started: "+pubser);
		IWebPublishService	wps	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IWebPublishService.class));
		wps.publishResources("[http://localhost:8081/]wswebapi", "org/activecomponents/webservice").get();
		wps.publishResources("[http://localhost:8081/]", "/").get();
	}
}