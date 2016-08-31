package jadex.micro.examples.eliza;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.commons.LazyResource;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
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


/**
 *  This agent implements a simple chat bot
 *  based on Eliza code from http://www.jesperjuul.net/eliza/
 */
@Agent
@ComponentTypes(@ComponentType(name="chat", filename="jadex/platform/service/chat/ChatAgent.class"))	// Hack!!! Implicit dependency to jadex-platform
@Configurations(@Configuration(name="default", components=@Component(type="chat", arguments=@NameValue(name="nosave", value="true"), configuration="user")))
@RequiredServices(@RequiredService(name="chat", type=IChatGuiService.class))
public class ElizaAgent
{
	//-------- attributes --------
	
	/** The eliza agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The gui service for controlling the inner chat component. */
	@AgentService
	protected IChatGuiService	chat;
	
	/** Each contact gets its own eliza conversation. */
	protected Map<IComponentIdentifier, Tuple2<ElizaParse, Long>>	conversations;
	
	//-------- methods --------
	
	/**
	 *  Register to inner chat at startup.
	 */
	@AgentCreated
	public void	start()
	{
		this.conversations	= new HashMap<IComponentIdentifier, Tuple2<ElizaParse,Long>>();
		
		chat.setNickName("Eliza").get();
		// Status get() comes back when all receivers have acknowledge their receipt of the info :-(
		chat.status(IChatService.STATE_IDLE, null, new IComponentIdentifier[0]);	// Change state from away to idle. 
		try
		{
			chat.setImage(new LazyResource(ElizaAgent.class, "images/eliza.png").getData()).get();
		}
		catch(IOException e)
		{
		}
		
		final IComponentIdentifier self = ((IService)chat).getServiceIdentifier().getProviderId();
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
						conversations.put(event.getComponentIdentifier(), new Tuple2<ElizaParse, Long>(eliza, Long.valueOf(System.currentTimeMillis())));
						
						eliza.handleLine(s);
						writeToLog(s, event.getComponentIdentifier());
						while(!eliza.msg.isEmpty())
						{
							chat.message((String)eliza.msg.elementAt(0), new IComponentIdentifier[]{event.getComponentIdentifier()}, true);
							writeToLog("> "+eliza.msg.elementAt(0), event.getComponentIdentifier());
							eliza.msg.removeElementAt(0);
						}
					}
					else if(s.toLowerCase().indexOf("eliza")!=-1)
					{
						writeToLog(s, event.getComponentIdentifier());
						chat.message("Hi! I'm the famous Eliza program. Please tell me your problem in private.", new IComponentIdentifier[]{event.getComponentIdentifier()}, true);
						writeToLog("> "+"Hi! I'm the famous Eliza program. Please tell me your problem in private.", event.getComponentIdentifier());
					}
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// ignore... (e.g. FutureTerminationException on exit)
			}
		});
		
		
		// Regularly clean up old conversations after 5 minutes of inactivity.
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(60000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				for(Iterator<IComponentIdentifier> it=conversations.keySet().iterator(); it.hasNext(); )
				{
					Tuple2<ElizaParse, Long>	tup	= conversations.get(it.next());
					if(tup.getSecondEntity().longValue()+30000<System.currentTimeMillis())
					{
						it.remove();
					}
				}
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Write logs of conversations.
	 *  @param text	The text to write to the log.
	 *  @param cid	The component identifier of the chat partner.
	 */
	protected void	writeToLog(String text, IComponentIdentifier partner)
	{
		try
		{
			File	dir	= new File(System.getProperty("user.home"), ".eliza");
			File	f	= new File(dir, partner.getName()+".txt");
			f.getParentFile().mkdirs();
			PrintStream	os	= new PrintStream(new FileOutputStream(f, true));
			os.println(text);
			os.flush();
			os.close();
		}
		catch(Exception e)
		{
			// ignore.
		}
	}
}
