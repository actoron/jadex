package jadex.micro.examples.chat;

import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.annotation.GuiClass;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Chat micro agent. 
 */
@Description("This agent offers a chat service.")
@RequiredServices(@RequiredService(name="chatservices", type=IChatService.class, 
	multiple=true, scope=RequiredServiceInfo.SCOPE_GLOBAL))
@GuiClass(ChatPanel.class)
public class ChatAgent extends MicroAgent
{
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		ChatService cs = new ChatService(this);
		addDirectService(cs);
	}
	
	//-------- static methods --------

//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static MicroAgentMetaInfo getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("This agent offers a chat service.", null, 
//			new IArgument[]{}//new Argument("infos", "Initial information records.", "InformationEntry[]")}
//			, null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.micro.examples.chat.ChatPanel"}),
//			new RequiredServiceInfo[]{
//				new RequiredServiceInfo("chatservices", IChatService.class, true, true, RequiredServiceInfo.SCOPE_GLOBAL), 
//				}, new Class[]{IChatService.class});
//	}

}
