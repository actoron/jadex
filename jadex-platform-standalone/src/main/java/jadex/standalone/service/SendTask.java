package jadex.standalone.service;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IMessageService;
import jadex.bridge.MessageType;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.DelegationResultListener;

import java.util.Iterator;
import java.util.Map;

/**
 * 
 */
public class SendTask
{
	/** The message service. */
	protected IMessageService msgservice;
	
	/** The message map. */
	protected Map message;
	
	/** The message type. */
	protected MessageType messagetype;
	
	/**
	 * 
	 */
	public SendTask(IMessageService msgservice, Map message, MessageType messagetype)
	{
		this.msgservice = msgservice;
		this.message = message;
		this.messagetype = messagetype;
	}

	/**
	 * 
	 */
	public IFuture execute()
	{
		final Future ret = new Future();
		
		// Determine manager tasks

		ManagerSendTask[] tasks = createManagerTasks();
		
		CollectionResultListener lis = new CollectionResultListener(tasks.length, false, new DelegationResultListener(ret));
		
		for(int i=0; i<tasks.length; i++)
		{
			tasks[i].execute().addResultListener(lis);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public ManagerSendTask[] createManagerTasks()
	{
		MultiCollection managers = new MultiCollection();
		
		String recid = messagetype.getReceiverIdentifier();
		
		for(Iterator it = SReflect.getIterator(message.get(recid)); it.hasNext(); )
		{
			IComponentIdentifier cid = (IComponentIdentifier)it.next();
			TargetManager tm = ((MessageService)msgservice).getTargetManager(cid); // todo: HACK!!!
			managers.put(tm, cid);
		}
		
		ManagerSendTask[] ret = new ManagerSendTask[managers.size()];
		int i=0;
		for(Iterator it=managers.keySet().iterator(); it.hasNext(); i++)
		{
			TargetManager tm = (TargetManager)it.next();
			IComponentIdentifier[] recs = (IComponentIdentifier[])managers.getCollection(tm)
				.toArray(new IComponentIdentifier[0]);
			ret[i] = new ManagerSendTask(this, recs, tm);
		}
		
		return ret;
	}
	
	/**
	 *  Get the message.
	 *  @return the message.
	 */
	public Map getMessage()
	{
		return message;
	}

	/**
	 *  Get the messagetype.
	 *  @return the messagetype.
	 */
	public MessageType getMessageType()
	{
		return messagetype;
	}
}
