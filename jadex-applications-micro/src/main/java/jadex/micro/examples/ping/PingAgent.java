package jadex.micro.examples.ping;

import jadex.base.fipa.SFipa;
import jadex.bridge.MessageType;
import jadex.micro.MicroAgent;

import java.util.Map;

/**
 *  Answer ping requests. 
 */
public class PingAgent extends MicroAgent
{
	/**
	 *  Send a reply to the sender.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	public void messageArrived(Map msg, MessageType mt)
	{
		String perf = (String)msg.get(SFipa.PERFORMATIVE);
		if((SFipa.QUERY_IF.equals(perf) || SFipa.QUERY_REF.equals(perf)) 
			&& "ping".equals(msg.get(SFipa.CONTENT)))
		{
			Map reply = createReply(msg, mt);
	
			reply.put(SFipa.CONTENT, "alive");
			reply.put(SFipa.PERFORMATIVE, SFipa.INFORM);
			reply.put(SFipa.SENDER, getComponentIdentifier());
			sendMessage(reply, mt);
		}
		else
		{
			getLogger().severe("Could not process message: "+msg);
		}
	}
}
