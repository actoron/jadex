package jadex.standalone.service;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.MessageFailureException;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.execution.IExecutionService;
import jadex.standalone.transport.ITransport;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class TargetManager implements IExecutable
{
	//-------- attributes --------
	
	/** The service provider. */
	protected IServiceProvider provider;
	
	/** The available transports. */
	protected ITransport[] transports;
	
	/** The list of messages to send. */
	protected List messages;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public TargetManager(IServiceProvider provider, ITransport[] transports)
	{
		this.provider = provider;
		this.transports = transports;
		this.messages = new ArrayList();
	}
	
	//-------- methods --------

	/**
	 *  Send a message.
	 */
	public boolean execute()
	{
		Object[] task = null;
		boolean isempty;
		
		synchronized(this)
		{
			if(!messages.isEmpty())
				task = (Object[])messages.remove(0);
			isempty = messages.isEmpty();
		}
		
		if(task!=null)
			internalSendMessage((ManagerSendTask)task[0], (Future)task[1]);

		return !isempty;
	}
	
	/**
	 *  Send a message.
	 *  @param message The native message.
	 */
	protected void internalSendMessage(ManagerSendTask task, Future ret)
	{
		// todo: move out
		
//		if(receivers.length == 0)
//		{
//			ret.setException(new MessageFailureException(msg, type, null, "No receiver specified"));
//			return;
//		}
//		for(int i=0; i<receivers.length; i++)
//		{
//			if(receivers[i]==null)
//			{
//				ret.setException(new MessageFailureException(msg, type, null, "A receiver nulls: "+msg));
//				return;
//			}
//		}

		// todo: transport shifting in case of no connection
		
		IComponentIdentifier[] receivers = task.getReceivers();
		
		for(int i = 0; i < transports.length && receivers.length>0; i++)
		{
			try
			{
				// Method returns component identifiers of undelivered components
//				IConnection con = transports[i].getConnection(addresses[i]);
//				if(con==null)
				
				receivers = transports[i].sendMessage(task.getMessage(), task.getMessageType().getName(), receivers);
			}
			catch(Exception e)
			{
				// todo: ?
				e.printStackTrace();
//				ret.setException(e);
			}
		}

		if(receivers.length > 0)
		{
//			logger.warning("Message could not be delivered to (all) receivers: " + SUtil.arrayToString(receivers));
			ret.setException(new MessageFailureException(task.getMessage(), task.getMessageType(), receivers, 
				"Message could not be delivered to (all) receivers: "+ SUtil.arrayToString(receivers)));
		}
		else
		{
			ret.setResult(null);
		}
	}
	
	/**
	 *  Add a message to be sent.
	 *  @param message The message.
	 */
	public IFuture addMessage(ManagerSendTask task)
	{
		final Future ret = new Future();
		
		synchronized(this)
		{
			messages.add(new Object[]{task, ret});
		}
		
		SServiceProvider.getService(provider, IExecutionService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				try
				{
					((IExecutionService)result).execute(TargetManager.this);
				}
				catch(RuntimeException e)
				{
					// ignore if execution service is shutting down.
				}						
			}
		});
		
		return ret;
	}
}
