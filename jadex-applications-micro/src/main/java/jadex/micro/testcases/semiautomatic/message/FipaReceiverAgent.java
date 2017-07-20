package jadex.micro.testcases.semiautomatic.message;

import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.fipa.FipaMessage;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentMessageArrived;

/**
 *  An agent that replies to a FIPA message.
 */
@Agent
public class FipaReceiverAgent
{	
	/**
	 *  Wait for the message and reply.
	 */
	@AgentMessageArrived
	protected void	received(FipaMessage message, IMessageFeature mf, IMsgHeader header)
	{
		System.out.println("Receiver received: "+message);
		
		FipaMessage	reply	= new FipaMessage();
		reply.setPerformative(FipaMessage.Performative.INFORM);
		reply.setContent(message.getContent()+" World!");
		
		mf.sendReply(header, reply);
	}
}
