package jadex.micro.benchmarks;

import jadex.adapter.base.fipa.SFipa;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IArgument;
import jadex.bridge.MessageType;
import jadex.microkernel.MicroAgent;
import jadex.microkernel.MicroAgentMetaInfo;

import java.util.HashMap;
import java.util.Map;

/**
 *  Test message performance. 
 */
public class MessagePerformanceAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The received messages. */
	protected int received;
	
	/** The start time. */
	protected long starttime;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public void executeBody()
	{
		int msgcnt = ((Integer)getArgument("max")).intValue();
		IComponentIdentifier receiver = getAgentIdentifier();
		starttime = getTime();
		
		System.out.println("Now sending " + msgcnt + " messages to " + receiver);
		
		boolean usecodec = ((Boolean)getArgument("codec")).booleanValue();
		System.out.println("Codec is: "+usecodec);
		// Send messages.
		for(int i=1; i<=msgcnt; i++)
		{
			Map request = new HashMap();
			request.put(SFipa.PERFORMATIVE, SFipa.INFORM);
			request.put(SFipa.RECEIVERS, new IComponentIdentifier[]{receiver});
			request.put(SFipa.REPLY_WITH, "some reply id");
			
			if(!usecodec)
			{	
				request.put(SFipa.CONTENT, "message: "+i);
			}
			else
			{
				request.put(SFipa.LANGUAGE, SFipa.NUGGETS_XML);
				request.put(SFipa.CONTENT, new Message("message: "+i, true));
			}
			
			sendMessage(request, SFipa.FIPA_MESSAGE_TYPE);
			if(i % 10 == 0)
			{
				System.out.print('.');
				// waitFor(0);
			}
		}
	}
	
	/**
	 *  Called on message arrival.
	 */
	public void messageArrived(Map msg, MessageType mt)
	{
		received++;
			
		int msgcnt = ((Integer)getArgument("max")).intValue();
		if(received==msgcnt)
		{
			long dur = getTime() - starttime;
			System.out.println("Sending/receiving " + msgcnt + " messages took: " + dur + " milliseconds.");
			killAgent();
		}
	}
	

	/**
	 *  Get the meta information about the agent.
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agents benchmarks agent message sending.", 
			new String[0], new IArgument[]
			{
				new IArgument()
				{
					public Object getDefaultValue(String configname)
					{
						return new Integer(1000);
					}
					public String getDescription()
					{
						return "Maximum number of messages to send.";
					}
					public String getName()
					{
						return "max";
					}
					public String getTypename()
					{
						return "Integer";
					}
					public boolean validate(String input)
					{
						boolean ret = true;
						try
						{
							Integer.parseInt(input);
						}
						catch(Exception e)
						{
							ret = false;
						}
						return ret;
					}
				},
				new IArgument()
				{
					public Object getDefaultValue(String configname)
					{
						return Boolean.FALSE;
					}
					public String getDescription()
					{
						return "Use content codec for message content.";
					}
					public String getName()
					{
						return "codec";
					}
					public String getTypename()
					{
						return "boolean";
					}
					public boolean validate(String input)
					{
						boolean ret = true;
						try
						{
							Boolean.valueOf(input);
						}
						catch(Exception e)
						{
							ret = false;
						}
						return ret;
					}
				}
			}, null, null
		);
	}
}
