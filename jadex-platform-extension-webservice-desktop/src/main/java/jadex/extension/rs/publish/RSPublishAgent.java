package jadex.extension.rs.publish;

import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that publishes the rs publication service.
 */
@Agent
@ProvidedServices(
{
	@ProvidedService(name="publish_rs", type=IWebPublishService.class, 
		implementation=@Implementation(GrizzlyRestServicePublishService.class))
//	@ProvidedService(name="publish_rs", type=IWebPublishService.class, 
//		implementation=@Implementation(JettyRestPublishService.class))
//		implementation=@Implementation(GrizzlyRestPublishService.class))
})
@Properties(@NameValue(name="system", value="true"))
public class RSPublishAgent
{
}

