package jadex.micro.examples.ping;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentMessageArrived;

/**
 *  Answer ping requests. 
 */
@Agent
public class PingAgent 
{
	/** The micro agent class. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Send a reply to the sender.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	@AgentMessageArrived
	public void messageArrived(Map<String, Object> msg)
	{
		String perf = (String)msg.get(SFipa.PERFORMATIVE);
		if((SFipa.QUERY_IF.equals(perf) || SFipa.QUERY_REF.equals(perf)) 
			&& "ping".equals(msg.get(SFipa.CONTENT)))
		{
			// transform to reply
			Map<String, Object> reply = msg;
			reply.put(SFipa.RECEIVERS, msg.get(SFipa.REPLY_TO)!=null ?  msg.get(SFipa.REPLY_TO) : msg.get(SFipa.SENDER));
			reply.put(SFipa.IN_REPLY_TO, msg.get(SFipa.REPLY_WITH));
			reply.remove(SFipa.REPLY_WITH);
			reply.remove(SFipa.REPLY_TO);
			
			reply.put(SFipa.CONTENT, "alive");
			reply.put(SFipa.PERFORMATIVE, SFipa.INFORM);
			reply.put(SFipa.SENDER, agent.getId());
			agent.getFeature(IMessageFeature.class).sendMessage(reply, (IComponentIdentifier)reply.get(SFipa.RECEIVERS));
		}
		else
		{
			agent.getLogger().severe("Could not process message: "+msg);
		}
	}
}
