package jadex.bridge;


import java.util.Map;


/**
 *  The interface for the message service. It is responsible for
 *  managing the transports and sending/delivering messages.
 */
public interface IMessageService extends IPlatformService
{
	/**
	 *  Send a message.
	 *  @param cl	The class loader used by the sending agent (i.e. corresponding to classes of objects in the message map).
	 */
	public void sendMessage(Map message, MessageType msgtype, IAgentIdentifier sender, ClassLoader cl);
	
	/**
	 *  Deliver a message to some agents.
	 */
	public void deliverMessage(Map message, String msgtype, IAgentIdentifier[] receivers);
	
	/**
	 *  Adds a transport for the message service.
	 *  @param transport The transport.
	 * /
	public void addTransport(ITransport transport);*/
	
	/**
	 *  Remove a transport for the message service.
	 *  @param transport The transport.
	 * /
	public void removeTransport(ITransport transport);*/
	
	/**
	 *  todo: better method
	 *  Change transport position.
	 *  @param up Move up?
	 *  @param transport The transport to move.
	 * /
	public void changeTransportPosition(boolean up, ITransport transport);*/
	
	/**
	 *  Get the transports.
	 *  @return The transports.
	 * /
	public ITransport[] getTransports();*/
	
	/**
	 *  Get addresses of all transports.
	 *  @return The addresses of all transports.
	 */
	public String[] getAddresses();

}
