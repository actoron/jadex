package jadex.platform.service.remote;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.ContentException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddressBook;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.marshal.IMarshalService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.MessageType;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.AgentMessageArrived;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Feature;
import jadex.micro.annotation.Features;
import jadex.micro.annotation.Properties;
import jadex.platform.service.remote.RemoteServiceManagementService.WaitingCallInfo;
import jadex.platform.service.remote.commands.AbstractRemoteCommand;
import jadex.platform.service.remote.commands.RemoteDGCAddReferenceCommand;
import jadex.platform.service.remote.commands.RemoteDGCRemoveReferenceCommand;
import jadex.platform.service.remote.commands.RemoteFutureTerminationCommand;
import jadex.platform.service.remote.commands.RemoteGetExternalAccessCommand;
import jadex.platform.service.remote.commands.RemoteIntermediateResultCommand;
import jadex.platform.service.remote.commands.RemoteMethodInvocationCommand;
import jadex.platform.service.remote.commands.RemoteResultCommand;
import jadex.platform.service.remote.commands.RemoteSearchCommand;
import jadex.platform.service.remote.replacements.DefaultEqualsMethodReplacement;
import jadex.platform.service.remote.replacements.DefaultHashcodeMethodReplacement;
import jadex.platform.service.remote.replacements.GetComponentFeatureMethodReplacement;

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
@Agent
@Features(@Feature(clazz=RmsMessageFeature.class, type=IMessageFeature.class, replace=true))
@Properties(@NameValue(name="system", value="true"))
public class RemoteServiceManagementAgent
{
	/** The receiver of a call in the message header. */
	public static final String CALL_RECEIVER = "callreceiver";
	
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The remote management service. */
	protected RemoteServiceManagementService rms;
	
	//-------- constructors --------
	
	/**
	 *  Called once after agent creation.
	 */
	@AgentCreated
	public IFuture<Void>	agentCreated()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Register communication classes that have aliases.
		STransformation.registerClass(ProxyInfo.class);
		STransformation.registerClass(ProxyReference.class);
		STransformation.registerClass(RemoteReference.class);
		STransformation.registerClass(RemoteDGCAddReferenceCommand.class);
		STransformation.registerClass(RemoteDGCRemoveReferenceCommand.class);
		STransformation.registerClass(RemoteFutureTerminationCommand.class);
		STransformation.registerClass(RemoteGetExternalAccessCommand.class);
		STransformation.registerClass(RemoteIntermediateResultCommand.class);
		STransformation.registerClass(RemoteMethodInvocationCommand.class);
		STransformation.registerClass(RemoteResultCommand.class);
		STransformation.registerClass(RemoteSearchCommand.class);
		STransformation.registerClass(DefaultEqualsMethodReplacement.class);
		STransformation.registerClass(DefaultHashcodeMethodReplacement.class);
		STransformation.registerClass(GetComponentFeatureMethodReplacement.class);
		
		final ILibraryService libservice = SServiceProvider.getLocalService(agent, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM, false);
		final IMarshalService marshalservice = SServiceProvider.getLocalService(agent, IMarshalService.class, RequiredServiceInfo.SCOPE_PLATFORM, false);
		final IMessageService msgservice = SServiceProvider.getLocalService(agent, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM, false);
//		boolean binarymode = ((Boolean)getArgument("binarymessages")).booleanValue();
		
		ITransportAddressService tas = SServiceProvider.getLocalService(agent, ITransportAddressService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		tas.getTransportAddresses().addResultListener(new ExceptionDelegationResultListener<TransportAddressBook, Void>(ret)
		{
			public void customResultAvailable(TransportAddressBook addresses)
			{
				rms = new RemoteServiceManagementService(agent.getExternalAccess(), libservice, marshalservice, msgservice, addresses);//, binarymode);
//				IMessageService msgser = SServiceProvider.getLocalService(agent.getComponentIdentifier(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM);
//				msgser.addPreprocessors(rms.getPreprocessors().toArray(new ITraverseProcessor[0])).get();
//				msgser.addPostprocessors(rms.getPostprocessors().toArray(new ITraverseProcessor[0])).get();
				agent.getComponentFeature(IProvidedServicesFeature.class).addService("rms", IRemoteServiceManagementService.class, rms, BasicServiceInvocationHandler.PROXYTYPE_DIRECT);
				ret.setResult(null);
			}
		});
		
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
	@AgentKilled
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
	@AgentMessageArrived
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
//			Map rep = agent.getComponentFeature(IMessageFeature.class).createReply(msg, mt);
			Map rep = mt.createReply(msg);
			rep.put(SFipa.CONTENT, "alive");
			rep.put(SFipa.PERFORMATIVE, SFipa.INFORM);
			agent.getComponentFeature(IMessageFeature.class).sendMessage(rep, mt);
			return;
		}
		
//		if(ServiceCall.getInvocation0()!=null)
//		{
//			System.out.println("sdklugi: "+ServiceCall.getInvocation0());
//		}
		
		final IResourceIdentifier[] rid = new IResourceIdentifier[1];
		
		if(SFipa.MESSAGE_TYPE_NAME_FIPA.equals(mt.getName()))
		{
//			ServiceCall	next	= ServiceCall.getOrCreateNextInvocation();
//			next.setProperty("debugsource", "RemoteServiceManagementAgent.messageArrived("+msg+")");
			
			agent.getComponentFeature(IRequiredServicesFeature.class).searchService(ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new IResultListener<ILibraryService>()
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
							((AbstractRemoteCommand)com).postprocessCommand(agent, rms.getRemoteReferenceModule(), agent.getComponentIdentifier()) : IFuture.DONE;
						final Map<String, Object> nonfunc = com instanceof AbstractRemoteCommand? ((AbstractRemoteCommand)com).getNonFunctionalProperties(): null;
							
						// Validate command.
						final Future<Void>	valid	= new Future<Void>();
						post.addResultListener(new DelegationResultListener<Void>(valid)
						{
							public void customResultAvailable(Void result)
							{
								agent.getComponentFeature(IRequiredServicesFeature.class).searchService(ISecurityService.class, RequiredServiceInfo.SCOPE_PLATFORM)
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
								RemoteServiceManagementService.getResourceIdentifier(agent.getExternalAccess(), ((AbstractRemoteCommand)com).getRealReceiver())
									.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<IResourceIdentifier>()
								{
									public void resultAvailable(final IResourceIdentifier srid) 
									{
//										if(ServiceCall.getInvocation0()!=null)
//										{
//											System.out.println("xdfhklx: "+ServiceCall.getInvocation0());
//										}

										rid[0] = srid;
//										System.out.println("Command valid: "+com);
										
//										if(com instanceof RemoteMethodInvocationCommand && ((RemoteMethodInvocationCommand)com).getCaller()==null)
//										{
//											System.out.println("No caller: "+msg.get(SFipa.SENDER)+", "+((RemoteMethodInvocationCommand)com).getMethodName());
//										}
										
										com.execute((IExternalAccess)agent.getExternalAccess(), rms)
											.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IntermediateDelegationResultListener<IRemoteCommand>(reply)));
									}
									
									
									public void exceptionOccurred(Exception exception)
									{
										// Terminated, when rms killed in mean time
										if(!(exception instanceof ComponentTerminatedException))
										{
											agent.getLogger().warning("Exception while sending reply to "+msg.get(SFipa.SENDER)+": "+exception);
										}
									}
								}));
							}
							
							public void exceptionOccurred(Exception exception)
							{
								// RMS might be terminated in mean time.
								if(!(exception instanceof ComponentTerminatedException))
								{
									agent.getLogger().info("RMS rejected unauthorized command: "+msg.get(SFipa.SENDER)+", "+com);
									reply.addIntermediateResult(new RemoteResultCommand(null, null, null, exception, callid, false, null, nonfunc));
									reply.setFinished();
								}
							}
						});								
					}
					else if(content instanceof ContentException)
					{
						agent.getLogger().info("RMS received broken message content: "+msg.get(SFipa.SENDER)+", "+content);							
						WaitingCallInfo	wci	= rms.getWaitingCall(callid);
						if(wci!=null)
						{
							Future<?> future = wci.getFuture();
							future.setExceptionIfUndone((Exception) content);
						}
						else
						{
							reply.addIntermediateResult(new RemoteResultCommand(null, null, null, new RuntimeException("RMS received broken message content: "+content), callid, false, null, null));
							reply.setFinished();
						}
					}
					else
					{
						agent.getLogger().info("RMS received unexpected message content: "+msg.get(SFipa.SENDER)+", "+content);
						reply.addIntermediateResult(new RemoteResultCommand(null, null, null, new RuntimeException("RMS received unexpected message content: "+content), callid, false, null, null));
						reply.setFinished();
					}

					// Send reply.
					reply.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IIntermediateResultListener<IRemoteCommand>()
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
//							if(result instanceof RemoteResultCommand)// && "ping".equals(((RemoteResultCommand)result).getResult()))
//								System.out.println("sending result command: "+result+" "+((RemoteResultCommand)result).getCallId());
							
							if(result!=null)
							{
								Future<Void>	pre	= new Future<Void>(); 
								if(result instanceof AbstractRemoteCommand)
								{
									((AbstractRemoteCommand)result).preprocessCommand(agent,
										rms.getRemoteReferenceModule(), (IComponentIdentifier)msg.get(SFipa.SENDER))
										.addResultListener(new DelegationResultListener<Void>(pre));
								}
								else
								{
									pre.setResult(null);
								}
								
								pre.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
								{
									public void resultAvailable(Void v)
									{
										final Map<String, Object> reply = mt.createReply(msg);
										
//										if((""+result).indexOf("RemoteSearchCommand")!=-1 && (""+result).indexOf("IServiceCallService")!=-1)
//										{
//											System.out.println("sdhkl");
//										}
										
//										if(rid[0]!=null && rid[0].getGlobalIdentifier()!=null)
//										{
////											System.out.println("rid: "+rid+" "+result.getClass());
//											reply.put(SFipa.X_RID, rid[0]);
//										}
//										else
//										{
//											System.out.println("no rid: "+result.getClass());
//										}
										reply.put(SFipa.CONTENT, result);
//										System.out.println("content: "+result);
//										System.out.println("reply: "+callid);
//										if(reply.toString().indexOf("IAutoTerminateService.subscribe()")!=-1)
//										{
//											System.out.println("RMS sending: "+System.currentTimeMillis()+", "+reply);
//										}

//										agent.getComponentFeature(IMessageFeature.class).sendMessage(reply, mt, null).addResultListener(new IResultListener<Void>()
										IMessageService ms = SServiceProvider.getLocalService(agent, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM);
										ms.sendMessage(reply, SFipa.FIPA_MESSAGE_TYPE, agent.getComponentIdentifier(), rid[0], ((AbstractRemoteCommand)result).getRealReceiver(), null, null).addResultListener(new IResultListener<Void>()
										{
											public void resultAvailable(Void res)
											{
												// Nop on success.
//												if(reply.toString().indexOf("IAutoTerminateService.subscribe()")!=-1)
//												{
//													System.out.println("RMS sent: "+System.currentTimeMillis()+", "+reply);
//												}
											}
											
											public void exceptionOccurred(Exception exception)
											{
//												if(reply.toString().indexOf("IAutoTerminateService.subscribe()")!=-1)
//												{
//													System.out.println("RMS exception: "+System.currentTimeMillis()+", "+exception+", "+reply);
//												}
//												exception.printStackTrace();v
												// Could not send message -> terminate future, if terminable.
												if(result instanceof RemoteIntermediateResultCommand
													&& ((RemoteIntermediateResultCommand)result).getOriginalFuture() instanceof ITerminableFuture)
												{
													((ITerminableFuture<?>)((RemoteIntermediateResultCommand)result).getOriginalFuture()).terminate(exception);
												}
											}
										});
									}
									
									public void exceptionOccurred(Exception exception)
									{
										// Terminated, when rms killed in mean time
										if(!(exception instanceof ComponentTerminatedException))
										{
											agent.getLogger().warning("Exception while sending reply to "+msg.get(SFipa.SENDER)+": "+exception);
										}
									}
								}));
							}
						}
						
						public void finished()
						{
						}
						
						public void exceptionOccurred(Exception exception)
						{
							// Terminated, when rms killed in mean time
							if(!(exception instanceof ComponentTerminatedException))
							{
								agent.getLogger().warning("Exception while sending reply to "+msg.get(SFipa.SENDER)+": "+exception);
							}
						}
					}));
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// Terminated, when rms killed in mean time
					if(!(exception instanceof ComponentTerminatedException))
					{
						agent.getLogger().warning("Exception while sending reply to "+msg.get(SFipa.SENDER)+": "+exception);
					}
				}
			});
		}
	}
}
