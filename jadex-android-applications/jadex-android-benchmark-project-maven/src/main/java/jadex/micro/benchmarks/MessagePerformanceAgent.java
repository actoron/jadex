package jadex.micro.benchmarks;

import jadex.platform.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.NameValue;
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
	@Argument(name="max", clazz=int.class, defaultvalue="100", description="Maximum number of messages to send."),
	@Argument(name="codec", clazz=boolean.class, defaultvalue="false", description="Use content codec for message content."),
	@Argument(name="echo", clazz=IComponentIdentifier.class, description="Address of an echo agent.")
})
@Results(@Result(name="result", clazz=String.class, description="The benchmark results as text."))
@Configurations(
{
	@Configuration(name="local"),
	@Configuration(name="remote", arguments=@NameValue(name="echo",
		value="new jadex.bridge.ComponentIdentifier(\"echo@echo\", new String[]{\""+SRelay.DEFAULT_ADDRESS+"\"})"))
})
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
	public IFuture<Void>	executeBody()
	{
		getTime().addResultListener(new DefaultResultListener<Long>()
		{
			public void resultAvailable(Long result)
			{
				current = 1;
				starttime = result.longValue();
				
				final int msgcnt = ((Integer)getArgument("max")).intValue();
				final IComponentIdentifier receiver = getArgument("echo")!=null
					? (IComponentIdentifier)getArgument("echo") : getComponentIdentifier();
				final boolean usecodec = ((Boolean)getArgument("codec")).booleanValue();
				
				final CounterResultListener<Void>	crl	= new CounterResultListener<Void>(msgcnt, true, new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						System.out.println("sending completed");
					}

					public void exceptionOccurred(Exception exception)
					{
						System.out.println("sending failed: "+exception);
						exception.printStackTrace();
					}
				});
				
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
							
							sendMessage(request, SFipa.FIPA_MESSAGE_TYPE).addResultListener(crl);
//								.addResultListener(new IResultListener<Void>()
//							{
//								public void resultAvailable(Void result)
//								{
//									System.out.println("message sent");
//								}
//								
//								public void exceptionOccurred(Exception exception)
//								{
//									System.out.println("message not sent: "+exception);
//									exception.printStackTrace();
//								}
//							});
							if(i%100==0)
							{
								break;
							}
						}
						
						current = i+1;
						if(current<=msgcnt)
						{
							waitFor(0, this);
						}
						else
						{
							System.out.println("all messages queued for sending");
						}
						
						return IFuture.DONE;
					}
				};
				
				send.execute(MessagePerformanceAgent.this);
			}
		});
		
		return new Future<Void>();
	}
	
	/**
	 *  Called on message arrival.
	 */
	public void messageArrived(Map<String, Object> msg, MessageType mt)
	{
		if(received == 0)
		{
			System.out.println("received first message");
		}
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
}
