package jadex.micro.examples.ping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentMessageArrived;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;

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
@Agent
public class PingingAgent 
{	
	//-------- attributes --------

	/** The micro agent class. */
	@Agent
	protected IInternalAccess agent;
	
	/** The receiver. */
	protected IComponentIdentifier receiver;
	
	/** The difference between sent messages and received replies. */
	protected int dif;
	
	/** Hashset with conversation ids of sent messages. */
	protected Set<String> sent;
	
	//-------- methods --------

	/**
	 *  Execute the body.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		receiver = (IComponentIdentifier)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("receiver");
		final int missed_max = ((Number)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("missed_max")).intValue();
		final long timeout = ((Number)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("timeout")).longValue();
		final Object content = agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("content");
		sent = new HashSet<String>();
		
		final IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(dif>missed_max)
				{
					agent.getLogger().warning("Ping target does not respond: "+receiver);
//					killAgent();
					ret.setResult(null);
				}
				else
				{
					String convid = SUtil.createUniqueId(agent.getComponentIdentifier().getName());
					Map<String, Object> msg = new HashMap<String, Object>();
					msg.put(SFipa.CONTENT, content);
					msg.put(SFipa.PERFORMATIVE, SFipa.QUERY_IF);
					msg.put(SFipa.CONVERSATION_ID, convid);
					msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{receiver});
//					msg.put(SFipa.SENDER, getComponentIdentifier());
					dif++;
					sent.add(convid);
					agent.getComponentFeature(IMessageFeature.class).sendMessage(msg, receiver);
					agent.getComponentFeature(IExecutionFeature.class).waitForDelay(timeout, this);
				}
				return IFuture.DONE;
			}
		};
		
		if(receiver==null)
		{
			receiver = new BasicComponentIdentifier("Ping", agent.getComponentIdentifier().getParent());
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

		agent.getComponentFeature(IExecutionFeature.class).scheduleStep(step);
		
		return ret;
	}
	
	/**
	 *  Called when a message arrives.
	 */
	@AgentMessageArrived
	public void messageArrived(Map<String, Object> msg)
	{
		String convid = (String)msg.get(SFipa.CONVERSATION_ID);
		if(sent.remove(convid))
		{
			dif = 0; // A received ping heals other outstanding pings.
			sent.clear();
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
//				new Argument("missed_max", "Maximum number of allowed missed replies", "int", Integer.valueOf(3)),
//				new Argument("timeout", "Timeout for reply", "long", new Long(1000)),
//				new Argument("content", "Ping message content", "String", "ping"),
//				
//			}, null);
//	}
}
