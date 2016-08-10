package jadex.platform.service.registry;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.registry.IRegistrySynchronizationService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Default registry synchronization agent. 
 */
@Agent
@ProvidedServices(@ProvidedService(type=IRegistrySynchronizationService.class, implementation=@Implementation(RegistrySynchronizationService.class)))
@Properties(value=@NameValue(name="system", value="true"))
public class RegistrySynchronizationAgent
{
}
