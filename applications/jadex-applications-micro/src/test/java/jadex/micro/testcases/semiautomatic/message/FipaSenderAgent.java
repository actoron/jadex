package jadex.micro.testcases.semiautomatic.message;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.FipaMessage;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;

/**
 *  An agent that sends a FIPA message.
 */
@Agent
public class FipaSenderAgent
{
	/** The receiver. */
	@AgentArgument
	protected IComponentIdentifier	receiver	= IComponentIdentifier.LOCAL.get();
	
	/**
	 *  Send the message and wait for the result.
	 */
	@AgentBody
	protected IFuture<Void>	run(IMessageFeature mf)
	{
		FipaMessage	request	= new FipaMessage(FipaMessage.Performative.REQUEST, "Hello?", receiver);
		FipaMessage	reply	= (FipaMessage)mf.sendMessageAndWait(null, request).get();
		System.out.println("Sender received: "+reply);
		
		return IFuture.DONE;
	} 
}
