package jadex.micro.examples.chat;

import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.annotation.GuiClass;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Chat micro agent. 
 */
@Description("This agent offers a chat service.")
@ProvidedServices(@ProvidedService(type=IChatService.class, expression="new ChatService($component)"))
@RequiredServices(@RequiredService(name="chatservices", type=IChatService.class, 
	multiple=true, scope=RequiredServiceInfo.SCOPE_GLOBAL))
@GuiClass(ChatPanel.class)
public class ChatAgent extends MicroAgent
{
}
