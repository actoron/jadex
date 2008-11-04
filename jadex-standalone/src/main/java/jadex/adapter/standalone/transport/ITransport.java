package jadex.adapter.standalone.transport;

import jadex.adapter.standalone.fipaimpl.AgentIdentifier;
import jadex.bridge.IAgentIdentifier;

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
	public void start();

	/**
	 *  Perform cleanup operations (if any).
	 */
	public void shutdown();
	
	/**
	 *  Send a message.
	 *  @param message The message to send.
	 *  @return The agent identifiers to which this 
	 *  message could not be delivered.
	 */
//	public AgentIdentifier[] sendMessage(IMessageEnvelope message);
	public AgentIdentifier[] sendMessage(Map message, String msgtype, IAgentIdentifier[] receivers);
	
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
