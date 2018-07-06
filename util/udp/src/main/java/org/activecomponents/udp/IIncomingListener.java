package org.activecomponents.udp;

import java.net.SocketAddress;

/**
 *  Listener for incoming messages and packets.
 *
 */
public interface IIncomingListener
{
	/** Receive an incoming packet. */
	public void receivePacket(SocketAddress remoteaddress, byte[] data);
	
	/** Receive an incoming message. */
	public void receiveMessage(SocketAddress remoteaddress, byte[] data);
}
