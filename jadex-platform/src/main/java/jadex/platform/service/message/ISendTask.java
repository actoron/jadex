package jadex.platform.service.message;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.IResultCommand;
import jadex.commons.future.IFuture;


/**
 * Public interface of send task to be passed to transports.
 */
public interface ISendTask
{
	/**
	 *  Get the message.
	 *  @return the message.
	 */
//	public Object getMesssage();
	
	/**
	 *  Get the encoded message.
	 *  @return the encoded message.
	 */
	public byte[] getEncodedMessage();
	
	/**
	 *  Get the raw message.
	 *  @return the raw message, implementation-dependent.
	 */
	public Object getRawMessage();

//	/**
//	 *  Get the message type.
//	 *  @return the message type.
//	 */
//	public MessageType getMessageType();

	/**
	 *  Get the receivers.
	 *  @return the receivers.
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
	 *  
	 *  The send manager calls the send commands of the transports and makes sure that the message
	 *  gets sent only once (i.e. call send commands sequentially and stop, when one send command finished successfully).
	 *  
	 *  @param send	The command to be executed to send the message with the transport.
	 *  	The result future of the command should indicate
	 *  	when/if message sending was successful. 
	 */
	public void ready(IResultCommand<IFuture<Void>, Void> send);
	
	/**
	 *  Get the non-functional requirements.
	 *  @return The non-functional properties.
	 */
	public Map<String, Object> getNonFunctionalProperties();
}
