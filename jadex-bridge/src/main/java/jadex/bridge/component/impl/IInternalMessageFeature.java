package jadex.bridge.component.impl;

import jadex.bridge.IConnection;
import jadex.bridge.IMessageAdapter;

/**
 *  A component feature for message-based communication.
 */
public interface IInternalMessageFeature
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
