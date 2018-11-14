package jadex.bridge.component;

import jadex.bridge.IComponentIdentifier;

/**
 *   Interface for message headers with meta information / link-level security.
 *
 */
public interface IMsgHeader
{
	/** Message header key for the sender. */
	public static final String SENDER = "sender";
	
	/** Message header key for the receiver. */
	public static final String RECEIVER = "receiver";
	
	/** Message header key for conversation IDs (optional). */
	public static final String CONVERSATION_ID = "convid";

	/** Message header key for internal message id (optional).
	 *  Added automatically when monitoring is active on sender side. */
	public static final String XID = "x_message_id";
	
	/**
	 *  Gets the sender of the message.
	 * 
	 *  @return The sender.
	 */
	public IComponentIdentifier getSender();
	
	/**
	 *  Gets the receiver of the message.
	 * 
	 *  @return The receiver.
	 */
	public IComponentIdentifier getReceiver();
	
	/**
	 *  Gets a property stored in the header.
	 *  
	 *  @param propertyname
	 * @return
	 */
	public Object getProperty(String propertyname);
	
	/**
	 *  Adds a header property to the header.
	 *  
	 *  @param propname The property name.
	 *  @param propval The property value.
	 */
	public void addProperty(String propname, Object propval);
}
