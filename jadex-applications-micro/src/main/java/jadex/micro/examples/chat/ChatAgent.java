package jadex.micro.examples.chat;

import jadex.micro.MicroAgent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Chat micro agent. 
 */
@Description("This agent offers a chat service.")
@ProvidedServices(@ProvidedService(type=IChatService.class, implementation=@Implementation(ChatService.class)))
@RequiredServices(
	@RequiredService(name="chatservices", type=IChatService.class, multiple=true,
		binding=@Binding(dynamic=true, scope=Binding.SCOPE_GLOBAL))
)
public class ChatAgent extends MicroAgent
{
}
