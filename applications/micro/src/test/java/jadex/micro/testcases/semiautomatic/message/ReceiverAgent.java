package jadex.micro.testcases.semiautomatic.message;

import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentMessageArrived;
import jadex.micro.annotation.OnMessage;

/**
 *  An agent that replies to a message.
 */
@Agent
public class ReceiverAgent
{	
	/**
	 *  Wait for the message and reply.
	 */
	//@AgentMessageArrived
	@OnMessage
	protected void	received(String message, IMessageFeature mf, IMsgHeader header)
	{
//		System.out.println("Receiver received: "+message);
		
		mf.sendReply(header, message+" World!");
	}
}
