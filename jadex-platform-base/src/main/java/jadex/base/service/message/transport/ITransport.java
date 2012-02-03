package jadex.base.service.message.transport;

import jadex.base.service.message.ManagerSendTask;
import jadex.commons.concurrent.Token;
import jadex.commons.future.IFuture;


/**
 *  Interface for Jadex Standalone transports.
 */
public interface ITransport
{
	//-------- methods --------
	
	/**
	 *  Start the transport.
	 */
	public IFuture<Void> start();

	/**
	 *  Perform cleanup operations (if any).
	 */
	public IFuture<Void> shutdown();
	
	/**
	 *  Send a message to receivers on the same platform.
	 *  This method is called concurrently for all transports.
	 *  Each transport should try to connect to the target platform
	 *  (or reuse an existing connection) and afterwards acquire the token.
	 *  
	 *  The one transport that acquires the token (i.e. the first connected transport) gets to send the message.
	 *  All other transports ignore the current message and return an exception,
	 *  but may keep any established connections open for later messages.
	 *  
	 *  @param task The message to send.
	 *  @param token The token to be acquired before sending. 
	 *  @return A future indicating successful sending or exception, when the message was not send by this transport.
	 */
	public IFuture<Void>	sendMessage(ManagerSendTask task, Token token);
	
	/**
	 *  Returns the prefix of this transport
	 *  @return Transport prefix.
	 */
	public String getServiceSchema();
	
	/**
	 *  Get the addresses of this transport.
	 *  @return An array of strings representing the addresses 
	 *  of this message transport mechanism.
	 */
	public String[] getAddresses();
}
