package jadex.extension.rs.publish;

import jadex.bridge.service.types.publish.IPublishService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that publishes the rs publication service.
 */
@Agent
@ProvidedServices(
{
	@ProvidedService(name="publish_rs", type=IPublishService.class, 
		implementation=@Implementation(DefaultRestServicePublishService.class))
})
public class RSPublishAgent
{
}

