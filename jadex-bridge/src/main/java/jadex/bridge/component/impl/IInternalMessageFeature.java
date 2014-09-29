package jadex.bridge.component.impl;

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
}
