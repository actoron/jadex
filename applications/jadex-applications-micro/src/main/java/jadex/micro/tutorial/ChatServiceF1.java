package jadex.micro.tutorial;

import java.util.Collection;
import java.util.Iterator;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;

/**
 *  Chat service implementation.
 */
@Service
public class ChatServiceF1 implements IChatService
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
		final ChatBotF1Agent	chatbot	= (ChatBotF1Agent)agent.getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
		if(text.toLowerCase().indexOf(chatbot.getKeyword().toLowerCase())!=-1)
		{
			IFuture<Collection<IChatService>> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices("chatservices");
			fut.addResultListener(new DefaultResultListener<Collection<IChatService>>()
			{
				public void resultAvailable(Collection<IChatService> result)
				{
					for(Iterator<IChatService> it=result.iterator(); it.hasNext(); )
					{
						IChatService cs = it.next();
						cs.message(agent.getComponentIdentifier().getName(), chatbot.getReply()+": "+sender);
					}
				}
			});
		}
	}
}
