package org.activecomponents.udp.holepunching.server.webcommands;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.activecomponents.udp.holepunching.server.IConnectedHost;
import org.activecomponents.udp.holepunching.server.IRegisteredHost;

/** Host connected via http. */
public class WebConnectedHost implements IConnectedHost
{
	protected String remoteaddr;
	
	protected Map<String, IRegisteredHost> registeredhosts;
	
	protected DatagramSocket dgsocket;
	
	public WebConnectedHost(String remoteaddr, Map<String, IRegisteredHost> registeredhosts, DatagramSocket dgsocket)
	{
		this.remoteaddr = remoteaddr;
		this.registeredhosts = registeredhosts;
		this.dgsocket = dgsocket;
	}

	/**
	 *  Retrieves the registered hosts.
	 *  
	 *  @return The registered hosts.
	 */
	public Map<String, IRegisteredHost> getRegisteredHosts()
	{
//		Map<String, IRegisteredHost> ret = (Map<String, IRegisteredHost>) getServletContext().getAttribute("registeredhosts");
//		return ret;
		return registeredhosts;
	}
	
	/**
	 *  Gets the address of the connected host.
	 *  @return The address.
	 */
	public InetAddress getRemoteAddress()
	{
		try
		{
			return InetAddress.getByName(remoteaddr);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *  Retrieves the UDP socket for testing communication.
	 *  
	 *  @return The UDP socket.
	 */
	public DatagramSocket getUdpSocket()
	{
//		DatagramSocket ret = (DatagramSocket) getServletContext().getAttribute("dgsocket");
//		return ret;
		return dgsocket;
	}
}
