package jadex.base.service.message.transport;

import jadex.base.service.message.ISendTask;
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
	 *  Test if a transport is applicable for the message.
	 *  
	 *  @return True, if the transport is applicable for the message.
	 */
	public boolean	isApplicable(ISendTask task);
	
	/**
	 *  Send a message to receivers on a specific target platform.
	 *  This method is called for all applicable transports.
	 *  Each transport should asynchronously try to connect to the target platform
	 *  (or reuse an existing connection) and afterwards call-back the ready() method on the send task.
	 *  
	 *  The send manager calls the send commands of the transports and makes sure that the message
	 *  gets sent only once (i.e. call send commands sequentially and stop, when a send command finished successfully).
	 *  
	 *  All transports may keep any established connections open for later messages.
	 *  
	 *  @param task A task representing the message to send.
	 */
	public void	sendMessage(ISendTask task);
	
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
