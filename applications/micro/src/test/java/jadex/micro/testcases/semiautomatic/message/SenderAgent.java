package jadex.micro.testcases.semiautomatic.message;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.service.annotation.OnStart;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;

/**
 *  An agent that sends a message.
 */
@Agent
public class SenderAgent
{
	/** The receiver. */
	@AgentArgument
	protected IComponentIdentifier	receiver;
	
	/**
	 *  Send the message and wait for the result.
	 */
	//@AgentBody
	@OnStart
	protected void	run(IMessageFeature mf)
	{
		String	reply	= (String)mf.sendMessageAndWait(receiver, "Hello?").get();
		System.out.println("Sender received: "+reply);
	} 
}
