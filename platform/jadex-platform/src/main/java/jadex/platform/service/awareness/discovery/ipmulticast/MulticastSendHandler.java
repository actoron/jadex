package jadex.platform.service.awareness.discovery.ipmulticast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.platform.service.awareness.discovery.DiscoveryAgent;
import jadex.platform.service.awareness.discovery.SendHandler;

/**
 *  Handle sending.
 */
public class MulticastSendHandler extends SendHandler
{
	/**
	 *  Create a new lease time handling object.
	 */
	public MulticastSendHandler(DiscoveryAgent agent)
	{
		super(agent);
	}
	
	/**
	 *  Method to send messages.
	 */
	public void send(AwarenessInfo info)
	{
		try
		{
			byte[] data = DiscoveryAgent.encodeObject(info, getAgent().getMicroAgent().getClassLoader());
			Object[] ai = getAgent().getAddressInfo();
			send(data, (InetAddress)ai[0], ((Integer)ai[1]).intValue());
//			System.out.println(getComponentIdentifier()+" sent '"+info+"' ("+data.length+" bytes)"+" "+port+" "+address);
		}
		catch(Exception e)
		{
			getAgent().getMicroAgent().getLogger().warning("Could not send awareness message: "+e);
//			e.printStackTrace();
		}
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
			getAgent().getSocket().send(p);
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
	protected MulticastDiscoveryAgent getAgent()
	{
		return (MulticastDiscoveryAgent)agent;
	}
}