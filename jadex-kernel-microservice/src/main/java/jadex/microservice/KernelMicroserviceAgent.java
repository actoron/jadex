package jadex.microservice;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Microservice kernel.
 */
@Properties({
	@NameValue(name="system", value="true"),
	@NameValue(name="kernel.types", value="new String[] { \"Microservice.class\"}")
})
@ProvidedServices({
	@ProvidedService(type=IComponentFactory.class, implementation=@Implementation(expression="new jadex.microservice.MicroserviceFactory($component, null)"))
})
@Agent
public class KernelMicroserviceAgent
{
}
