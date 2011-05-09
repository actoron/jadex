package jadex.base.service.message.transport;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

import java.util.Map;

/**
 *  Interface for Jadex Standalone transports.
 */
public interface ITransport
{
	//-------- constants --------
		
	/** The receiving port (if any). */
	public final static String PORT = "port";
	
	//-------- methods --------
	
	/**
	 *  Start the transport.
	 */
	public IFuture start();

	/**
	 *  Perform cleanup operations (if any).
	 */
	public IFuture shutdown();
	
	/**
	 *  Send a message to receivers on the same platform.
	 *  @param message The message to send.
	 *  @return A future indicating if sending was successful.
	 */
	public IFuture	sendMessage(Map message, String msgtype, IComponentIdentifier[] receivers, byte[] codecids);
	
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
