package jadex.bridge.component.impl;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.base.PlatformConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.TimeoutIntermediateResultListener;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMessageHandler;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.IRemoteCommand;
import jadex.bridge.component.IRemoteExecutionFeature;
import jadex.bridge.component.impl.remotecommands.AbstractInternalRemoteCommand;
import jadex.bridge.component.impl.remotecommands.RemoteFinishedCommand;
import jadex.bridge.component.impl.remotecommands.RemoteForwardCmdCommand;
import jadex.bridge.component.impl.remotecommands.RemoteIntermediateResultCommand;
import jadex.bridge.component.impl.remotecommands.RemoteMethodInvocationCommand;
import jadex.bridge.component.impl.remotecommands.RemotePullCommand;
import jadex.bridge.component.impl.remotecommands.RemoteReference;
import jadex.bridge.component.impl.remotecommands.RemoteResultCommand;
import jadex.bridge.component.impl.remotecommands.RemoteSearchCommand;
import jadex.bridge.component.impl.remotecommands.RemoteTerminationCommand;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.IAsyncFilter;
import jadex.commons.SUtil;
import jadex.commons.concurrent.TimeoutException;
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
		// TODO: security and safe commands
	}});
	
	protected Map<String, IFuture<?>> commands;
	
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
		getComponent().getComponentFeature(IMessageFeature.class).addMessageHandler(new RxHandler());
		return super.init();
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
		
		final long ftimeout	= timeout!=null ? timeout.longValue() : PlatformConfiguration.getRemoteDefaultTimeout(getComponent().getComponentIdentifier());
		
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
				commands.remove(rxid);
			}
			
			@Override
			public Object handleResult(Object result) throws Exception
			{
				commands.remove(rxid);
				return result;
			}
			
			@Override
			public void handleException(Exception exception)
			{
				commands.remove(rxid);
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

		if(commands==null)
		{
			commands	= new HashMap<String, IFuture<?>>();
		}
		commands.put(rxid, ret);
		
		sendRxMessage(target, rxid, command).addResultListener(new IResultListener<Void>()
		{
			public void exceptionOccurred(Exception exception)
			{
				@SuppressWarnings("unchecked")
				Future<T> ret = (Future<T>) commands.remove(rxid);
				if (ret != null)
					ret.setExceptionIfUndone(exception);
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
	public <T> IFuture<Collection<T>>	executeRemoteSearch(IComponentIdentifier target, ServiceQuery<T> query)
	{
		Long timeout	= null;	// TODO: custom search timeout?
		@SuppressWarnings("unchecked")
		Class<? extends IFuture<Collection<T>>>	clazz	= query.getFilter() instanceof IAsyncFilter
			? (Class<? extends IFuture<Collection<T>>>) IIntermediateFuture.class	// TODO: subscription for persistent queries?
			: (Class<? extends IFuture<Collection<T>>>) IFuture.class;
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
	protected IFuture<Void> sendRxMessage(IComponentIdentifier receiver, String rxid, Object msg)
	{
		Map<String, Object> header = new HashMap<String, Object>();
		header.put(RX_ID, rxid);
		return component.getComponentFeature(IMessageFeature.class).sendMessage(receiver, msg, header);
	}
	
	protected class RxHandler implements IMessageHandler
	{
		/**
		 *  Test if handler should handle a message.
		 *  @return True if it should handle the message. 
		 */
		public boolean isHandling(IMsgSecurityInfos secinfos, IMsgHeader header, Object msg)
		{
			boolean ret = false;
			if(header.getProperty(RX_ID) instanceof String)
			{
				if (secinfos.isTrustedPlatform() ||
					(secinfos.isAuthenticatedPlatform() && SAFE_COMMANDS.contains(msg.getClass())))
					ret = true;
			}
			return ret;
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
			if(msg instanceof IRemoteCommand)
			{
				IRemoteCommand<?> cmd = (IRemoteCommand<?>)msg;
				ServiceCall	sc	= null;
				if(cmd instanceof AbstractInternalRemoteCommand)
				{
					Map<String, Object>	nonfunc	= ((AbstractInternalRemoteCommand)cmd).getProperties();
					if(nonfunc!=null)
					{
						IComponentIdentifier.LOCAL.set((IComponentIdentifier)header.getProperty(IMsgHeader.SENDER));
						sc	= ServiceCall.getOrCreateNextInvocation(nonfunc);
						IComponentIdentifier.LOCAL.set(getComponent().getComponentIdentifier());
					}
				}
				final ServiceCall	fsc	= sc;
				
				final IFuture<?> retfut = cmd.execute(component, secinfos);
				if (commands == null)
					commands = new HashMap<String, IFuture<?>>();
				IFuture<?> prev	= commands.put(rxid, retfut);
				assert prev==null;
				
				final IResultListener<Void>	term;
				if(retfut instanceof ITerminableFuture)
				{
					term	= new IResultListener<Void>()
					{
						public void exceptionOccurred(Exception exception)
						{
							((ITerminableFuture)retfut).terminate();
							commands.remove(rxid);
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
				
				final IComponentIdentifier remote = (IComponentIdentifier) header.getProperty(IMsgHeader.SENDER);
				
				retfut.addResultListener(new IIntermediateFutureCommandResultListener()
				{
					public void intermediateResultAvailable(Object result)
					{
						RemoteIntermediateResultCommand<?> rc = new RemoteIntermediateResultCommand(result, fsc!=null ? fsc.getProperties() : null);
						IFuture<Void>	fut	= sendRxMessage(remote, rxid, rc);
						if(term!=null)
						{
							fut.addResultListener(term);
						}
					}
					
					public void finished()
					{
						commands.remove(rxid);
						RemoteFinishedCommand<?> rc = new RemoteFinishedCommand(fsc!=null ? fsc.getProperties() : null);
						sendRxMessage(remote, rxid, rc);
					}
					
					public void resultAvailable(Object result)
					{
						commands.remove(rxid);
						RemoteResultCommand<?> rc = new RemoteResultCommand(result, fsc!=null ? fsc.getProperties() : null);
						sendRxMessage(remote, rxid, rc).addResultListener(new IResultListener<Void>()
						{
							@Override
							public void exceptionOccurred(Exception exception)
							{
								// Serialization of result failed -> send back exception.
								RemoteResultCommand<?> rc = new RemoteResultCommand(exception, fsc!=null ? fsc.getProperties() : null);
								sendRxMessage(remote, rxid, rc);
							}
							
							@Override
							public void resultAvailable(Void result)
							{
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
						commands.remove(rxid);
						RemoteResultCommand<?> rc = new RemoteResultCommand(exception, fsc!=null ? fsc.getProperties() : null);
						sendRxMessage(remote, rxid, rc);
					}
					
					public void commandAvailable(Object command)
					{
						IFuture<Void>	fut	= sendRxMessage(remote, rxid, new RemoteForwardCmdCommand(command));
						if(term!=null)
						{
							fut.addResultListener(term);
						}
					}
				});
			}
			else if(msg instanceof IRemoteConversationCommand)
			{
				IFuture<?> fut = commands.get(rxid);
				if(fut!=null)
				{
					IRemoteConversationCommand<?> cmd = (IRemoteConversationCommand<?>)msg;
					if(cmd instanceof AbstractInternalRemoteCommand)
					{
						Map<String, Object>	nonfunc	= ((AbstractInternalRemoteCommand)cmd).getProperties();
						if(nonfunc!=null)
						{
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
					}
					cmd.execute(component, (IFuture)fut, secinfos);
				}
				else
				{
					getComponent().getLogger().warning("Outdated remote command: "+msg);
				}
			}
			else if(header.getProperty(MessageComponentFeature.EXCEPTION)!=null)
			{
				// Message could not be delivered -> remove the future (and terminate, if terminable)
				IFuture<?> fut = commands.remove(rxid);
				if(fut instanceof ITerminableFuture)
				{
					((ITerminableFuture)fut).terminate((Exception)header.getProperty(MessageComponentFeature.EXCEPTION));
				}
			}
			else
			{
				getComponent().getLogger().warning("Invalid remote execution message: "+header+", "+msg);
			}
		}
	}
}
