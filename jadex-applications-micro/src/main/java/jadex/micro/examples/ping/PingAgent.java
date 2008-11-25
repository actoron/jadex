package jadex.micro.examples.ping;

import java.util.List;
import java.util.Map;

import jadex.adapter.base.IMessageService;
import jadex.adapter.base.fipa.SFipa;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.MessageType;
import jadex.microkernel.MicroAgent;

/**
 *  Answer ping requests. 
 */
public class PingAgent extends MicroAgent
{
	/**
	 *  Send a reply to the sender.
	 *  @param message The message.
	 */
	public void messageArrived(IMessageAdapter message)
	{
		MessageType mt = message.getMessageType();
		Map reply = createReply(message.getParameterMap(), message.getMessageType());

		IMessageService ms = (IMessageService)getPlatform().getService(IMessageService.class);
		reply.put(SFipa.CONTENT, "alive");
		reply.put(SFipa.PERFORMATIVE, SFipa.INFORM);
		reply.put(SFipa.SENDER, getAgentIdentifier());
		List recs = (List)reply.get(SFipa.RECEIVERS);
		ms.sendMessage(reply, mt, (IAgentIdentifier[])recs.toArray(new IAgentIdentifier[recs.size()]));
	}
}
