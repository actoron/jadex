package jadex.bridge.component;

import jadex.bridge.IComponentIdentifier;
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
	 *  Send a message and wait for a reply.
	 *  @param receiver	The message receiver.
	 *  @param message	The message.
	 *  
	 */
	public IFuture<Void> sendMessageAndWait(IComponentIdentifier receiver, Object message);
	
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
