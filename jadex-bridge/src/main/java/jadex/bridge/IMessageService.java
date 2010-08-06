package jadex.bridge;

import java.util.Map;

/**
 *  The interface for the message service. It is responsible for
 *  managing the transports and sending/delivering messages.
 */
public interface IMessageService
{
	/**
	 *  Send a message.
	 *  @param cl The class loader used by the sending component (i.e. corresponding to classes of objects in the message map).
	 */
	public void sendMessage(Map message, MessageType msgtype, IComponentIdentifier sender, ClassLoader cl);
	
	/**
	 *  Deliver a message to some components.
	 */
	public void deliverMessage(Map message, String msgtype, IComponentIdentifier[] receivers);
	
	/**
	 *  Create a reply to a message.
	 *  @param msg	The message.
	 *  @param mt	The message type.
	 *  @return The reply event.
	 */
	public Map createReply(Map msg, MessageType mt);

	/**
	 *  Get addresses of all transports.
	 *  @return The addresses of all transports.
	 */
	// todo: remove
	// It could be a good idea to NOT have the addresses in the component identifiers all the time.
	// Only when sending a message across platform borders the component identifiers should be 
	// enhanced with the addresses to enable the other platform answering.
	// In the local case one could always omit the addresses and try out the services.
	public String[] getAddresses();

	/**
	 *  Get the message type.
	 *  @param type The type name.
	 *  @return The message type.
	 */
	public MessageType getMessageType(String type);
	
	/**
	 *  Add a message listener.
	 *  @param listener The change listener.
	 */
	public void addMessageListener(IMessageListener listener);
	
	/**
	 *  Remove a message listener.
	 *  @param listener The change listener.
	 */
	public void removeMessageListener(IMessageListener listener);

}
