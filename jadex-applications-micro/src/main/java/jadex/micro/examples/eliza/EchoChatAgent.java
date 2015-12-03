package jadex.micro.examples.eliza;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;


/**
 *  This agent implements a chat bot
 *  that simply echoes any privately sent message.
 *  It connects itself to the platform chat service.
 */
@Agent
@RequiredServices(@RequiredService(name="chat", type=IChatGuiService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)))
public class EchoChatAgent
{
	//-------- attributes --------
	
	/** The eliza agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The gui service for controlling the inner chat component. */
	@AgentService
	protected IChatGuiService	chat;
	
	//-------- methods --------
	
	/**
	 *  Register to inner chat at startup.
	 */
	@AgentCreated
	public void	start()
	{
		chat.status(IChatService.STATE_IDLE, null, new IComponentIdentifier[0]);	// Change state from away to idle.
//		chat.setNickName("Echo");
//		try
//		{
//			chat.setImage(new LazyResource(EchoChatAgent.class, "images/eliza.png").getData());
//		}
//		catch(IOException e)
//		{
//		}
		
		final IComponentIdentifier	self	= ((IService)chat).getServiceIdentifier().getProviderId();
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
		ThreadSuspendable sus = new ThreadSuspendable();
		IExternalAccess pl = Starter.createPlatform(new String[]{"-gui", "false", "-autoshutdown", "false"}).get();
		IComponentManagementService cms = SServiceProvider.getService(pl, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		
		for(int i=0; i<10000; i++)
		{
			System.out.print(".");
			if(i%100==0)
				System.out.println("\n "+i+": ");
			IComponentIdentifier cid = cms.createComponent(EchoChatAgent.class.getName()+".class", null).getFirstResult();
			try
			{
				cms.destroyComponent(cid).get();
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
