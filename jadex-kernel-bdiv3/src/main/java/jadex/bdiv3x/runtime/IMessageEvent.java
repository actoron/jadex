package jadex.bdiv3x.runtime;

/**
 *  The interface for all message events (concrete and referenced).
 */
public interface IMessageEvent<T> extends IParameterElement
{
	//-------- methods --------

	/**
	 *  Get the native (platform specific) message object.
	 *  @return The native message.
	 */
	public T getMessage();
//
//	/**
//	 *  Get the message type.
//	 *  @return The message type.
//	 */
//	public MessageType getMessageType();
//	
	/**
	 *  Get the message direction.
	 *  @return True, if message is incoming.
	 */
	//public boolean isIncoming();

	/**
	 *  Get the content.
	 *  Allowed content objects depend on the platform.
	 *  @return The content.
	 */
//	public Object getContent();

	/**
	 *  Set the content.
	 *  Allowed content objects depend on the platform.
	 *  @param content The content.
	 * /
	public void setContent(Object content);*/

	/**
	 *  Create a reply to this message event.
	 *  @param type	The reply message event type (defined in the ADF).
	 *  @return The reply event.
	 * /
	public IMessageEvent	createReply(String type);*/

	/**
	 *  Create a reply to this message event.
	 *  @param type	The reply message event type (defined in the ADF).
	 *  @param content	The message content.
	 *  @return The reply event.
	 */
//	public IMessageEvent	createReply(String type, Object content);

	/**
	 *  Get the filter to wait for a reply.
	 *  @return The filter.
	 */
//	public IFilter getFilter();
	
	//-------- listeners --------
	
//	/**
//	 *  Add a message event listener.
//	 *  @param listener The message event listener.
//	 */
//	public void addMessageEventListener(IMessageEventListener listener);
//	
//	/**
//	 *  Remove a message event listener.
//	 *  @param listener The message event listener.
//	 */
//	public void removeMessageEventListener(IMessageEventListener listener);
}
