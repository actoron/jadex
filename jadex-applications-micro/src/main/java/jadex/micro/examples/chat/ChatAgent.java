package jadex.micro.examples.chat;

import jadex.bridge.IArgument;
import jadex.commons.SUtil;
import jadex.commons.service.RequiredServiceInfo;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

/**
 *  Chat micro agent. 
 */
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
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				ChatPanel.createGui((IMicroExternalAccess)getExternalAccess());
//			}
//		});
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent offers a helpline for getting information about missing persons.", null, 
			new IArgument[]{}//new Argument("infos", "Initial information records.", "InformationEntry[]")}
			, null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.micro.examples.chat.ChatPanel"}),
			new RequiredServiceInfo[]{
				new RequiredServiceInfo("chatservices", IChatService.class, true, true, true, true), 
				}, new Class[]{IChatService.class});
	}

}
