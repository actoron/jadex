package jadex.base.service.chat;

import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
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
@ProvidedServices({
	@ProvidedService(name="chat", type=IChatService.class, implementation=@Implementation(ChatService.class)),
	@ProvidedService(type=IChatGuiService.class, implementation=@Implementation(expression="$component.getRawService(\"chat\")"))
})
@RequiredServices(
	@RequiredService(name="chatservices", type=IChatService.class, multiple=true,
		binding=@Binding(dynamic=true, scope=Binding.SCOPE_GLOBAL))
)
public class ChatAgent extends MicroAgent
{
}
