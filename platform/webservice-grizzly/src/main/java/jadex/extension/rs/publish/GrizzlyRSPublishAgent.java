package jadex.extension.rs.publish;


import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that publishes the rs publication service.
 */
@Agent(autostart=Boolean3.FALSE)
@ProvidedServices(
{
	@ProvidedService(name="publish_rs", type=IWebPublishService.class, 
		implementation=@Implementation(GrizzlyRestPublishService.class))
//		implementation=@Implementation(GrizzlyRestServicePublishService.class))
})
@Properties(@NameValue(name="system", value="true"))
public class GrizzlyRSPublishAgent
{
}

