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
 * 
 */
public class PingAgent extends MicroAgent
{
	/**
	 *  
	 */
	public boolean executeAction()
	{
		return false;
	}
	
	/**
	 *  Send a reply to the sender.
	 *  @param message The message.
	 */
	public void messageArrived(IMessageAdapter message)
	{
		MessageType mt = message.getMessageType();

		Map msg = message.getParameterMap();
		Map reply = createReply(message.getParameterMap(), message.getMessageType());

		IMessageService ms = (IMessageService)getPlatform().getService(IMessageService.class);
		reply.put(SFipa.CONTENT, "alive");
		List recs = (List)reply.get(SFipa.RECEIVERS);
		ms.sendMessage(reply, mt, (IAgentIdentifier[])recs.toArray(new IAgentIdentifier[recs.size()]));
	}
}
