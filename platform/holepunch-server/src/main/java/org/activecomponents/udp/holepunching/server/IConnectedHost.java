package org.activecomponents.udp.holepunching.server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

public interface IConnectedHost
{
	/**
	 *  Retrieves the registered hosts.
	 *  
	 *  @return The registered hosts.
	 */
	public Map<String, IRegisteredHost> getRegisteredHosts();
	
	/**
	 *  Gets the address of the connected host.
	 *  @return The address.
	 */
	public InetAddress getRemoteAddress();
	
	/**
	 *  Retrieves the UDP socket for testing communication.
	 *  
	 *  @return The UDP socket.
	 */
	public DatagramSocket getUdpSocket();
}
