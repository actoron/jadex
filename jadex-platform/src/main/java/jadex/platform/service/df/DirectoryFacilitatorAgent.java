package jadex.platform.service.df;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.df.IDF;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides the DF service.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IDF.class, implementation=@Implementation(DirectoryFacilitatorService.class)))
@Properties(value=@NameValue(name="system", value="true"))
public class DirectoryFacilitatorAgent
{
}
