package jadex.micro.benchmarks;

import java.util.HashMap;
import java.util.Map;

import jadex.adapter.base.fipa.SFipa;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IArgument;
import jadex.bridge.MessageType;
import jadex.microkernel.MicroAgent;
import jadex.microkernel.MicroAgentMetaInfo;

/**
 *  Test message performance. 
 */
public class MessagePerformanceAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The step indicating what should the agent do. */
	protected boolean done;
	
	/** The received messages. */
	protected int received;
	
	/** The start time. */
	protected long starttime;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public boolean executeAction()
	{
		if(!done)
		{
			done = true;
			
			int msgcnt = ((Integer)getArgument("max")).intValue();
			IAgentIdentifier receiver = getAgentIdentifier();
			starttime = getTime();
			
			System.out.println("Now sending " + msgcnt + " messages to " + receiver);
			
			// Send messages.
			for(int i=1; i<=msgcnt; i++)
			{
				Map request = new HashMap();
				request.put(SFipa.PERFORMATIVE, SFipa.INFORM);
				request.put(SFipa.CONTENT, "message: "+i);
				request.put(SFipa.RECEIVERS, new IAgentIdentifier[]{receiver});
				request.put(SFipa.REPLY_WITH, "some reply id");
				sendMessage(request, SFipa.FIPA_MESSAGE_TYPE);
				if(i % 10 == 0)
				{
					System.out.print('.');
					// waitFor(0);
				}
			}
		}
		
		return false;
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
				}
			}
		);
	}
}
