package jadex.micro.benchmarks;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
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
import jadex.micro.examples.ping.IEchoService;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *  Test message performance. 
 */
@Description("This agents benchmarks agent message sending.")
@Arguments(
{
	@Argument(name="max", clazz=int.class, defaultvalue="100", description="Maximum number of messages to send."),
	@Argument(name="size", clazz=int.class, defaultvalue="100", description="Size in bytes of each message (when not using codec)."),
	@Argument(name="codec", clazz=boolean.class, defaultvalue="false", description="Use content codec for message content."),
	@Argument(name="echo", clazz=IComponentIdentifier.class, description="Address of an echo agent."),
	@Argument(name="auto", clazz=boolean.class, defaultvalue="false", description="Automatically find address of an echo agent.")
})
@Results(@Result(name="result", clazz=String.class, description="The benchmark results as text."))
@Configurations(
{
	@Configuration(name="local"),
	@Configuration(name="remote", arguments=@NameValue(name="echo",
		value="new jadex.bridge.ComponentIdentifier(\"echo@echo\", new String[]{\"relay-http://jadex.informatik.uni-hamburg.de/relay/\"})"))
//		value="new jadex.bridge.ComponentIdentifier(\"echo@echo\", new String[]{\""+SRelay.DEFAULT_ADDRESS+"\"})"))
//		value="new jadex.bridge.ComponentIdentifier(\"echo@echo\", new String[]{\"relay-http://134.100.11.200:8080/jadex-platform-relay-web/\"})"))
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
	
	/** The future for terminating. */
	protected Future<Void> future;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public IFuture<Void> executeBody()
	{
		future = new Future<Void>();
		
		getTime().addResultListener(new DefaultResultListener<Long>()
		{
			public void resultAvailable(Long result)
			{
				current = 1;
				starttime = result.longValue();
				
				final int msgcnt = ((Integer)getArgument("max")).intValue();
				final int msgsize = ((Integer)getArgument("size")).intValue();
				boolean auto = ((Boolean)getArgument("auto")).booleanValue();
				IComponentIdentifier receiver = getArgument("echo")!=null
					? (IComponentIdentifier)getArgument("echo") : getComponentIdentifier();
				final boolean usecodec = ((Boolean)getArgument("codec")).booleanValue();
				
				final CounterResultListener<Void>	crl	= new CounterResultListener<Void>(msgcnt, true, new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						System.out.println("sending completed");
						future.setResult(null);
					}

					public void exceptionOccurred(Exception exception)
					{
						System.out.println("sending failed: "+exception);
						exception.printStackTrace();
						future.setResult(null);
					}
				});
				
				getTarget(auto, receiver).addResultListener(new IResultListener<IComponentIdentifier>()
				{
					public void resultAvailable(final IComponentIdentifier receiver)
					{
						IComponentStep<Void> send = new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								if(current==1)
								{
									System.out.println("Now sending " + msgcnt + " messages to " + receiver);
									System.out.println("Codec is: "+usecodec);
								}
								
								byte[]	content	= new byte[msgsize];	
								new Random().nextBytes(content);
								String scontent	= "";
								try
								{
									scontent = new String(content, "UTF-8");
								}
								catch(UnsupportedEncodingException e)
								{
									e.printStackTrace();
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
//												request.put(SFipa.CONTENT, "message: "+i);
										request.put(SFipa.CONTENT, scontent);
									}
									else
									{
										request.put(SFipa.LANGUAGE, SFipa.JADEX_XML);
//												request.put(SFipa.CONTENT, new BenchmarkMessage("message: "+i, true));
										request.put(SFipa.CONTENT, new BenchmarkMessage(scontent, true));
									}
									
									IFuture<Void>	fut	= sendMessage(request, SFipa.FIPA_MESSAGE_TYPE);
									fut.addResultListener(crl);
									fut.addResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
//													System.out.println("message sent");
										}
										
										public void exceptionOccurred(Exception exception)
										{
											System.out.println("message not sent: "+exception);
											exception.printStackTrace();
										}
									});
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
					
					public void exceptionOccurred(Exception exception)
					{
						throw (exception instanceof RuntimeException) ? (RuntimeException) exception : new RuntimeException(exception);
					}
				});
			}
		});
		
		return new Future<Void>(); // never kill?!
	}
	
	/**
	 *  Get the component identifier for sending.
	 */
	protected IFuture<IComponentIdentifier>	getTarget(boolean auto, final IComponentIdentifier def)
	{
		final Future<IComponentIdentifier>	ret	= new Future<IComponentIdentifier>();
		if(auto)
		{
			getServiceContainer().searchService(IEchoService.class, RequiredServiceInfo.SCOPE_GLOBAL)
				.addResultListener(new IResultListener<IEchoService>()
			{
				public void resultAvailable(IEchoService result)
				{
					ret.setResult(((IService)result).getServiceIdentifier().getProviderId());
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setResult(def);
				}
			});
		}
		else
		{
			ret.setResult(def);
		}
		return ret;
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
					
					future.addResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							killAgent();
						}
						
						public void exceptionOccurred(Exception exception)
						{
							killAgent();
						}
					});
					
				}
			});
		}
	}
}
