package jadex.bridge;


/**
 *  Interface for listening on message traffic.
 */
public interface IMessageListener
{
	/**
	 *  Invoked when a message event has been received.
	 *  @param msg The message adapter.
	 */
	public void messageReceived(IMessageAdapter msg);
	
	/**
	 *  Invoked when a message event has been sent.
	 *  @param msg The message adapter.
	 */
	public void messageSent(IMessageAdapter msg);
}
