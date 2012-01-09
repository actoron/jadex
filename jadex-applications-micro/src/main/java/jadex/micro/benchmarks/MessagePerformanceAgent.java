package jadex.micro.benchmarks;

import jadex.base.fipa.SFipa;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.HashMap;
import java.util.Map;


/**
 *  Test message performance. 
 */
@Description("This agents benchmarks agent message sending.")
@Arguments(
{
	@Argument(name="max", clazz=int.class, defaultvalue="1000", description="Maximum number of messages to send."),
	@Argument(name="codec", clazz=boolean.class, defaultvalue="false", description="Use content codec for message content.")
})
@Results(@Result(name="result", clazz=String.class, description="The benchmark results as text."))
public class MessagePerformanceAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The received messages. */
	protected int received;
	
	/** The start time. */
	protected long starttime;
	
	/** The current message number sent. */
	protected int current;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public void executeBody()
	{
		getTime().addResultListener(new DefaultResultListener<Long>()
		{
			public void resultAvailable(Long result)
			{
				current = 1;
				starttime = result.longValue();
				
				final int msgcnt = ((Integer)getArgument("max")).intValue();
				final IComponentIdentifier receiver = getComponentIdentifier();
				final boolean usecodec = ((Boolean)getArgument("codec")).booleanValue();
				
				IComponentStep<Void> send = new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						if(current==1)
						{
							System.out.println("Now sending " + msgcnt + " messages to " + receiver);
							System.out.println("Codec is: "+usecodec);
						}
						
						// Send messages.
						int i = current;
						for(; i<=msgcnt; i++)
						{
							Map<String, Object> request = new HashMap<String, Object>();
							request.put(SFipa.PERFORMATIVE, SFipa.INFORM);
							request.put(SFipa.RECEIVERS, new IComponentIdentifier[]{receiver});
							request.put(SFipa.REPLY_WITH, "some reply id");
							
							if(!usecodec)
							{	
								request.put(SFipa.CONTENT, "message: "+i);
							}
							else
							{
								request.put(SFipa.LANGUAGE, SFipa.JADEX_XML);
								request.put(SFipa.CONTENT, new BenchmarkMessage("message: "+i, true));
							}
							
							sendMessage(request, SFipa.FIPA_MESSAGE_TYPE);
							if(i>0 && i%10 == 0)
							{
								System.out.print('.');
								if(i%1000==0)
								{
									System.out.println();
									break;
								}
							}
						}
						
						current = i+1;
						if(current<=msgcnt)
						{
							waitFor(0, this);
						}
						
						return IFuture.DONE;
					}
				};
				
				send.execute(MessagePerformanceAgent.this);
			}
		});
	}
	
	/**
	 *  Called on message arrival.
	 */
	public void messageArrived(Map<String, Object> msg, MessageType mt)
	{
		received++;
		final int msgcnt = ((Integer)getArgument("max")).intValue();
		if(received==msgcnt)
		{
			getTime().addResultListener(new DefaultResultListener<Long>()
			{
				public void resultAvailable(Long result)
				{
					long dur = result.longValue() - starttime;
					System.out.println("Sending/receiving " + msgcnt + " messages took: " + dur + " milliseconds.");
					setResultValue("result", "Sending/receiving " + msgcnt + " messages took: " + dur + " milliseconds.");
					killAgent();
				}
			});
		}
	}
	

//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static Object getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("This agents benchmarks agent message sending.", 
//			new String[0], new IArgument[]
//			{
//				new Argument("max", "Maximum number of messages to send.", "Integer", new Integer(1000))
//				{
//					public boolean validate(String input)
//					{
//						boolean ret = true;
//						try
//						{
//							Integer.parseInt(input);
//						}
//						catch(Exception e)
//						{
//							ret = false;
//						}
//						return ret;
//					}
//				},
//				new Argument("codec", "Use content codec for message content.", "boolean", Boolean.FALSE)
//				{
//					public boolean validate(String input)
//					{
//						boolean ret = true;
//						try
//						{
//							Boolean.valueOf(input);
//						}
//						catch(Exception e)
//						{
//							ret = false;
//						}
//						return ret;
//					}
//				}
//			}, new IArgument[]
//			{
//				new Argument("result", "The benchmark results as text.", "String")
//			}, null, null
//		);
//	}
}
