package jadex.platform.service.message.transport;

import java.util.Map;

import jadex.commons.future.IFuture;
import jadex.platform.service.message.ISendTask;


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
	 *  Test if a transport is applicable for the target address.
	 *  @return True, if the transport is applicable for the address.
	 */
	public boolean	isApplicable(String address);
	
	/**
	 *  Test if a transport satisfies the non-functional requirements.
	 *  @param nonfunc	The non-functional requirements (name, value).
	 *  @param address	The transport address.
	 *  @return True, if the transport satisfies the non-functional requirements.
	 */
	public boolean	isNonFunctionalSatisfied(Map<String, Object> nonfunc, String address);
	
	/**
	 *  Send a message to the given address.
	 *  This method is called multiple times for the same message, i.e. once for each applicable transport / address pair.
	 *  The transport should asynchronously try to connect to the target address
	 *  (or reuse an existing connection) and afterwards call-back the ready() method on the send task.
	 *  
	 *  The send manager calls the obtained send commands of the transports and makes sure that the message
	 *  gets sent only once (i.e. call send commands sequentially and stop, when a send command finished successfully).
	 *  
	 *  All transports may keep any established connections open for later messages.
	 *  
	 *  @param address The address to send to.
	 *  @param task A task representing the message to send.
	 */
	public void	sendMessage(String address, ISendTask task);
	
	/**
	 *  Returns the prefixes of this transport
	 *  @return Transport prefixes.
	 */
	public String[] getServiceSchemas();
	
	/**
	 *  Get the addresses of this transport.
	 *  @return An array of strings representing the addresses 
	 *  of this message transport mechanism.
	 */
	public String[] getAddresses();
}
