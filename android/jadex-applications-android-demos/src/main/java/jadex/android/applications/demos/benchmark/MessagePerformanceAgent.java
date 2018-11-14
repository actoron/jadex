package jadex.android.applications.demos.benchmark;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.SUtil;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentMessageArrived;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

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
	@Configuration(name="select"),
	@Configuration(name="remote", arguments=@NameValue(name="echo",
		value="new jadex.bridge.ComponentIdentifier(\"echo@echo\", new String[]{\"relay-http://jadex.informatik.uni-hamburg.de/relay/\"})"))
//		value="new jadex.bridge.ComponentIdentifier(\"echo@echo\", new String[]{\""+SRelay.DEFAULT_ADDRESS+"\"})"))
//		value="new jadex.bridge.ComponentIdentifier(\"echo@echo\", new String[]{\"relay-http://134.100.11.200:8080/jadex-platform-relay-web/\"})"))
})
@Agent
public class MessagePerformanceAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
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
	@AgentBody
	public IFuture<Void> executeBody()
	{
		future = new Future<Void>();
		
		getTime().addResultListener(new DefaultResultListener<Long>()
		{
			public void resultAvailable(Long result)
			{
				current = 1;
				starttime = result.longValue();
				
				final int msgcnt = ((Integer)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("max")).intValue();
				final int msgsize = ((Integer)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("size")).intValue();
				boolean auto = ((Boolean)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("auto")).booleanValue();
				IComponentIdentifier receiver = agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("echo")!=null
					? (IComponentIdentifier)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("echo") : agent.getComponentIdentifier();
				final boolean usecodec = ((Boolean)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("codec")).booleanValue();
				
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
							Random r = new Random();
							
							public IFuture<Void> execute(IInternalAccess ia)
							{
								if(current==1)
								{
									System.out.println("Now sending " + msgcnt + " messages to " + receiver);
									System.out.println("Codec is: "+usecodec);
								}
								
								byte[]	content	= new byte[msgsize];	
								r.nextBytes(content);
								String scontent	= "";
								try
								{
									scontent = new String(content, "ISO-8859-1");
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
									
									IFuture<Void>	fut	= agent.getComponentFeature(IMessageFeature.class).sendMessage(receiver, request);
									fut.addResultListener(crl);
									fut.addResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
//													System.out.println("message sent");
										}
										
										public void exceptionOccurred(Exception exception)
										{
											if(!(exception instanceof ComponentTerminatedException))
											{
												System.out.println("message not sent: "+exception);
//												exception.printStackTrace();
											}
										}
									});
									if(i%10==0)
									{
										break;
									}
								}
								
								current = i+1;
								if(current<=msgcnt)
								{
									agent.getComponentFeature(IExecutionFeature.class).waitForDelay(0, this);
								}
								else
								{
									System.out.println("all messages queued for sending");
								}
								
								return IFuture.DONE;
							}
						};
												
						send.execute(agent);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						throw SUtil.throwUnchecked(exception);
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
//		if(auto)
//		{
//			agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IEchoService.class, RequiredServiceInfo.SCOPE_GLOBAL)
//				.addResultListener(new IResultListener<IEchoService>()
//			{
//				public void resultAvailable(IEchoService result)
//				{
//					ret.setResult(((IService)result).getServiceIdentifier().getProviderId());
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					ret.setResult(def);
//				}
//			});
//		}
//		else
		{
			ret.setResult(def);
		}
		return ret;
	}
	
	
	/**
	 *  Called on message arrival.
	 */
	@AgentMessageArrived
	public void messageArrived(Map<String, Object> msg)
	{
		if(received == 0)
		{
			System.out.println("received first message");
		}
		received++;
		final int msgcnt = ((Integer)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("max")).intValue();
		if(received==msgcnt)
		{
			getTime().addResultListener(new DefaultResultListener<Long>()
			{
				public void resultAvailable(Long result)
				{
					long dur = result.longValue() - starttime;
					System.out.println("Sending/receiving " + msgcnt + " messages took: " + dur + " milliseconds.");
					agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("result", "Sending/receiving " + msgcnt + " messages took: " + dur + " milliseconds.");
					
					future.addResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							agent.killComponent();
						}
						
						public void exceptionOccurred(Exception exception)
						{
							agent.killComponent();
						}
					});
					
				}
			});
		}
	}
	
	/**
	 *  Get the time.
	 */
	public IFuture<Long> getTime()
	{
		return new Future<Long>(new Long(SServiceProvider.getLocalService(agent, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).getTime()));
	}
}
