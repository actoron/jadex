package jadex.base.service.remote;

import jadex.base.fipa.SFipa;
import jadex.base.service.remote.commands.RemoteMethodInvocationCommand;
import jadex.base.service.remote.commands.RemoteResultCommand;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.MessageType;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.clock.IClockService;
import jadex.commons.service.library.ILibraryService;
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
	public void agentCreated()
	{
		SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IClockService clock = (IClockService)result;
				SServiceProvider.getService(getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						final ILibraryService libservice = (ILibraryService)result;
						rms = new RemoteServiceManagementService((IMicroExternalAccess)getExternalAccess(), clock, libservice);
						addDirectService(rms);
					}
				}));
			}
		}));
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
	public void agentKilled()
	{
		// Send notifications to other processes that remote references are not needed any longer.
		rms.getRemoteReferenceModule().shutdown();
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
			SServiceProvider.getService(getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(createResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					// Hack!!! Manual decoding for using custom class loader.
					final ILibraryService ls = (ILibraryService)result;
					Object	content	= msg.get(SFipa.CONTENT);
					
					if(content.toString().indexOf("calc")!=-1)
					{
						System.out.println("com: "+content);
					}

					if(content instanceof String)
					{
						// Catch decode problems.
						// Should be ignored or be a warning.
						try
						{
//							content = JavaReader.objectFromXML((String)content, ls.getClassLoader());
							List	errors	= new ArrayList();
							content = Reader.objectFromByteArray(rms.getReader(), ((String)content).getBytes(), ls.getClassLoader(), errors);
							
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
									content	= null;
									getLogger().warning("Remote service management service could not decode message."+content);
								}
							}
						}
						catch(Exception e)
						{
							content	= null;
							getLogger().warning("Remote service management service could not decode message."+content);
						}
					}
					
					if(content instanceof IRemoteCommand)
					{
						final IRemoteCommand com = (IRemoteCommand)content;
						if(com instanceof RemoteMethodInvocationCommand
							&& ((RemoteMethodInvocationCommand)com).getMethodName().indexOf("calc")!=-1)
						{
							System.out.println("com: "+com);
						}
						
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
						getLogger().warning("RMS unexpected message content: "+content);
					}
				}
			}));
		}
	}
}
