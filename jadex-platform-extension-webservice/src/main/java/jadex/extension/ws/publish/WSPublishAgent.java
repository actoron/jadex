package jadex.extension.ws.publish;

import jadex.bridge.service.types.publish.IPublishService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that publishes the ws publication service.
 */
@Agent
@ProvidedServices(
{
	@ProvidedService(name="publish_ws", type=IPublishService.class, 
		implementation=@Implementation(DefaultWebServicePublishService.class))
})
public class WSPublishAgent
{
}

