package jadex.bridge.component.impl;

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
import jadex.bridge.component.impl.remotecommands.RemoteSearchCommand;
import jadex.bridge.component.impl.remotecommands.ResultCommand;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IIntermediateFutureCommandResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.TerminableFuture;

/**
 *  Feature for securely sending and handling remote execution commands.
 */
public class RemoteExecutionComponentFeature extends AbstractComponentFeature implements IRemoteExecutionFeature, IInternalRemoteExecutionFeature
{
	//-------- constants ---------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(
		IRemoteExecutionFeature.class, RemoteExecutionComponentFeature.class);
	
	/** ID of the remote execution command in progress. */
	protected static final String RX_ID = "__rx_id__";
	
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
		Class<?> returntype;
		try
		{
			returntype = command.getClass().getMethod("execute", IInternalAccess.class, IMsgSecurityInfos.class).getReturnType();
		}
		catch (Exception e)
		{
			return new Future<T>(e);
		}
		
		@SuppressWarnings("unchecked")
		Future<T> ret = (Future<T>) SFuture.getFuture(returntype);
		ret.addResultListener(new IFutureCommandResultListener<T>()
		{
			public void resultAvailable(T result)
			{
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
			
			public void commandAvailable(Object command)
			{
			}
		});
		
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
	public <T> IFuture<Set<T>>	executeRemoteSearch(IComponentIdentifier target, ServiceQuery<T> query)
	{
		return execute(target, new RemoteSearchCommand<T>(query));
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
			if (msg instanceof IRemoteCommand && header.getProperty(RX_ID) instanceof String)
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
			IFuture<?> retfut = commands == null ? null : commands.get(rxid);
			if(retfut == null)
			{
				IRemoteCommand<?> cmd = (IRemoteCommand<?>)msg;
				retfut = cmd.execute(component, secinfos);
				if (commands == null)
					commands = new HashMap<String, IFuture<?>>();
				commands.put(rxid, retfut);
				
				final IComponentIdentifier remote = (IComponentIdentifier) header.getProperty(IMsgHeader.SENDER);
				
				retfut.addResultListener(new IIntermediateFutureCommandResultListener()
				{
					public void intermediateResultAvailable(Object result)
					{
//							System.out.println("inter: "+result);
//							ret.addIntermediateResult(new RemoteIntermediateResultCommand(rec, getSender(), result, callid, 
//								returnisref, methodname, false, getNFProps(true), (IFuture<?>)res, cnt++));
					}
					
					public void finished()
					{
//							System.out.println("fin");
//							ret.addIntermediateResult(new RemoteIntermediateResultCommand(rec, getSender(), null, callid, 
//								returnisref, methodname, true, getNFProps(false), (IFuture<?>)res, cnt));
//							ret.setFinished();
//							rsms.removeProcessingCall(callid);
					}
					
					public void resultAvailable(Object result)
					{
						commands.remove(rxid);
						ResultCommand<?> rc = new ResultCommand(result);
						sendRxMessage(remote, rxid, rc);
					}
					
					public void resultAvailable(Collection result)
					{
//							System.out.println("ra");
//							ret.addIntermediateResult(new RemoteResultCommand(rec, getSender(), result, null, callid, 
//								returnisref, methodname, getNFProps(false)));
//							ret.setFinished();
//							rsms.removeProcessingCall(callid);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						commands.remove(rxid);
						ResultCommand<?> rc = new ResultCommand(exception);
						sendRxMessage(remote, rxid, rc);
					}
					
					public void commandAvailable(Object command)
					{
						sendRxMessage(remote, rxid, command).addResultListener(new IResultListener<Void>()
						{
							public void exceptionOccurred(Exception exception)
							{
								IFuture<?> fut = commands.remove(rxid);
								if (fut instanceof TerminableFuture)
									((TerminableFuture) fut).terminate();
							}
							
							public void resultAvailable(Void result)
							{
							}
						});
					}
				});
			}
			else
			{
				IRemoteConversationCommand<?> cmd = (IRemoteConversationCommand<?>)msg;
				cmd.execute(component, (IFuture)retfut, secinfos);
			}
		}
	}
}
