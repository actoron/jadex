package jadex.bridge.component;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.IFuture;

/**
 *  Feature for sending messages and handling incoming messages via handlers.
 */
public interface IMessageFeature
{
	/**
	 *  Send a message.
	 *  @param receiver	The message receiver.
	 *  @param message	The message.
	 *  
	 */
	public IFuture<Void> sendMessage(IComponentIdentifier receiver, Object message);
	
	/**
	 *  Send a message.
	 *  @param receiver	The message receiver.
	 *  @param message	The message.
	 *  @param addheaderfields Additional header fields.
	 *  
	 */
	public IFuture<Void> sendMessage(IComponentIdentifier receiver, Object message, Map<String, Object> addheaderfields);
	
	/**
	 *  Send a message and wait for a reply.
	 *  
	 *  @param receiver	The message receiver.
	 *  @param message	The message.
	 *  
	 *  @return The reply.
	 */
	public IFuture<Object> sendMessageAndWait(IComponentIdentifier receiver, Object message);
	
	/**
	 *  Send a message and wait for a reply.
	 *  
	 *  @param receiver	The message receiver.
	 *  @param message	The message.
	 *  @param timeout	The reply timeout.
	 *  
	 *  @return The reply.
	 */
	public IFuture<Object> sendMessageAndWait(IComponentIdentifier receiver, Object message, Long timeout);
	
	/**
	 *  Send a message reply.
	 *  @param receivedmessageid	ID of the received message that is being replied to.
	 *  @param message	The reply message.
	 *  
	 */
	public IFuture<Void> sendReply(IMsgHeader header, Object message);
	
	/**
	 *  Add a message handler.
	 *  @param  The handler.
	 */
	public void addMessageHandler(IMessageHandler handler);
	
	/**
	 *  Remove a message handler.
	 *  @param handler The handler.
	 */
	public void removeMessageHandler(IMessageHandler handler);
	
	/**
	 *  Create a virtual output connection.
	 *  @param sender The sender.
	 *  @param receiver The receiver.
	 *  @param nonfunc The nonfunc props.
	 */
	public IFuture<IOutputConnection> createOutputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc);

	/**
	 *  Create a virtual input connection.
	 *  @param sender The sender.
	 *  @param receiver The receiver.
	 *  @param nonfunc The nonfunc props.
	 */
	public IFuture<IInputConnection> createInputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc);
}
