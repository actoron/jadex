package jadex.base.service.remote;

import jadex.base.fipa.SFipa;
import jadex.base.service.remote.commands.RemoteResultCommand;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.bridge.MessageType;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.library.ILibraryService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.IMicroExternalAccess;
import jadex.micro.MicroAgent;
import jadex.xml.reader.Reader;
import jadex.xml.writer.Writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Remote service management service that hosts the corresponding
 *  service. It basically has the task to forward messages from
 *  remote service management components on other platforms to its service.
 */
public class RemoteServiceManagementAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The remote management service. */
	protected RemoteServiceManagementService rms;
	
	//-------- constructors --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture	agentCreated()
	{
		final Future	ret	= new Future();
		SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IClockService clock = (IClockService)result;
				SServiceProvider.getService(getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final ILibraryService libservice = (ILibraryService)result;
						rms = new RemoteServiceManagementService((IMicroExternalAccess)getExternalAccess(), clock, libservice);
						addService(IRemoteServiceManagementService.class, rms, BasicServiceInvocationHandler.PROXYTYPE_DIRECT);
						ret.setResult(null);
					}
				}));
			}
		}));
		return ret;
	}
	
//	/**
//	 *  Execute the functional body of the agent.
//	 *  Is only called once.
//	 */
//	public void executeBody()
//	{
//		ICommand gcc = new ICommand()
//		{
//			public void execute(Object args)
//			{
//				System.gc();
//				waitFor(5000, this);
//			}
//		};
//		waitFor(5000, gcc);
//	}
	
	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public IFuture	agentKilled()
	{
		// Send notifications to other processes that remote references are not needed any longer.
		return rms.getRemoteReferenceModule().shutdown();
	}
	
	/**
	 *  Called, whenever a message is received.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	public void messageArrived(final Map msg, final MessageType mt)
	{
		if(SFipa.MESSAGE_TYPE_NAME_FIPA.equals(mt.getName()))
		{
			SServiceProvider.getService(getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(createResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					// Hack!!! Manual decoding for using custom class loader.
					final ILibraryService ls = (ILibraryService)result;
					Object content = msg.get(SFipa.CONTENT);
					final String callid = (String)msg.get(SFipa.CONVERSATION_ID);
					
//					System.out.println("received: "+callid);
					
//					if(((String)content).indexOf("getServices")!=-1)
//						System.out.println("getS: "+callid);
					
					if(content instanceof String)
					{
						// For debugging.
//						String orig = (String)content;
						
						// Catch decode problems.
						// Should be ignored or be a warning.
						try
						{	
//							content = JavaReader.objectFromXML((String)content, ls.getClassLoader());
							List	errors	= new ArrayList();
							content = Reader.objectFromXML(rms.getReader(), (String)content, ls.getClassLoader(), errors);
							
							// For corrupt result (e.g. if class not found) set exception to clean up waiting call.
							if(!errors.isEmpty())
							{
								if(content instanceof RemoteResultCommand)
								{
//									System.out.println("corrupt content: "+content);
//									System.out.println("errors: "+errors);
									((RemoteResultCommand)content).setExceptionInfo(new ExceptionInfo(new RuntimeException("Errors during XML decoding: "+errors)));
								}
								else
								{
//									content	= null;
									content = new RemoteResultCommand(null, new RuntimeException("Errors during XML decoding: "+errors), callid);
								}
								getLogger().info("Remote service management service could not decode message from: "+msg.get(SFipa.SENDER));
//								getLogger().warning("Remote service management service could not decode message."+orig+"\n"+errors);
							}
						}
						catch(Exception e)
						{
//							content	= null;
							content = new RemoteResultCommand(null, e, callid);
							getLogger().info("Remote service management service could not decode message from: "+msg.get(SFipa.SENDER));
//							getLogger().warning("Remote service management service could not decode message."+orig);
//							e.printStackTrace();
						}
					}
					
					if(content instanceof IRemoteCommand)
					{
						final IRemoteCommand com = (IRemoteCommand)content;
						
						com.execute((IMicroExternalAccess)getExternalAccess(), rms).addResultListener(createResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object result)
							{
		//						System.out.println("result of command: "+com+" "+result);
								if(result!=null)
								{
									final Object repcontent = result;
									createReply(msg, mt).addResultListener(createResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object result)
										{
											Map reply = (Map)result;
//											reply.put(SFipa.CONTENT, JavaWriter.objectToXML(repcontent, ls.getClassLoader()));
											String content = Writer.objectToXML(rms.getWriter(), repcontent, ls.getClassLoader(), msg.get(SFipa.SENDER));
											reply.put(SFipa.CONTENT, content);
//											System.out.println("content: "+content);
											
//											System.out.println("reply: "+callid);
											sendMessage(reply, mt);
										}
										public void exceptionOccurred(Exception exception)
										{
											// Terminated, when rms killed in mean time
											if(!(exception instanceof ComponentTerminatedException))
											{
												super.exceptionOccurred(exception);
											}
										}
									}));
								}
							}
							public void exceptionOccurred(Exception exception)
							{
								// Terminated, when rms killed in mean time
								if(!(exception instanceof ComponentTerminatedException))
								{
									super.exceptionOccurred(exception);
								}
							}
						}));
					}
					else if(content!=null)
					{
						getLogger().info("RMS unexpected message content: "+content);
					}
				}
			}));
		}
	}
}
