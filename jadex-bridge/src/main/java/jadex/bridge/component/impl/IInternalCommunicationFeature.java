package jadex.bridge.component.impl;

import jadex.bridge.IConnection;

/**
 *  A component feature for message and stream-based communication.
 */
public interface IInternalCommunicationFeature
{
	/**
	 *  Inform the component that a stream has arrived.
	 *  @param con The stream that arrived.
	 */
	public void streamArrived(IConnection con);
}
