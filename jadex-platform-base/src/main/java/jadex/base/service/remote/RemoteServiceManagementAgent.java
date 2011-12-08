package jadex.base.service.remote;

import jadex.base.fipa.SFipa;
import jadex.base.service.remote.commands.AbstractRemoteCommand;
import jadex.base.service.remote.commands.RemoteResultCommand;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.marshal.IMarshalService;
import jadex.bridge.service.types.message.MessageType;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
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
	public IFuture<Void>	agentCreated()
	{
		final Future<Void>	ret	= new Future<Void>();
		SServiceProvider.getService(getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new ExceptionDelegationResultListener<ILibraryService, Void>(ret)
		{
			public void customResultAvailable(final ILibraryService libservice)
			{
//				final ILibraryService libservice = (ILibraryService)result;
				
				SServiceProvider.getService(getServiceContainer(), IMarshalService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(createResultListener(new ExceptionDelegationResultListener<IMarshalService, Void>(ret)
				{
					public void customResultAvailable(final IMarshalService marshalservice)
					{
						rms = new RemoteServiceManagementService((IMicroExternalAccess)getExternalAccess(), libservice, marshalservice);
						addService("rms", IRemoteServiceManagementService.class, rms, BasicServiceInvocationHandler.PROXYTYPE_DIRECT);
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
	public IFuture<Void>	agentKilled()
	{
		// Send notifications to other processes that remote references are not needed any longer.
		return rms.getRemoteReferenceModule().shutdown();
	}
	
	/**
	 *  Called, whenever a message is received.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	public void messageArrived(final Map<String, Object> msg, final MessageType mt)
	{
//		System.out.println("RMS received message: "+msg);
		
		if(SFipa.MESSAGE_TYPE_NAME_FIPA.equals(mt.getName()))
		{
			getServiceContainer().searchService(ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DefaultResultListener<ILibraryService>()
			{
				public void resultAvailable(final ILibraryService ls)
				{
					// Hack!!! Manual decoding for using custom class loader.
					// todo: use classloader of receiver
					// currently just uses the 'global' platform classloader 
//					ClassLoader cl = ls.getClassLoader(null);//rms.getComponent().getModel().getResourceIdentifier());
					ls.getClassLoader(null).addResultListener(new DefaultResultListener<ClassLoader>()
					{
						public void resultAvailable(final ClassLoader cl) 
						{
							Object content = msg.get(SFipa.CONTENT);
							final String callid = (String)msg.get(SFipa.CONVERSATION_ID);
							
		//					System.out.println("received: "+rms.getServiceIdentifier()+" "+callid);
		//					
		//					if(((String)content).indexOf("store")!=-1)
		//						System.out.println("store command: "+callid+" "+getComponentIdentifier());
		
		//					// For debugging.
//							final String orig = (String)content;
		
							if(content instanceof String)
							{
								
								// Catch decode problems.
								// Should be ignored or be a warning.
								try
								{	
									List<Object>	errors	= new ArrayList<Object>();
//									String contentcopy = (String)content;	// for debugging
									
									content = Reader.objectFromXML(rms.getReader(), (String)content, cl, errors);
									
									// For corrupt result (e.g. if class not found) set exception to clean up waiting call.
									if(!errors.isEmpty())
									{
//										System.out.println("Error: "+contentcopy);
										if(content instanceof RemoteResultCommand)
										{
		//										System.out.println("corrupt content: "+content);
		//										System.out.println("errors: "+errors);
											((RemoteResultCommand)content).setExceptionInfo(new ExceptionInfo(new RuntimeException("Errors during XML decoding: "+errors)));
										}
										else
										{
		//										content	= null;
											content = new RemoteResultCommand(null, new RuntimeException("Errors during XML decoding: "+errors), callid, false);
										}
										getLogger().info("Remote service management service could not decode message from: "+msg.get(SFipa.SENDER));
		//									getLogger().warning("Remote service management service could not decode message."+orig+"\n"+errors);
									}
								}
								catch(Exception e)
								{
		//								content	= null;
									content = new RemoteResultCommand(null, e, callid, false);
									getLogger().info("Remote service management service could not decode message from: "+msg.get(SFipa.SENDER));
		//								getLogger().warning("Remote service management service could not decode message."+orig);
		//								e.printStackTrace();
								}
							}
							
							if(content instanceof IRemoteCommand)
							{
								final IRemoteCommand com = (IRemoteCommand)content;
								
		//						if(content instanceof RemoteResultCommand && ((RemoteResultCommand)content).getMethodName()!=null && ((RemoteResultCommand)content).getMethodName().indexOf("store")!=-1)
		//							System.out.println("result of command1: "+com+" "+result);
								
//								System.out.println("received: "+rms.getServiceIdentifier()+" "+content);
								
								// Validate command.
								final Future<Void>	valid	= new Future<Void>();
								if(com instanceof RemoteResultCommand)
								{
									// Results are allowed to simplify one-way authentication.
									valid.setResult(null);
								}
								else
								{
									getServiceContainer().searchService(ISecurityService.class, RequiredServiceInfo.SCOPE_PLATFORM)
										.addResultListener(new IResultListener<ISecurityService>()
									{
										public void resultAvailable(ISecurityService sec)
										{
											sec.validateRequest(com).addResultListener(new DelegationResultListener<Void>(valid));
										}
										public void exceptionOccurred(Exception e)
										{
											if(e instanceof ServiceNotFoundException)
											{
												// Valid by default, if no security service installed.
												valid.setResult(null);
											}
											else
											{
												valid.setException(e);
											}
										}
									});
								}
								
								// Execute command and fetch reply
								final Future<IRemoteCommand>	reply	= new Future<IRemoteCommand>();
								valid.addResultListener(new IResultListener<Void>()
								{
									public void resultAvailable(Void result)
									{
//										System.out.println("Command valid: "+com);
										com.execute((IMicroExternalAccess)getExternalAccess(), rms)
											.addResultListener(createResultListener(new DelegationResultListener<IRemoteCommand>(reply)));
									}
									
									public void exceptionOccurred(Exception exception)
									{
										getLogger().info("RMS rejected unauthorized command: "+com);
										reply.setResult(new RemoteResultCommand(null, exception, callid, false));
									}
								});
								
								reply.addResultListener(new DefaultResultListener<IRemoteCommand>()
								{
									public void resultAvailable(final IRemoteCommand result)
									{
//										if(((String)orig).indexOf("store")!=-1)
//											System.out.println("result of command: "+com+" "+result);
										if(result!=null)
										{
											Future<Void>	pre	= new Future<Void>(); 
											if(result instanceof AbstractRemoteCommand)
											{
												((AbstractRemoteCommand)result).preprocessCommand(RemoteServiceManagementAgent.this,
													rms.getRemoteReferenceModule(), (IComponentIdentifier)msg.get(SFipa.SENDER))
													.addResultListener(new DelegationResultListener<Void>(pre));
											}
											else
											{
												pre.setResult(null);
											}
											
											pre.addResultListener(new DefaultResultListener<Void>()
											{
												public void resultAvailable(Void v)
												{
													createReply(msg, mt).addResultListener(createResultListener(new DefaultResultListener<Map<String, Object>>()
													{
														public void resultAvailable(Map<String, Object> reply)
														{
				//											reply.put(SFipa.CONTENT, JavaWriter.objectToXML(repcontent, ls.getClassLoader()));
															
//															ClassLoader cl = ls.getClassLoader(null);//rms.getComponent().getModel().getResourceIdentifier());
															String content = Writer.objectToXML(rms.getWriter(), result, cl, msg.get(SFipa.SENDER));
															reply.put(SFipa.CONTENT, content);
				//											System.out.println("content: "+content);
															
//															System.out.println("reply: "+callid);
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
											});
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
								});
							}
							else if(content!=null)
							{
								getLogger().info("RMS unexpected message content: "+content);
							}
						}
					});
				}
			});
		}
	}
}
