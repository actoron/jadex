package jadex.micro.examples.ping;

import jadex.adapter.base.fipa.SFipa;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.MessageType;
import jadex.microkernel.MicroAgent;

import java.util.Map;

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
		Map msg = message.getParameterMap();
		
		String perf = (String)msg.get(SFipa.PERFORMATIVE);
		if((SFipa.QUERY_IF.equals(perf) || SFipa.QUERY_REF.equals(perf)) 
			&& "ping".equals(msg.get(SFipa.CONTENT)))
		{
			Map reply = createReply(message.getParameterMap(), message.getMessageType());
	
			reply.put(SFipa.CONTENT, "alive");
			reply.put(SFipa.PERFORMATIVE, SFipa.INFORM);
			reply.put(SFipa.SENDER, getAgentIdentifier());
			sendMessage(reply, mt);
		}
		else
		{
			getLogger().severe("Could not process message: "+msg);
		}
	}
}
