package jadex.micro.testcases.semiautomatic.message_old;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SUtil;
import jadex.micro.AbstractMessageHandler;
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
	@AgentBody
	protected void	run(IMessageFeature mf)
	{
		mf.sendMessageAndWait(new HashMap<String, Object>(){{
			put(SFipa.RECEIVERS, receiver);
			put(SFipa.CONTENT, "Hello?");
			put(SFipa.CONVERSATION_ID, SUtil.createUniqueId(receiver.getName()));
		}}, SFipa.FIPA_MESSAGE_TYPE, new AbstractMessageHandler()
		{
			@Override
			public void handleMessage(Map<String, Object> msg, MessageType type)
			{
				System.out.println("Sender received: "+msg.get(SFipa.CONTENT));
			}
		}).get();
	} 
}
