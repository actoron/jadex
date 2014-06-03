package jadex.bridge.component;

import jadex.bridge.IConnection;
import jadex.bridge.IMessageAdapter;

/**
 *  A component feature for message and stream-based communication.
 */
public interface ICommunicationFeature
{
	/**
	 *  Inform the component that a message has arrived.
	 *  @param message The message that arrived.
	 */
	public void messageArrived(IMessageAdapter message);

	/**
	 *  Inform the component that a stream has arrived.
	 *  @param con The stream that arrived.
	 */
	public void streamArrived(IConnection con);
}
