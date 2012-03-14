package jadex.micro.examples.ping;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Agent that pings another and waits for its replies.
 */
@Description("A simple agent that sends pings to another agent and waits for replies.")
@Arguments({
	@Argument(name="receiver", clazz=IComponentIdentifier.class, description="The component receiver of the ping target."),
	@Argument(name="missed_max", clazz=int.class, description="Maximum number of allowed missed replies", defaultvalue="3"),
	@Argument(name="timeout", clazz=long.class, description="Timeout for reply", defaultvalue="1000"),
	@Argument(name="content", clazz=String.class, description="Ping message content", defaultvalue="\"ping\"")
})
public class PingingAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The receiver. */
	protected IComponentIdentifier receiver;
	
	/** The difference between sent messages and received replies. */
	protected int dif;
	
	/** Hashset with conversation ids of sent messages. */
	protected Set sent;
	
	//-------- methods --------

	/**
	 *  Execute the body.
	 */
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		receiver = (IComponentIdentifier)getArgument("receiver");
		final int missed_max = ((Number)getArgument("missed_max")).intValue();
		final long timeout = ((Number)getArgument("timeout")).longValue();
		final Object content = getArgument("content");
		sent = new HashSet();
		
		final IComponentStep step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(dif>missed_max)
				{
					getLogger().warning("Ping target does not respond: "+receiver);
//					killAgent();
					ret.setResult(null);
				}
				else
				{
					String convid = SUtil.createUniqueId(getAgentName());
					Map msg = new HashMap();
					msg.put(SFipa.CONTENT, content);
					msg.put(SFipa.PERFORMATIVE, SFipa.QUERY_IF);
					msg.put(SFipa.CONVERSATION_ID, convid);
					msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{receiver});
//					msg.put(SFipa.SENDER, getComponentIdentifier());
					dif++;
					sent.add(convid);
					sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE);
					waitFor(timeout, this);
				}
				return IFuture.DONE;
			}
		};
		
		if(receiver==null)
		{
			receiver = new ComponentIdentifier("Ping", getComponentIdentifier().getParent());
		}
//			createComponentIdentifier("Ping").addResultListener(new DefaultResultListener()
//			{
//				public void resultAvailable(Object result)
//				{
//					receiver = (IComponentIdentifier)result;
//					scheduleStep(step);
//				}
//			});
//		}
//		else
//		{
//			scheduleStep(step);
//		}

		scheduleStep(step);
		
		return ret;
	}
	
	/**
	 *  Called when a message arrives.
	 */
	public void messageArrived(Map msg, MessageType mt)
	{
		if(mt.equals(SFipa.FIPA_MESSAGE_TYPE))
		{
			String convid = (String)msg.get(SFipa.CONVERSATION_ID);
			if(sent.remove(convid))
			{
				dif = 0; // A received ping heals other outstanding pings.
				sent.clear();
			}
		}
	}
	
//	/**
//	 *  Get the agent meta info. 
//	 */
//	public static Object getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("A simple agent that sends pings to another agent and waits for replies.", 
//			null, new IArgument[]
//			{
//				new Argument("receiver", "The component receiver of the ping target.", "IComponentIdentifier"),
//				new Argument("missed_max", "Maximum number of allowed missed replies", "int", new Integer(3)),
//				new Argument("timeout", "Timeout for reply", "long", new Long(1000)),
//				new Argument("content", "Ping message content", "String", "ping"),
//				
//			}, null);
//	}
}
