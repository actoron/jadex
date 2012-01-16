package jadex.base.service.message.transport;

import jadex.base.service.message.ManagerSendTask;
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
	 *  @param message The message to send.
	 *  @return A future indicating if sending was successful.
	 */
//	public IFuture<Void>	sendMessage(Map<String, Object> message, String msgtype, IComponentIdentifier[] receivers, byte[] codecids);
	public IFuture<Void>	sendMessage(ManagerSendTask task);
	
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
