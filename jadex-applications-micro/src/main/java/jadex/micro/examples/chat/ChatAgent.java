package jadex.micro.examples.chat;

import jadex.bridge.IArgument;
import jadex.commons.SUtil;
import jadex.micro.IMicroExternalAccess;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import javax.swing.SwingUtilities;

/**
 *  Chat micro agent. 
 */
public class ChatAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The chat service. */
	protected ChatService cs;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		cs = new ChatService(getExternalAccess());
		addDirectService(cs);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				ChatPanel.createGui((IMicroExternalAccess)getExternalAccess());
			}
		});
	}
	
	/**
	 *  Get the chat service.
	 */
	public ChatService getChatService()
	{
		return cs;
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
			null, new Class[]{IChatService.class});
	}

}
