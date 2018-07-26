package jadex.platform.service.df;


import jadex.bridge.service.types.df.IDF;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Autostart;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;

/**
 *  Agent that provides the DF service.
 */
@Agent(autostart=@Autostart(name="df", value=Boolean3.FALSE))
@ProvidedServices(@ProvidedService(type=IDF.class, scope=RequiredService.SCOPE_PLATFORM, implementation=@Implementation(DirectoryFacilitatorService.class)))
public class DirectoryFacilitatorAgent
{
}
