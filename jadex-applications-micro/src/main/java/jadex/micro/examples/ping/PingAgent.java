package jadex.micro.examples.ping;

import jadex.base.fipa.SFipa;
import jadex.bridge.MessageType;
import jadex.commons.concurrent.DefaultResultListener;
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
			createReply(msg, mt).addResultListener(createResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					Map reply = (Map)result;
					reply.put(SFipa.CONTENT, "alive");
					reply.put(SFipa.PERFORMATIVE, SFipa.INFORM);
					reply.put(SFipa.SENDER, getComponentIdentifier());
					sendMessage(reply, mt);
				}
			}));
		}
		else
		{
			getLogger().severe("Could not process message: "+msg);
		}
	}
}
