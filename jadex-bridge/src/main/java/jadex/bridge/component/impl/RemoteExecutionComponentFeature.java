package jadex.bridge.component.impl;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMessageHandler;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.IRemoteCommand;
import jadex.bridge.component.IRemoteExecutionFeature;
import jadex.bridge.component.impl.remotecommands.RemoteFinishedCommand;
import jadex.bridge.component.impl.remotecommands.RemoteIntermediateResultCommand;
import jadex.bridge.component.impl.remotecommands.RemoteMethodInvocationCommand;
import jadex.bridge.component.impl.remotecommands.RemoteReference;
import jadex.bridge.component.impl.remotecommands.RemoteResultCommand;
import jadex.bridge.component.impl.remotecommands.RemoteSearchCommand;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
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
	 *  @return	The result(s) of the command, if any.
	 */
	public <T> IFuture<T>	execute(IComponentIdentifier target, IRemoteCommand<T> command)
	{
		@SuppressWarnings("unchecked")
		Future<T> ret = (Future<T>) SFuture.getFuture(command.getReturnType(getComponent()));
		
		final String rxid = SUtil.createUniqueId("");
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
		return execute(target, new RemoteSearchCommand<T>(query));
	}

	/**
	 *  Invoke a method on a remote object.
	 *  @param ref	The target reference.
	 *  @param method	The method to be executed.
	 *  @param args	The arguments.
	 *  @return	The result(s) of the method invocation, if any. Connects any futures involved.
	 */
	public Object	executeRemoteMethod(RemoteReference ref, Method method, Object[] args)
	{
		return execute(ref.getRemoteComponent(), new RemoteMethodInvocationCommand(ref.getTargetIdentifier(), method, args));
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
						RemoteIntermediateResultCommand<?> rc = new RemoteIntermediateResultCommand(result);
						IFuture<Void>	fut	= sendRxMessage(remote, rxid, rc);
						if(term!=null)
						{
							fut.addResultListener(term);
						}
					}
					
					public void finished()
					{
						commands.remove(rxid);
						RemoteFinishedCommand<?> rc = new RemoteFinishedCommand();
						sendRxMessage(remote, rxid, rc);
					}
					
					public void resultAvailable(Object result)
					{
						commands.remove(rxid);
						RemoteResultCommand<?> rc = new RemoteResultCommand(result);
						sendRxMessage(remote, rxid, rc).addResultListener(new IResultListener<Void>()
						{
							@Override
							public void exceptionOccurred(Exception exception)
							{
								exception.printStackTrace();
							}
							
							@Override
							public void resultAvailable(Void result)
							{
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
						RemoteResultCommand<?> rc = new RemoteResultCommand(exception);
						sendRxMessage(remote, rxid, rc);
					}
					
					public void commandAvailable(Object command)
					{
						IFuture<Void>	fut	= sendRxMessage(remote, rxid, command);
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
				IRemoteConversationCommand<?> cmd = (IRemoteConversationCommand<?>)msg;
				cmd.execute(component, (IFuture)fut, secinfos);
			}
			else if(header.getProperty(MessageComponentFeature.EXCEPTION)!=null)
			{
				IFuture<?> fut = commands.get(rxid);
				if(fut instanceof ITerminableFuture)
				{
					((ITerminableFuture)fut).terminate((Exception)header.getProperty(MessageComponentFeature.EXCEPTION));
					commands.remove(rxid);
				}
			}
			else
			{
				getComponent().getLogger().warning("Invalid remote execution message: "+header+", "+msg);
			}
		}
	}
}
