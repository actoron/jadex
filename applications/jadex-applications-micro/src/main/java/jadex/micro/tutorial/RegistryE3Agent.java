package jadex.micro.tutorial;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Chat micro agent with a registry service. 
 */
@Description("This agent provides a registry service.")
@Agent
@ProvidedServices(@ProvidedService(type=IRegistryServiceE3.class, 
	implementation=@Implementation(RegistryServiceE3.class)))
public class RegistryE3Agent
{
}