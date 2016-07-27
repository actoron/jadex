package jadex.platform.service.registry;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.bridge.service.types.registry.IRegistryService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Default registry agent. 
 */
@Agent
@ProvidedServices(@ProvidedService(type=IRegistryService.class, implementation=@Implementation(RegistryService.class)))
@Properties(value=@NameValue(name="system", value="true"))
public class RegistryAgent
{
}
