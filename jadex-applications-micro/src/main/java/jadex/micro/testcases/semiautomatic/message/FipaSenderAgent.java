package jadex.micro.testcases.semiautomatic.message;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.FipaMessage;
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
	@AgentArgument(convert="$value!=null ? $value : $component.getComponentIdentifier()")
	protected IComponentIdentifier	receiver;
	
	/**
	 *  Send the message and wait for the result.
	 */
	@AgentBody
	protected void	run(IMessageFeature mf)
	{
		FipaMessage	request	= new FipaMessage(receiver, FipaMessage.Performative.REQUEST, "Hello?");
		FipaMessage	reply	= (FipaMessage)mf.sendMessageAndWait(null, request).get();
		System.out.println("Sender received: "+reply);
	} 
}
