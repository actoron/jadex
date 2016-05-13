package jadex.micro.examples.ping;

import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.MessageType;
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
	public void messageArrived(Map<String, Object> msg, final MessageType mt)
	{
		String perf = (String)msg.get(SFipa.PERFORMATIVE);
		if((SFipa.QUERY_IF.equals(perf) || SFipa.QUERY_REF.equals(perf)) 
			&& "ping".equals(msg.get(SFipa.CONTENT)))
		{
			Map<String, Object> reply = mt.createReply(msg);
			reply.put(SFipa.CONTENT, "alive");
			reply.put(SFipa.PERFORMATIVE, SFipa.INFORM);
			reply.put(SFipa.SENDER, agent.getComponentIdentifier());
			agent.getComponentFeature(IMessageFeature.class).sendMessage(reply, mt);
		}
		else
		{
			agent.getLogger().severe("Could not process message: "+msg);
		}
	}
}
