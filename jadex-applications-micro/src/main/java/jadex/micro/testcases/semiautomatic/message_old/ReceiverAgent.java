package jadex.micro.testcases.semiautomatic.message_old;

import java.util.Map;

import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentMessageArrived;

/**
 *  An agent that replies to a message.
 */
@Agent
public class ReceiverAgent
{	
	/**
	 *  Wait for the message and reply.
	 */
	@AgentMessageArrived
	protected void	received(Map<String, Object> message, IMessageFeature mf)
	{
//		System.out.println("Receiver received: "+message);
		
		Map<String, Object>	reply	= SFipa.FIPA_MESSAGE_TYPE.createReply(message);
		reply.put(SFipa.CONTENT, message.get(SFipa.CONTENT)+" World!");
		mf.sendMessage(reply, SFipa.FIPA_MESSAGE_TYPE);
	}
}
