package org.activecomponents.examples.puzzleng;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentServiceQuery;

/**
 *  Makes the web puzzler available by providing the
 *  web resources such as index.html via IWebPublishService.
 *  
 *  Communication is handled via the websocket channel of the
 *  web server.
 */
@Agent
public class PuzzleStarterAgent
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Wait for the IWebPublishService and then publish the resources.
	 *  @param pubser The publish service.
	 */
	@AgentServiceQuery
	protected void publish(IWebPublishService wps)
	{
		// does not need to create a puzzle agent as it uses session scope
		
		//System.out.println("publish started: "+pubser);
		//IWebPublishService	wps	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IWebPublishService.class));
		wps.publishResources("[http://localhost:8081/]wswebapi", "org/activecomponents/webservice").get();
		wps.publishResources("[http://localhost:8081/]", "/").get();
	}
}