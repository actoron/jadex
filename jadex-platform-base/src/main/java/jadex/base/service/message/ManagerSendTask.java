package jadex.base.service.message;

import jadex.base.service.message.MessageService.SendManager;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.MessageType;

import java.util.Map;

/**
 * 
 */
public class ManagerSendTask
{
	/** The message. */
	protected Map message;
	
	/** The message type. */
	protected MessageType messagetype;
	
	/** The codecids. */
	protected byte[] codecids;
	
	/** The managed receivers. */
	protected IComponentIdentifier[] receivers;

	/** The target manager. */
	protected SendManager manager;
	
	/**
	 *  Create a new manager send task.
	 */
	public ManagerSendTask(Map message, MessageType messagetype, IComponentIdentifier[] receivers, byte[] codecids, SendManager manager)
	{
		this.message = message;
		this.messagetype = messagetype;
		this.receivers = receivers;
		this.codecids = codecids;
		this.manager = manager;
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
	public SendManager getSendManager()
	{
		return manager;
	}


	/**
	 *  Get the codecids.
	 *  @return the codecids.
	 */
	public byte[] getCodecIds()
	{
		return codecids;
	}

	/**
	 *  Set the codecids.
	 *  @param codecids The codecids to set.
	 */
	public void setCodecIds(byte[] codecids)
	{
		this.codecids = codecids;
	}
}
