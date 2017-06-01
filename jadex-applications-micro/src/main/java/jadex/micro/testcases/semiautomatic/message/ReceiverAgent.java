package jadex.micro.testcases.semiautomatic.message;

import java.util.Map;

import jadex.bridge.component.IMessageFeature;
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
	// TODO: Shouldn't require header as message id. Use user message object instead.  
	@AgentMessageArrived
	protected void	received(String message, IMessageFeature mf, Map<String, Object> id)
	{
		System.out.println("Receiver received: "+message);
		
		mf.sendReply(id, "World!");
	}
}
