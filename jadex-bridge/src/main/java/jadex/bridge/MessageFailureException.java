package jadex.bridge;

import java.util.Map;

import jadex.bridge.service.types.message.MessageType;

/**
 *  An exception indicating a message failure (e.g. unknown receiver).
 */
public class MessageFailureException	extends RuntimeException
{
	//-------- attributes --------
	
	/** The message. */
	protected Object message;

	/** The message type. */
	protected MessageType messagetype;
	
	/** The receivers that could not be reached. */
	protected IComponentIdentifier[] unknown;
	
	//----- constructors --------
	
	/**
	 *  Create a new MessageFailureException.
	 */
	public MessageFailureException(Object message, MessageType type, IComponentIdentifier[] unknown, Throwable cause)
	{
		super(null, cause);
		this.message = message;
		this.messagetype = type;
		this.unknown = unknown;
	}

	/**
	 *  Create a new MessageFailureException.
	 */
	public MessageFailureException(Object message, MessageType type, IComponentIdentifier[] unknown, String text)
	{
		super(text, null);
		this.message = message;
		this.messagetype = type;
		this.unknown = unknown;
	}

	//-------- methods --------

	/**
	 *  Get the message.
	 *  @return the message.
	 */
	public Object getMessageEvent()
	{
		return message;
	}

	/**
	 *  Set the message.
	 *  @param message The message to set.
	 */
	public void setMessageEvent(Map message)
	{
		this.message = message;
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
	 *  Set the messagetype.
	 *  @param messagetype The messagetype to set.
	 */
	public void setMessageType(MessageType messagetype)
	{
		this.messagetype = messagetype;
	}

	/**
	 *  Get the unknown.
	 *  @return the unknown.
	 */
	public IComponentIdentifier[] getUnknownReceivers()
	{
		return unknown;
	}

	/**
	 *  Set the unknown.
	 *  @param unknown The unknown to set.
	 */
	public void setUnknownReceivers(IComponentIdentifier[] unknown)
	{
		this.unknown = unknown;
	}
	
//	public void printStackTrace()
//	{
//		Thread.dumpStack();
//		super.printStackTrace();
//	}

}
