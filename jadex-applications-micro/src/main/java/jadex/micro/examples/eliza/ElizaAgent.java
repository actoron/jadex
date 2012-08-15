package jadex.micro.examples.eliza;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.commons.LazyResource;
import jadex.commons.Tuple2;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *  This agent implements a simple chat bot
 *  based on Eliza code from http://www.jesperjuul.net/eliza/
 */
@Agent
@ComponentTypes(@ComponentType(name="chat", filename="jadex/base/service/chat/ChatAgent.class"))	// Hack!!! Implicit dependency to jadex-platform-base
@Configurations(@Configuration(name="default", components=@Component(type="chat")))
@RequiredServices(@RequiredService(name="chat", type=IChatGuiService.class))
public class ElizaAgent
{
	//-------- attributes --------
	
	/** The gui service for controlling the inner chat component. */
	@AgentService
	protected IChatGuiService	chat;
	
	/** Each contact gets its own eliza conversation. */
	// Todo: remove old conversation (e.g. after 5 min)
	protected Map<IComponentIdentifier, Tuple2<ElizaParse, Long>>	conversations;
	
	//-------- methods --------
	
	/**
	 *  Register to inner chat at startup.
	 */
	@AgentCreated
	public void	start()
	{
		this.conversations	= new HashMap<IComponentIdentifier, Tuple2<ElizaParse,Long>>();
		
		chat.setNickName("Eliza");
		try
		{
			chat.setImage(new LazyResource(ElizaAgent.class, "images/eliza.png").getData());
		}
		catch(IOException e)
		{
		}
		
		final IComponentIdentifier	self	= ((IService)chat).getServiceIdentifier().getProviderId();
		chat.subscribeToEvents().addResultListener(new IntermediateDefaultResultListener<ChatEvent>()
		{
			public void intermediateResultAvailable(ChatEvent event)
			{
				if(ChatEvent.TYPE_MESSAGE.equals(event.getType()) && !self.equals(event.getComponentIdentifier()))
				{
					String	s	= (String)event.getValue();
					s=s.trim();
					if(event.isPrivateMessage() && s.length()>0)
					{
						Tuple2<ElizaParse, Long>	tup	= conversations.get(event.getComponentIdentifier());
						ElizaParse	eliza;
						if(tup==null)
						{
							eliza	= new ElizaParse();
						}
						else
						{
							eliza	= tup.getFirstEntity();
						}
						conversations.put(event.getComponentIdentifier(), new Tuple2<ElizaParse, Long>(eliza, new Long(System.currentTimeMillis())));
						
						eliza.handleLine(s);
						while(!eliza.msg.isEmpty())
						{
							chat.message((String)eliza.msg.elementAt(0), new IComponentIdentifier[]{event.getComponentIdentifier()}, true);
							eliza.msg.removeElementAt(0);
						}
					}
					else if(s.toLowerCase().indexOf("eliza")!=-1)
					{
						chat.message("Hi! I'm Eliza. Please tell me your problem in private.", new IComponentIdentifier[]{event.getComponentIdentifier()}, true);
					}
				}
			}
		});
	}
}
