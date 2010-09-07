package jadex.standalone.service;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.MessageType;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;

/**
 * 
 */
public class ManagerSendTask
{
	/** The overall send task. */
	protected SendTask sendtask;
	
	/** The managed receivers. */
	protected IComponentIdentifier[] receivers;

	/** The target manager. */
	protected TargetManager manager;
	
	/**
	 *  Create a new manager send task.
	 */
	public ManagerSendTask(SendTask sendtask, IComponentIdentifier[] receivers, TargetManager manager)
	{
		this.sendtask = sendtask;
		this.receivers = receivers;
		this.manager = manager;
	}

	/**
	 * 
	 */
	public IFuture execute()
	{
		Future ret = new Future();
		manager.addMessage(this).addResultListener(new DelegationResultListener(ret));
		return ret;
	}
	
	/**
	 *  Get the sendtask.
	 *  @return the sendtask.
	 */
	public SendTask getSendTask()
	{
		return sendtask;
	}
	
	/**
	 *  Get the message.
	 *  @return the message.
	 */
	public Map getMessage()
	{
		return sendtask.getMessage();
	}

	/**
	 *  Get the messagetype.
	 *  @return the messagetype.
	 */
	public MessageType getMessageType()
	{
		return sendtask.getMessageType();
	}

	/**
	 *  Get the receivers.
	 *  @return the receivers.
	 */
	public IComponentIdentifier[] getReceivers()
	{
		return receivers;
	}

	/**
	 *  Get the manager.
	 *  @return the manager.
	 */
	public TargetManager getTargetManager()
	{
		return manager;
	}

}
