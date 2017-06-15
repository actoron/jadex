package jadex.micro.testcases.semiautomatic.message_old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 *  An agent that sends messages and prints the roundtrip time.
 */
@Agent
public class BenchmarkAgent
{
	/** The receiver. */
	@AgentArgument
	protected IComponentIdentifier	receiver;
	
	/** The number of messages. */
	@AgentArgument
	protected int	count	= 10000;
	
	/**
	 *  Send the message and wait for the result.
	 */
	@AgentBody
	protected void	run(IMessageFeature mf)
	{
		// Dry run.
//		final List<String>	log	= new ArrayList<String>();
		for(int i=0;i<count/10; i++)
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
					// NOP
//					log.add("Sender received: "+msg.get(SFipa.CONTENT));
				}
			}).get();
//			log.add("Sender and wait completed.");
		}
//		for(String entry: log)
//		{
//			System.out.println(entry);
//		}

		// Now testing.
		long	start	= System.nanoTime();
		for(int i=0;i<count; i++)
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
					// NOP
//					System.out.println("Sender received: "+msg.get(SFipa.CONTENT));
				}
			}).get();
		}
		long	delta	= System.nanoTime() - start;

		System.out.println("Message roundtrip took "+delta/count/1000/1000.0+" milliseconds.");
	} 
}
