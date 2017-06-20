package jadex.platform.service.settings;


import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides the settings service.
 */
@Agent
@ProvidedServices(@ProvidedService(type=ISettingsService.class, implementation=@Implementation(SettingsService.class)))
//@Properties(value=@NameValue(name="system", value="true"))
public class SettingsAgent
{
}