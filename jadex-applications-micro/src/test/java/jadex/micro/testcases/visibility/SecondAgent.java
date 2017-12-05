package jadex.micro.testcases.visibility;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides a service only within the own platform.
 */
@Agent
@ProvidedServices(@ProvidedService(type = IMessageService.class, implementation = @Implementation(MessageService.class), scope = RequiredServiceInfo.SCOPE_PLATFORM))
public class SecondAgent 
{
}
