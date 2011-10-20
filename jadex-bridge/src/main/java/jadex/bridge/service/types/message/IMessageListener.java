package jadex.bridge.service.types.message;

import jadex.bridge.IMessageAdapter;
import jadex.commons.future.IFuture;


/**
 *  Interface for listening on message traffic.
 */
public interface IMessageListener
{
	/**
	 *  Invoked when a message event has been received.
	 *  @param msg The message adapter.
	 */
	public IFuture messageReceived(IMessageAdapter msg);
	
	/**
	 *  Invoked when a message event has been sent.
	 *  @param msg The message adapter.
	 */
	public IFuture messageSent(IMessageAdapter msg);
}
