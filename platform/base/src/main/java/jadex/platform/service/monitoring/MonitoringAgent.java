package jadex.platform.service.monitoring;


import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Default monitoring agent. 
 */
@Agent(predecessors="jadex.platform.service.settings.SettingsAgent",
	autostart=Boolean3.FALSE)
@ProvidedServices(@ProvidedService(type=IMonitoringService.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(MonitoringService.class)))
//@Properties(value=@NameValue(name="system", value="true"))
public class MonitoringAgent
{
}
