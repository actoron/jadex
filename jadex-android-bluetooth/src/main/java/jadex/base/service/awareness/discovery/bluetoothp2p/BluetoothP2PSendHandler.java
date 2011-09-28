package jadex.base.service.awareness.discovery.bluetoothp2p;

import jadex.base.service.awareness.AwarenessInfo;
import jadex.base.service.awareness.discovery.DiscoveryAgent;
import jadex.base.service.awareness.discovery.SendHandler;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 *  Handle sending.
 */
public class BluetoothP2PSendHandler extends SendHandler
{
	/**
	 *  Create a new lease time handling object.
	 */
	public BluetoothP2PSendHandler(DiscoveryAgent agent)
	{
		super(agent);
	}
	
	/**
	 *  Method to send messages.
	 */
	public void send(AwarenessInfo info)
	{
	}
	
	/**
	 *  Send a packet.
	 */
	public boolean send(byte[] data, InetAddress address, int port)
	{
//		System.out.println("sent packet: "+address+" "+port);
		boolean ret = true;
		try
		{
			DatagramPacket p = new DatagramPacket(data, data.length, new InetSocketAddress(address, port));
			getAgent().send(p);
		}
		catch(Exception e)
		{
			ret = false;
		}
		return ret;
	}
	
	/**
	 *  Get the agent.
	 */
	protected BluetoothP2PDiscoveryAgent getAgent()
	{
		return (BluetoothP2PDiscoveryAgent)agent;
	}
}