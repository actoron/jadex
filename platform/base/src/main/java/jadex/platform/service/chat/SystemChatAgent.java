package jadex.platform.service.chat;

import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.platform.service.ISystemService;

@Agent(autostart=Boolean3.TRUE, autoprovide=Boolean3.TRUE,
	predecessors="jadex.platform.service.registryv2.SuperpeerClientAgent")
public class SystemChatAgent extends ChatAgent implements ISystemService
{

}
