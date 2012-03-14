package jadex.micro.examples.ping;

import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.DefaultResultListener;
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
	public void messageArrived(Map msg, final MessageType mt)
	{
		String perf = (String)msg.get(SFipa.PERFORMATIVE);
		if((SFipa.QUERY_IF.equals(perf) || SFipa.QUERY_REF.equals(perf)) 
			&& "ping".equals(msg.get(SFipa.CONTENT)))
		{
			createReply(msg, mt).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					Map reply = (Map)result;
					reply.put(SFipa.CONTENT, "alive");
					reply.put(SFipa.PERFORMATIVE, SFipa.INFORM);
					reply.put(SFipa.SENDER, getComponentIdentifier());
					sendMessage(reply, mt);
				}
			});
		}
		else
		{
			getLogger().severe("Could not process message: "+msg);
		}
	}
}
