package jadex.micro.tutorial;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.DefaultResultListener;
import jadex.micro.IPojoMicroAgent;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Chat service implementation.
 */
public class ChatServiceF4 implements IChatService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
			
	//-------- attributes --------	
	
	/**
	 *  Receives a chat message.
	 *  @param sender The sender's name.
	 *  @param text The message text.
	 */
	public void message(final String sender, final String text)
	{
		// Reply if the message contains the keyword.
		final ChatBotF4Agent	chatbot	= (ChatBotF4Agent)((IPojoMicroAgent)agent).getPojoAgent();
		if(text.toLowerCase().indexOf(chatbot.getKeyword().toLowerCase())!=-1)
		{
			agent.getServiceContainer().getRequiredServices("chatservices")
				.addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
					{
						IChatService cs = (IChatService)it.next();
						cs.message(agent.getComponentIdentifier().getName(), chatbot.getReply()+": "+sender);
					}
				}
			});
		}
	}
}
