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
	 *  Send a message.
	 *  @param message The message to send.
	 *  @return The component identifiers to which this 
	 *  message could not be delivered.
	 */
	public IComponentIdentifier[] sendMessage(Map message, String msgtype, IComponentIdentifier[] receivers, byte[] codecids);
	
	/**
	 *  Returns the prefix of this transport
	 *  @return Transport prefix.
	 */
	public String getServiceSchema();
	
	/**
	 *  Get the adresses of this transport.
	 *  @return An array of strings representing the addresses 
	 *  of this message transport mechanism.
	 */
	public String[] getAddresses();
}
