package jadex.micro.examples.eliza;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;


/**
 *  This agent implements a chat bot
 *  that simply echoes any privately sent message.
 *  It connects itself to the platform chat service.
 */
@Agent
@ComponentTypes(@ComponentType(name="chat", filename="jadex/platform/service/chat/ChatAgent.class"))	// Hack!!! Implicit dependency to jadex-platform
@Configurations({
	@Configuration(name="intern", components=@Component(type="chat", arguments=@NameValue(name="nosave", value="true"), configuration="user")),
	@Configuration(name="extern")
})
@RequiredServices({
	@RequiredService(name="chat_intern", type=IChatGuiService.class),
	@RequiredService(name="chat_extern", type=IChatGuiService.class, scope=RequiredService.SCOPE_PLATFORM)
})
public class EchoChatAgent
{
	//-------- attributes --------
	
	/** The eliza agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The gui service for controlling the inner chat component. */
	@AgentServiceSearch(name="%{\"chat_\"+$config}")
	protected IChatGuiService	chat;
	
	//-------- methods --------
	
	/**
	 *  Register to inner chat at startup.
	 */
	@AgentCreated
	public void	start()
	{
		chat.status(IChatService.STATE_IDLE, null, new IComponentIdentifier[0]);	// Change state from away to idle.
		
		chat.setNickName("Echo").get();

//		try
//		{
//			chat.setImage(new LazyResource(EchoChatAgent.class, "images/eliza.png").getData());
//		}
//		catch(IOException e)
//		{
//		}
		
		final IComponentIdentifier	self = ((IService)chat).getServiceId().getProviderId();
		chat.subscribeToEvents().addResultListener(new IntermediateDefaultResultListener<ChatEvent>()
		{
			public void intermediateResultAvailable(ChatEvent event)
			{
				if(ChatEvent.TYPE_MESSAGE.equals(event.getType()) && !self.equals(event.getComponentIdentifier())
					&& event.isPrivateMessage())
				{
					String	s	= (String)event.getValue();
					s=s.trim();
					if(s.length()>0)
					{
						chat.message(s, new IComponentIdentifier[]{event.getComponentIdentifier()}, true);
					}
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// ignore... (e.g. FutureTerminationException on exit)
			}
		});		
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		IExternalAccess pl = Starter.createPlatform(new String[]{"-gui", "false"}).get();		
		for(int i=0; i<10000; i++)
		{
			System.out.print(".");
			if(i%100==0)
				System.out.println("\n "+i+": ");
			IComponentIdentifier cid = pl.createComponent(new CreationInfo().setFilename(EchoChatAgent.class.getName()+".class")).getFirstResult();
			try
			{
				pl.killComponent(cid).get();
			}
			catch(Exception e)
			{
				System.out.println("Ex: "+e.getMessage());
			}
		}
		
//		try
//		{
//			Thread.currentThread().sleep(30000);
//		}
//		catch(Exception e)
//		{
//		}
		
		System.out.println("fini");
	}
}
