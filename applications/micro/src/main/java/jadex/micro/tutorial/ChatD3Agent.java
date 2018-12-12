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
 *  Chat micro agent offers profile information. 
 */
@Description("This agent offers profile information.")
@Agent
@ProvidedServices(@ProvidedService(type=IExtendedChatService.class, 
	implementation=@Implementation(ChatServiceD3.class)))
@RequiredServices({
	@RequiredService(name="clockservice", type=IClockService.class),
	@RequiredService(name="chatservices", type=IExtendedChatService.class, multiple=true, scope=ServiceScope.PLATFORM)
})
public class ChatD3Agent
{
}
