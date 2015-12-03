package jadex.bridge.component;

import java.util.Map;

import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.IFuture;

/**
 *  Feature for sending messages and handling incoming messages via handlers.
 */
public interface IMessageFeature
{
	/**
	 *  Send a message.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	public IFuture<Void> sendMessage(Map<String, Object> me, MessageType mt);
	
	/**
	 *  Send a message.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	public IFuture<Void> sendMessage(final Map<String, Object> me, final MessageType mt, final byte[] codecids);
	
	/**
	 *  Send a message and wait for a reply.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	// Todo: supply reply message as future return value?
	public IFuture<Void> sendMessageAndWait(final Map<String, Object> me, final MessageType mt, final IMessageHandler handler);
	
	/**
	 *  Add a message handler.
	 *  @param  The handler.
	 */
	public IFuture<Void> addMessageHandler(IMessageHandler handler);
	
	/**
	 *  Remove a message handler.
	 *  @param handler The handler.
	 */
	public IFuture<Void> removeMessageHandler(IMessageHandler handler);
}
