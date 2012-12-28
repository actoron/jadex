package jadex.platform.service.remote;

import jadex.base.service.remote.IRemoteCommand;
import jadex.base.service.remote.commands.AbstractRemoteCommand;
import jadex.base.service.remote.commands.RemoteIntermediateResultCommand;
import jadex.base.service.remote.commands.RemoteResultCommand;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.ContentException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.marshal.IMarshalService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.MessageType;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.IMicroExternalAccess;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.platform.service.remote.RemoteServiceManagementService.WaitingCallInfo;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 *  Remote service management service that hosts the corresponding
 *  service. It basically has the task to forward messages from
 *  remote service management components on other platforms to its service.
 */
//@Properties(@NameValue(name="logging.level", value="java.util.logging.Level.INFO"))
@Arguments(
{
	@Argument(name="binarymessages", clazz=boolean.class, defaultvalue="false", description="Set if the agent should send binary messages as default.")
})
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
				SServiceProvider.getService(getServiceContainer(), IMarshalService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(createResultListener(new ExceptionDelegationResultListener<IMarshalService, Void>(ret)
				{
					public void customResultAvailable(final IMarshalService marshalservice)
					{
						SServiceProvider.getService(getServiceContainer(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(createResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
						{
							public void customResultAvailable(final IMessageService msgservice)
							{
//								boolean binarymode = ((Boolean)getArgument("binarymessages")).booleanValue();
								rms = new RemoteServiceManagementService((IMicroExternalAccess)getExternalAccess(), libservice, marshalservice, msgservice);//, binarymode);
								addService("rms", IRemoteServiceManagementService.class, rms, BasicServiceInvocationHandler.PROXYTYPE_DIRECT);
								ret.setResult(null);
							}
						}));
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
//		String	tmp	= (""+msg.get(SFipa.CONTENT)).replace("\n", " ").replace("\r", " ");
//		System.out.println("RMS "+getComponentIdentifier()+" received message from "+msg.get(SFipa.SENDER)
//			+": "+tmp.substring(0, Math.min(tmp.length(), 400)));
		
		// Handle pings for message awareness (hack that rms does this?)
		if((SFipa.QUERY_IF.equals(msg.get(SFipa.PERFORMATIVE)) 
			|| SFipa.QUERY_REF.equals(msg.get(SFipa.PERFORMATIVE))) 
			&& "ping".equals(msg.get(SFipa.CONTENT)))
		{
//			System.out.println("replying to ping from "+msg.get(SFipa.SENDER));
			Map rep = createReply(msg, mt);
			rep.put(SFipa.CONTENT, "alive");
			rep.put(SFipa.PERFORMATIVE, SFipa.INFORM);
			rep.put(SFipa.SENDER, getComponentIdentifier());
			sendMessage(rep, mt);
			return;
		}
		
		final IResourceIdentifier[] rid = new IResourceIdentifier[1];
		
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
					Object content = msg.get(SFipa.CONTENT);
					
					final String callid = (String)msg.get(SFipa.CONVERSATION_ID);
					final IntermediateFuture<IRemoteCommand>	reply	= new IntermediateFuture<IRemoteCommand>();
//					System.out.println("received: "+rms.getServiceIdentifier()+" "+callid);
//					
//					if(((String)content).indexOf("store")!=-1)
//						System.out.println("store command: "+callid+" "+getComponentIdentifier());

					// Execute command.
					if(content instanceof IRemoteCommand)
					{
						final IRemoteCommand com = (IRemoteCommand)content;
						
//						if(content instanceof RemoteSearchCommand)
//							System.out.println("result of command1: "+com);
						
//								System.out.println("received: "+rms.getServiceIdentifier()+" "+content);
						
						// Post-process.
						final IFuture<Void>	post	= com instanceof AbstractRemoteCommand? 
							((AbstractRemoteCommand)com).postprocessCommand(RemoteServiceManagementAgent.this, rms.getRemoteReferenceModule(), getComponentIdentifier()) : IFuture.DONE;
						final Map<String, Object> nonfunc = com instanceof AbstractRemoteCommand? ((AbstractRemoteCommand)com).getNonFunctionalProperties(): null;
							
						// Validate command.
						final Future<Void>	valid	= new Future<Void>();
						post.addResultListener(new DelegationResultListener<Void>(valid)
						{
							public void customResultAvailable(Void result)
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
						});
						
						// Execute command and fetch reply
						valid.addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								RemoteServiceManagementService.getResourceIdentifier(getServiceProvider(), ((AbstractRemoteCommand)com).getRealReceiver())
									.addResultListener(createResultListener(new DefaultResultListener<IResourceIdentifier>()
								{
									public void resultAvailable(final IResourceIdentifier srid) 
									{
										rid[0] = srid;
//										System.out.println("Command valid: "+com);
										com.execute((IMicroExternalAccess)getExternalAccess(), rms)
											.addResultListener(createResultListener(new IntermediateDelegationResultListener<IRemoteCommand>(reply)));
									}
								}));
							}
							
							public void exceptionOccurred(Exception exception)
							{
								// RMS might be terminated in mean time.
								if(!(exception instanceof ComponentTerminatedException))
								{
									getLogger().info("RMS rejected unauthorized command: "+msg.get(SFipa.SENDER)+", "+com);
									reply.addIntermediateResult(new RemoteResultCommand(null, null, exception, callid, false, null, nonfunc));
									reply.setFinished();
								}
							}
						});								
					}
					else if(content instanceof ContentException)
					{
						getLogger().info("RMS received broken message content: "+msg.get(SFipa.SENDER)+", "+content);							
						WaitingCallInfo	wci	= rms.getWaitingCall(callid);
						if(wci!=null)
						{
							Future<?> future = wci.getFuture();
							future.setExceptionIfUndone((Exception) content);
						}
						else
						{
							reply.addIntermediateResult(new RemoteResultCommand(null, null, new RuntimeException("RMS received broken message content: "+content), callid, false, null, null));
							reply.setFinished();
						}
					}
					else
					{
						getLogger().info("RMS received unexpected message content: "+msg.get(SFipa.SENDER)+", "+content);
						reply.addIntermediateResult(new RemoteResultCommand(null, null, new RuntimeException("RMS received unexpected message content: "+content), callid, false, null, null));
						reply.setFinished();
					}

					// Send reply.
					reply.addResultListener(createResultListener(new IntermediateDefaultResultListener<IRemoteCommand>()
					{
						public void intermediateResultAvailable(IRemoteCommand result)
						{
							sendCommand(result);
						}
						
						public void resultAvailable(Collection<IRemoteCommand> result)
						{
							for(Iterator<IRemoteCommand> it=result.iterator(); it.hasNext(); )
								sendCommand(it.next());
						}
						
						public void sendCommand(final IRemoteCommand result)
						{
//							if(result instanceof RemoteResultCommand && "ping".equals(((RemoteResultCommand)result).getResult()))
//								System.out.println("result command: "+result);
							
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
										Map reply = createReply(msg, mt);
										if(rid[0]!=null)
										{
//											System.out.println("rid: "+rid+" "+result.getClass());
											reply.put(SFipa.X_RID, rid[0]);
										}
//										else
//										{
//											System.out.println("no rid: "+result.getClass());
//										}
										reply.put(SFipa.CONTENT, result);
//										System.out.println("content: "+result);
//										System.out.println("reply: "+callid);
										sendMessage(reply, mt, null).addResultListener(new IResultListener<Void>()
										{
											public void resultAvailable(Void res)
											{
//												System.out.println("result: "+result);
											}
											
											public void exceptionOccurred(Exception exception)
											{
//												System.out.println("exception: "+result+", "+exception);
//												exception.printStackTrace();
												// Could not send message -> terminate future, if terminable.
												if(result instanceof RemoteIntermediateResultCommand
													&& ((RemoteIntermediateResultCommand)result).getOriginalFuture() instanceof ITerminableFuture)
												{
													((ITerminableFuture<?>)((RemoteIntermediateResultCommand)result).getOriginalFuture()).terminate();
												}
											}
										});
									}
									
									public void exceptionOccurred(Exception exception)
									{
										// Ignore when terminated in mean time.
										if(!(exception instanceof ComponentTerminatedException))
										{
											super.exceptionOccurred(exception);
										}
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
					}));
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
	}
}
