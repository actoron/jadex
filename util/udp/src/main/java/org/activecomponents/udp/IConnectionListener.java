package org.activecomponents.udp;

/**
 *  Listener for new connections.
 *
 */
public interface IConnectionListener
{
	/**
	 *  Called when a new connection is established.
	 *  
	 *  @param connection The new connection.
	 */
	public void peerConnected(Connection connection);
	
	/**
	 *  Called when a connection is disconnected.
	 *  
	 *  @param connection The disconnected connection.
	 */
	public void peerDisconnected(Connection connection);
}
