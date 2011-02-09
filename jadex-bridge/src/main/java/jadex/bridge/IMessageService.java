package jadex.bridge;

import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.commons.service.IService;

import java.util.Map;

/**
 *  The interface for the message service. It is responsible for
 *  managing the transports and sending/delivering messages.
 */
public interface IMessageService extends IService
{
	/**
	 *  Send a message.
	 *  @param map The message as key value pairs.
	 *  @param msgtype The message type.
	 *  @param sender The sender component identifier.
	 *  @param cl The class loader used by the sending component (i.e. corresponding to classes of objects in the message map).
	 *  @return Future that indicates an exception when messages could not be delivered to components. 
	 */
	public IFuture sendMessage(Map message, MessageType msgtype, IComponentIdentifier sender, ClassLoader cl);
	
	/**
	 *  Deliver a message to some components.
	 */
	public void deliverMessage(Map message, String msgtype, IComponentIdentifier[] receivers);
	

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
	 *  Get addresses of all transports.
	 *  @return The address schemes of all transports.
	 */
	public String[] getAddressSchemes();

	/**
	 *  Get the message type.
	 *  @param type The type name.
	 *  @return The message type.
	 */
	public MessageType getMessageType(String type);
		
	/**
	 *  Add a message listener.
	 *  @param listener The change listener.
	 *  @param filter An optional filter to only receive notifications for matching messages. 
	 */
	public void addMessageListener(IMessageListener listener, IFilter filter);
	
	/**
	 *  Remove a message listener.
	 *  @param listener The change listener.
	 */
	public void removeMessageListener(IMessageListener listener);

}
