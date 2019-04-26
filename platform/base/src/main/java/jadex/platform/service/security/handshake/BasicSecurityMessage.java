package jadex.platform.service.security.handshake;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;

/**
 *  Base class for security messages.
 *
 */
public class BasicSecurityMessage
{
	/** The message sender. */
	protected IComponentIdentifier sender;
	
	/** The conversation ID. */
	protected String conversationid;
	
	/** The unique message ID to filter duplicates. */
	protected String messageid;
	
	/**
	 *  Create message.
	 */
	public BasicSecurityMessage()
	{
	}
	
	/**
	 *  Create message.
	 */
	public BasicSecurityMessage(IComponentIdentifier sender, String conversationid)
	{
		this.sender = sender;
		this.conversationid = conversationid;
	}

	/**
	 *  Gets the sender.
	 * 
	 *  @return The sender
	 */
	public IComponentIdentifier getSender()
	{
		return sender;
	}

	/**
	 *  Sets the sender.
	 * 
	 *  @param sender The sender to set.
	 */
	public void setSender(IComponentIdentifier sender)
	{
		this.sender = sender;
	}
	
	/**
	 *  Gets the conversation ID.
	 *  
	 *  @return The conversation ID.
	 */
	public String getConversationId()
	{
		return conversationid;
	}
	
	/**
	 *  Sets the conversation ID.
	 *  
	 *  @param conversationid The conversation ID.
	 */
	public void setConversationId(String conversationid)
	{
		this.conversationid = conversationid;
	}
	
	/**
	 *  Get the message ID.
	 *  @return Message ID.
	 */
	public String getMessageId()
	{
		return messageid;
	}
	
	/**
	 *  Sets the message ID.
	 *  @param messageid The message ID.
	 */
	public void setMessageId(String messageid)
	{
		System.out.println("SET MSG ID: " + messageid + " " + this.messageid);
		this.messageid = messageid;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName()+"(sender="+getSender()+", convid="+getConversationId()+")";
	}
}
