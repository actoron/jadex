package jadex.base.service.message;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.IResultCommand;
import jadex.commons.future.IFuture;

import java.util.Map;


/**
 * Public interface of send task to be passed to transports.
 */
public interface ISendTask
{
	/**
	 * Get the message.
	 * 
	 * @return the message.
	 */
	public Map<String, Object> getMessage();

	/**
	 * Get the messagetype.
	 * 
	 * @return the messagetype.
	 */
	public MessageType getMessageType();

	/**
	 * Get the receivers.
	 * 
	 * @return the receivers.
	 */
	public IComponentIdentifier[] getReceivers();
	
	/**
	 *  Get the encoded message.
	 *  Saves the message to avoid multiple encoding with different transports.
	 */
	public byte[] getData();
	
	/**
	 *  Get the prolog bytes.
	 *  Separated from data to avoid array copies.
	 *  Message service expects messages to be delivered in the form {prolog}{data}. 
	 *  @return The prolog bytes.
	 */
	public byte[] getProlog();

	/**
	 *  Called by the transport when is is ready to send the message,
	 *  i.e. when a connection is established.
	 *  @param send	The code to be executed to send the message.
	 */
	public void ready(IResultCommand<IFuture<Void>, Void> send);
}
