package jadex.micro.tutorial;

import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.clock.IClockService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Chat micro agent with a . 
 */
@Description("This agent provides a basic chat service.")
@Agent
@ProvidedServices(@ProvidedService(type=IChatService.class, 
	implementation=@Implementation(value=ChatServiceD5.class)))
@RequiredServices({
	@RequiredService(name="clockservice", type=IClockService.class),
	@RequiredService(name="chatservices", type=IChatService.class, multiple=true, scope=ServiceScope.GLOBAL)
})
public class ChatD5Agent
{
}