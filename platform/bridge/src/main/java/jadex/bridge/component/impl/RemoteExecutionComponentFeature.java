package jadex.bridge.component.impl;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.TimeoutIntermediateResultListener;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.IRemoteCommand;
import jadex.bridge.component.IRemoteExecutionFeature;
import jadex.bridge.component.IUntrustedMessageHandler;
import jadex.bridge.component.impl.remotecommands.AbstractInternalRemoteCommand;
import jadex.bridge.component.impl.remotecommands.AbstractResultCommand;
import jadex.bridge.component.impl.remotecommands.ISecuredRemoteCommand;
import jadex.bridge.component.impl.remotecommands.RemoteFinishedCommand;
import jadex.bridge.component.impl.remotecommands.RemoteForwardCmdCommand;
import jadex.bridge.component.impl.remotecommands.RemoteIntermediateResultCommand;
import jadex.bridge.component.impl.remotecommands.RemoteMethodInvocationCommand;
import jadex.bridge.component.impl.remotecommands.RemotePullCommand;
import jadex.bridge.component.impl.remotecommands.RemoteReference;
import jadex.bridge.component.impl.remotecommands.RemoteResultCommand;
import jadex.bridge.component.impl.remotecommands.RemoteSearchCommand;
import jadex.bridge.component.impl.remotecommands.RemoteTerminationCommand;
import jadex.bridge.service.ServiceIdentifier;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateFutureCommandResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;

/**
 *  Feature for securely sending and handling remote execution commands.
 */
public class RemoteExecutionComponentFeature extends AbstractComponentFeature implements IRemoteExecutionFeature, IInternalRemoteExecutionFeature
{
	//-------- constants ---------

	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IRemoteExecutionFeature.class, RemoteExecutionComponentFeature.class);
	
	/** ID of the remote execution command in progress. */
	public static final String RX_ID = "__rx_id__";
	
	/** Commands safe to use with untrusted clients. */
	protected static final Set<Class<?>> SAFE_COMMANDS	= Collections.unmodifiableSet(new HashSet<Class<?>>()
	{{
		// Unconditional commands
		add(RemoteFinishedCommand.class);
		add(RemoteForwardCmdCommand.class);
		add(RemoteIntermediateResultCommand.class);
		add(RemotePullCommand.class);
		add(RemoteResultCommand.class);
		add(RemoteTerminationCommand.class);

		// Conditional commands (throwing security exception in execute when not allowed).
		add(RemoteSearchCommand.class);
		add(RemoteMethodInvocationCommand.class);
	}});
	
	/** Commands that have been sent to a remote component.
	 *  Stored to set return value etc. */
	protected Map<String, OutCommand> outcommands;
	
	/** Commands that have been received to be executed locally.
	 *  Stored to allow termination etc.*/
	protected Map<String, IFuture<?>> incommands;
	
	/**
	 *  Create the feature.
	 */
	public RemoteExecutionComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Initialize the feature.
	 */
	@Override
	public IFuture<Void> init()
	{
		getComponent().getFeature(IMessageFeature.class).addMessageHandler(new RxHandler());
		return super.init();
	}
	
	/**
	 *  Has no user body.
	 */
	@Override
	public boolean hasUserBody()
	{
		return false;
	}
	
	/**
	 *  Execute a command on a remote agent.
	 *  @param target	The component to send the command to.
	 *  @param command	The command to be executed.
	 *  @param clazz	The return type.
	 *  @param timeout	Custom timeout or null for default.
	 *  @return	The result(s) of the command, if any.
	 */
	public <T> IFuture<T>	execute(final IComponentIdentifier target, IRemoteCommand<T> command, Class<? extends IFuture<T>> clazz, Long timeout)
	{
		final String rxid = SUtil.createUniqueId("");
//		System.out.println(getComponent().getComponentIdentifier() + " sending remote command: "+command+", rxid="+rxid);
		final long ftimeout	= timeout!=null ? timeout.longValue() : Starter.getRemoteDefaultTimeout(getComponent().getId());
		
		// TODO: Merge with DecouplingInterceptor code.
		@SuppressWarnings("unchecked")
		final Future<T> ret	= (Future<T>)FutureFunctionality.getDelegationFuture(clazz, new FutureFunctionality(getComponent().getLogger())
		{
			@Override
			public boolean isUndone(boolean undone)
			{
				// Always undone when (potentially) timeout exception.
				return undone || ftimeout>=0;
			}
			
			@Override
			public void handleTerminated(Exception reason)
			{
				sendRxMessage(target, rxid, new RemoteTerminationCommand<T>(reason));
			}
			
			@Override
			public void handlePull()
			{
				sendRxMessage(target, rxid, new RemotePullCommand<T>());
			}
			
			// TODO: send backward command
			
			// cleanup on finished:
			
			@Override
			public void handleFinished(Collection<Object> results) throws Exception
			{
//				System.out.println("Remove due to finished: "+target+", "+command);
				outcommands.remove(rxid);
			}
			
			@Override
			public Object handleResult(Object result) throws Exception
			{
//				System.out.println("Remove due to result: "+target+", "+command);
				outcommands.remove(rxid);
				return result;
			}
			
			@Override
			public void handleException(Exception exception)
			{
//				System.out.println("Remove due to exception: "+target+", "+command+", "+exception);
				outcommands.remove(rxid);
			}
		});
		
		if(ftimeout>=0)
		{
			@SuppressWarnings({"rawtypes", "unchecked"})
			IResultListener<T>	trl	= new TimeoutIntermediateResultListener(ftimeout, getComponent().getExternalAccess(), true, command, null)
			{
				@Override
				public void timeoutOccurred(TimeoutException te)
				{
					ret.setExceptionIfUndone(te);
				}
			};			
			ret.addResultListener(trl);
		}

		if(outcommands==null)
		{
			outcommands	= new HashMap<String, OutCommand>();
		}
		OutCommand outcmd = new OutCommand(ret);
		outcommands.put(rxid, outcmd);
		
		sendRxMessage(target, rxid, command).addResultListener(new IResultListener<Void>()
		{
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("Remove due to exception2: "+target+", "+command+", "+exception);
				OutCommand outcmd = outcommands.remove(rxid);
				if (outcmd != null)
				{
					@SuppressWarnings("unchecked")
					Future<T> ret = (Future<T>) outcmd.getFuture();
					if (ret != null)
						ret.setExceptionIfUndone(exception);
				}
			}
			
			public void resultAvailable(Void result)
			{
			}
		});
		
		return ret;
	}
	
	/**
	 *  Execute a command on a remote agent.
	 *  @param target	The component to send the command to.
	 *  @param command	The command to be executed.
	 *  @return	The result(s) of the command, if any.
	 */
	// TODO: remove method and use remote registry service instead!!!
	public <T> IFuture<Collection<T>>	executeRemoteSearch(IComponentIdentifier target, ServiceQuery<T> query)
	{
		Long timeout	= null;	// TODO: custom search timeout?
		@SuppressWarnings({"rawtypes"})
		Class	clazz	= query.getMultiplicity().getTo()!=-1
			? IIntermediateFuture.class
			: IFuture.class;
		return execute(target, new RemoteSearchCommand<T>(query), clazz, timeout);
	}

	/**
	 *  Invoke a method on a remote object.
	 *  @param ref	The target reference.
	 *  @param method	The method to be executed.
	 *  @param args	The arguments.
	 *  @return	The result(s) of the method invocation, if any. Connects any futures involved.
	 */
	public <T> IFuture<T>	executeRemoteMethod(RemoteReference ref, Method method, Object[] args)
	{
		ServiceCall invoc = ServiceCall.getNextInvocation();
		Long timeout = invoc!=null && invoc.hasUserTimeout()? invoc.getTimeout(): null;
		Map<String, Object>	nonfunc	= invoc!=null ? invoc.getProperties() : null;
		CallAccess.resetNextInvocation();
		
		@SuppressWarnings("unchecked")
		Class<IFuture<T>>	clazz	= (Class<IFuture<T>>)(IFuture.class.isAssignableFrom(method.getReturnType())
			? (Class<IFuture<T>>)method.getReturnType()
			: IFuture.class);
		
		return execute(ref.getRemoteComponent(), new RemoteMethodInvocationCommand<T>(ref.getTargetIdentifier(), method, args, nonfunc), clazz, timeout);
	}

	/**
	 *  Sends RX message.
	 *  
	 *  @param receiver The receiver.
	 *  @param rxid The remote execution ID.
	 *  @param msg The message.
	 *  
	 *  @return Null, when sent.
	 */
	protected IFuture<Void> sendRxMessage(IComponentIdentifier receiver, String rxid, final Object msg)
	{
		Map<String, Object> header = new HashMap<String, Object>();
		header.put(RX_ID, rxid);
		
		IFuture<Void> ret = component.getFeature(IMessageFeature.class).sendMessage(msg, header, receiver);
//		ret.addResultListener(new IResultListener<Void>()
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("not sent: "+exception+" "+msg);
//			}
//			public void resultAvailable(Void result)
//			{
//				System.out.println("sent: "+msg);
//			}
//		});
		return ret;
	}
	
	/**
	 *  Handle RX Messages.
	 *  Also handles untrusted messages and does its own security checks.
	 *
	 */
	protected class RxHandler implements IUntrustedMessageHandler
	{
		/**
		 *  Test if handler should handle a message.
		 *  @return True if it should handle the message. 
		 */
		public boolean isHandling(IMsgSecurityInfos secinfos, IMsgHeader header, Object msg)
		{
			return header.getProperty(RX_ID) instanceof String;
		}
		
		/**
		 *  Test if handler should be removed.
		 *  @return True if it should be removed. 
		 */
		public boolean isRemove()
		{
			return false;
		}
		
		/**
		 *  Handle the message.
		 *  @param header The header.
		 *  @param msg The message.
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void handleMessage(IMsgSecurityInfos secinfos, IMsgHeader header, Object msg)
		{
			final String rxid = (String) header.getProperty(RX_ID);
//			System.out.println(getComponent().getComponentIdentifier() + " received remote command: "+msg+", rxid="+rxid);
			
			if(msg instanceof IRemoteCommand)
			{
				final IComponentIdentifier remote = (IComponentIdentifier) header.getProperty(IMsgHeader.SENDER);
				if(checkSecurity(secinfos, header, msg))
				{
					IRemoteCommand<?> cmd = (IRemoteCommand<?>)msg;
					ServiceCall	sc	= null;
					if(cmd instanceof AbstractInternalRemoteCommand)
					{
						// Create new hashmap to prevent remote manipulation of the map object
						Map<String, Object>	nonfunc	= new HashMap<>(SUtil.safeMap(((AbstractInternalRemoteCommand)cmd).getProperties()));
//						if(nonfunc==null)
//							nonfunc = new HashMap<String, Object>();
						nonfunc.put(ServiceCall.SECURITY_INFOS, secinfos);
						IComponentIdentifier.LOCAL.set((IComponentIdentifier)header.getProperty(IMsgHeader.SENDER));
						// Local is used to set the caller in the new service call context
						sc = ServiceCall.getOrCreateNextInvocation(nonfunc);
						// After call creation it can be reset
						IComponentIdentifier.LOCAL.set(getComponent().getId());
					}
					final ServiceCall	fsc	= sc;
					
					final IFuture<?> retfut = cmd.execute(component, secinfos);
					CallAccess.resetNextInvocation();
					if(incommands == null)
						incommands = new HashMap<String, IFuture<?>>();
					IFuture<?> prev	= incommands.put(rxid, retfut);
					assert prev==null;
					
					final IResultListener<Void>	term;
					if(retfut instanceof ITerminableFuture)
					{
						term = new IResultListener<Void>()
						{
							public void exceptionOccurred(Exception exception)
							{
								((ITerminableFuture)retfut).terminate();
								incommands.remove(rxid);
							}
							
							public void resultAvailable(Void result)
							{
							}
						};
					}
					else
					{
						term	= null;
					}
					
					retfut.addResultListener(component.getFeature(IExecutionFeature.class).createResultListener(new IIntermediateFutureCommandResultListener()
					{
						/** Result counter. */
						int counter = Integer.MIN_VALUE;
						
						public void intermediateResultAvailable(Object result)
						{
							RemoteIntermediateResultCommand<?> rc = new RemoteIntermediateResultCommand(result, fsc!=null ? fsc.getProperties() : null);
							rc.setResultCount(counter++);
							IFuture<Void>	fut	= sendRxMessage(remote, rxid, rc);
							if(term!=null)
							{
								fut.addResultListener(term);
							}
						}
						
						public void finished()
						{
							incommands.remove(rxid);
							RemoteFinishedCommand<?> rc = new RemoteFinishedCommand(fsc!=null ? fsc.getProperties() : null);
							rc.setResultCount(counter++);
							sendRxMessage(remote, rxid, rc);
						}
						
						public void resultAvailable(final Object result)
						{
//							getComponent().getLogger().severe("sending result: "+rxid+", "+result);
							incommands.remove(rxid);
							RemoteResultCommand<?> rc = new RemoteResultCommand(result, fsc!=null ? fsc.getProperties() : null);
							final int msgcounter = counter++;
							rc.setResultCount(msgcounter);
							sendRxMessage(remote, rxid, rc).addResultListener(new IResultListener<Void>()
							{
								@Override
								public void exceptionOccurred(Exception exception)
								{
//									getComponent().getLogger().severe("sending result failed: "+rxid+", "+result+", "+exception);
									// Serialization of result failed -> send back exception.
									RemoteResultCommand<?> rc = new RemoteResultCommand(exception, fsc!=null ? fsc.getProperties() : null);
									rc.setResultCount(msgcounter);
									sendRxMessage(remote, rxid, rc);
								}
								
								@Override
								public void resultAvailable(Void v)
								{
//									getComponent().getLogger().severe("sending result succeeded: "+rxid+", "+result);
									// OK -> ignore
								}
							});
						}
						
						public void resultAvailable(Collection result)
						{
							resultAvailable(result);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							incommands.remove(rxid);
							RemoteResultCommand<?> rc = new RemoteResultCommand(exception, fsc!=null ? fsc.getProperties() : null);
							rc.setResultCount(counter++);
							sendRxMessage(remote, rxid, rc);
						}
						
						public void commandAvailable(Object command)
						{
							RemoteForwardCmdCommand fc = new RemoteForwardCmdCommand(command);
							fc.setResultCount(counter++);
							IFuture<Void>	fut	= sendRxMessage(remote, rxid, fc);
							if(term!=null)
							{
								fut.addResultListener(term);
							}
						}
					}));
				}
				else
				{
					// Not allowed -> send back exception.
					RemoteResultCommand<?> rc = new RemoteResultCommand(new SecurityException("Command "+msg.getClass()+" not allowed."), null);
					sendRxMessage(remote, rxid, rc);	
				}
			}
			else if(msg instanceof IRemoteConversationCommand || msg instanceof IRemoteOrderedConversationCommand)
			{
				if(checkSecurity(secinfos, header, msg))
				{
					// Can be result/exception -> for outcommands
					// or termination -> for incommands.
					OutCommand outcmd = outcommands!=null ? outcommands.get(rxid) : null;
					IFuture<?> fut = outcmd != null ? outcmd.getFuture() : null;
					fut	= fut!=null ? fut : incommands!=null ? incommands.get(rxid) : null;
					
					if(fut!=null)
					{
						if(msg instanceof AbstractInternalRemoteCommand)
						{
							// Create new hashmap to prevent remote manipulation of the map object
							Map<String, Object>	nonfunc	= new HashMap(SUtil.safeMap(((AbstractInternalRemoteCommand)msg).getProperties()));
							nonfunc.put(ServiceCall.SECURITY_INFOS, secinfos);
							ServiceCall sc = ServiceCall.getLastInvocation();
							if(sc==null)
							{
								// TODO: why null?
								sc	= CallAccess.createServiceCall((IComponentIdentifier)header.getProperty(IMsgHeader.SENDER), nonfunc);
							}
							else
							{
								for(String name: nonfunc.keySet())
								{
									sc.setProperty(name, nonfunc.get(name));
								}
							}
						}
						
						if (msg instanceof IRemoteConversationCommand)
						{
							IRemoteConversationCommand<?> cmd = (IRemoteConversationCommand<?>)msg;
							cmd.execute(component, (IFuture)fut, secinfos);
						}
						else
						{
							IRemoteOrderedConversationCommand cmd = (IRemoteOrderedConversationCommand)msg;
							cmd.execute(component, outcmd, secinfos);
						}
					}
					else
					{
						getComponent().getLogger().warning("Outdated remote command: "+msg);
					}
				}
				else
				{
					getComponent().getLogger().warning("Command from "+header.getProperty(IMsgHeader.SENDER)+" not allowed: "+msg.getClass());
				}
			}
			else if(header.getProperty(MessageComponentFeature.EXCEPTION)!=null)
			{
				// Message could not be delivered -> remove the future (and abort, if possible)
				
				// locally executing command -> terminate, if terminable (i.e. abort to callee)
				if(incommands!=null && incommands.get(rxid) instanceof ITerminableFuture)
				{
					((ITerminableFuture)incommands.remove(rxid))
						.terminate((Exception)header.getProperty(MessageComponentFeature.EXCEPTION));
				}
				
				// Remotely executing command -> set local future to failed (i.e. abort to caller)
				else if(outcommands!=null && outcommands.get(rxid) instanceof OutCommand)
				{
//					System.out.println("Remove due to exception3: "+header.getProperty(MessageComponentFeature.EXCEPTION));
					((Future) ((OutCommand)outcommands.remove(rxid)).getFuture())
						.setException((Exception)header.getProperty(MessageComponentFeature.EXCEPTION));
				}
			}
			else
			{
				getComponent().getLogger().warning("Invalid remote execution message: "+header+", "+msg);
			}
		}

		/**
		 *  Check if it is ok to execute a command.
		 */
		protected boolean checkSecurity(IMsgSecurityInfos secinfos, IMsgHeader header, Object msg)
		{
			boolean	trusted	= false;
			
			// Trusted platforms (i.e. in possession  of our platform key) can do anything.
			if(secinfos.isTrustedPlatform())
			{
				trusted	= true;
			}
			
			// Internal command -> safe to check as stated by command.
			else if(SAFE_COMMANDS.contains(msg.getClass()))
			{
				if(msg instanceof ISecuredRemoteCommand)
				{
					Set<String>	secroles	= ServiceIdentifier.getRoles(((ISecuredRemoteCommand)msg).getSecurityLevel(getComponent()), getComponent());
					
					// No roles or default role means any authenticated platform or network.
					if(secroles==null || secroles.contains(Security.DEFAULT))
					{
						trusted	= secinfos.isAuthenticated();
					}
					
					// Always allow 'unrestricted' access
					else if(secroles.contains(Security.UNRESTRICTED))
					{
						trusted	= true;
					}
					
					// TODO: currently no platform authentication in place
//					// Check remote platform name
//					else if(secroles.contains(secinfos.getAuthenticatedPlatformName()))
//					{
//						trusted	= true;
//					}
					
					// Custom roles required by command. Check if at least one is authenticated.
					else
					{
						// Assumption: secinfo role set (defined/authenticated roles of platform) usually larger than role set annotated at service / method.
						for(String role: secroles)
						{
							trusted	= secinfos.getRoles().contains(role);
							if(trusted)
								break;
						}
					}
				}
				else
				{
					// safe command without special security, e.g. intermediate result
					trusted	= true;
				}
			}
			
			if(!trusted)
			{
				getComponent().getLogger().info("Untrusted command not executed: "+msg);
			}
//			else
//			{
//				System.out.println("Trusted command allowed: "+msg);
//			}
			
			return trusted;
		}
	}
	
	/** Command that has been sent to a remote component.
	 *  Stored to set return value etc. */
	protected static class OutCommand implements IOrderedConversation
	{
		/** Commands that have been deferred until a prior command arrives. */
		protected PriorityQueue<AbstractResultCommand> deferredcommands;
		
		protected int resultcount = Integer.MIN_VALUE;
		
		/** Future for results. */
		protected IFuture<?> future;
		
		/**
		 *  Creates the command.
		 */
		public OutCommand(IFuture<?> future)
		{
			this.future = future;
		}
		
		/** Gets the future. */
		public IFuture<?> getFuture()
		{
			return future;
		}
		
		/**
		 *  Gets the count of the next result.
		 *  
		 *  @return The count of the next result.
		 */
		public int getNextResultCount()
		{
			return resultcount;
		}
		
		/**
		 *  Increases the next result count. 
		 */
		public void incNextResultCount()
		{
			++resultcount;
		}
		
		/**
		 *  Returns queue of commands that have been deferred due to
		 *  out-of-order arrival.
		 *  
		 *  @return Queue of commands, may be null.
		 */
		public PriorityQueue<AbstractResultCommand> getDeferredCommands()
		{
			if (deferredcommands == null)
			{
				deferredcommands = new PriorityQueue<AbstractResultCommand>(11, new Comparator<AbstractResultCommand>()
				{
					public int compare(AbstractResultCommand o1, AbstractResultCommand o2)
					{
						if (o1.getResultCount() == null)
							return 1;
						if (o2.getResultCount() == null)
							return -1;
						return o1.getResultCount() - o2.getResultCount();
					}
				});
			}
				
			
			return deferredcommands;
		}
	}
}
