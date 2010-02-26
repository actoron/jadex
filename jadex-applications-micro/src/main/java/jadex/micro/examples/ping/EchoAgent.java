package jadex.micro.examples.ping;

import jadex.adapter.base.fipa.SFipa;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.MessageType;
import jadex.micro.MicroAgent;

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
		IComponentIdentifier sender = (IComponentIdentifier)msg.get(SFipa.SENDER);
		reply.put(SFipa.SENDER, getComponentIdentifier());
		reply.put(SFipa.RECEIVERS, new IComponentIdentifier[]{sender});
		
		sendMessage(reply, mt);
	}
}
