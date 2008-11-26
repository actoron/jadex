package jadex.micro.examples.ping;

import jadex.adapter.base.fipa.SFipa;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.MessageType;
import jadex.microkernel.MicroAgent;

import java.util.HashMap;
import java.util.Map;

/**
 *  Sends back the same message it received. 
 */
public class EchoAgent extends MicroAgent
{
	/**
	 *  Send same message back to the sender.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	public void messageArrived(Map msg, MessageType mt)
	{
		Map reply = new HashMap(msg);
		IAgentIdentifier sender = (IAgentIdentifier)msg.get(SFipa.SENDER);
		reply.put(SFipa.SENDER, getAgentIdentifier());
		reply.put(SFipa.RECEIVERS, new IAgentIdentifier[]{sender});
		
		sendMessage(reply, mt);
	}
}
