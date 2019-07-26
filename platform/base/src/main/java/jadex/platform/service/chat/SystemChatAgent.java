package jadex.platform.service.chat;

import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.platform.service.ISystemService;

// todo: add system flag to agent
@Agent(autostart=Boolean3.TRUE, name="chat", autoprovide=Boolean3.TRUE)
public class SystemChatAgent extends ChatAgent implements ISystemService
{

}
