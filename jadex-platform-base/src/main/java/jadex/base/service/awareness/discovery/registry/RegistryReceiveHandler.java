package jadex.base.service.awareness.discovery.registry;

import jadex.base.service.awareness.AwarenessInfo;
import jadex.base.service.awareness.discovery.DiscoveryAgent;
import jadex.base.service.awareness.discovery.DiscoveryEntry;
import jadex.base.service.awareness.discovery.ReceiveHandler;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * 
 */
public class RegistryReceiveHandler extends ReceiveHandler
{
	/** The receive buffer. */
	protected byte[] buffer;
	
	/**
	 *  Create a new lease time handling object.
	 */
	public RegistryReceiveHandler(DiscoveryAgent agent)
	{
		super(agent);
	}
	
	/**
	 *  Receive a packet.
	 */
	public Object[] receive()
	{
		Object[] ret = null;
		try
		{
			if(buffer==null)
			{
				// todo: max ip datagram length (is there a better way to determine length?)
				buffer = new byte[8192];
			}

			final DatagramPacket pack = new DatagramPacket(buffer, buffer.length);
			getAgent().getSocket().receive(pack);
			ret = new Object[]{pack.getAddress(), new Integer(pack.getPort()), pack.getData()};
		}
		catch(Exception e)
		{
			getAgent().getMicroAgent().getLogger().warning("Message receival error: "+e);
		}
		
		return ret;
	}
	
	/**
	 *  Handle a received packet.
	 */
	public void handleReceivedPacket(InetAddress address, int port, byte[] data, AwarenessInfo info)
	{
		super.handleReceivedPacket(address, port, data, info);
		InetSocketAddress sa = new InetSocketAddress(address, port);
		getAgent().getKnowns().addOrUpdateEntry(new DiscoveryEntry(info, getAgent().getClockTime(), sa));
//		System.out.println("received awa info: "+getComponentIdentifier().getLocalName()+" "+info.getSender());
	}
	
	/**
	 *  Get the agent.
	 */
	public RegistryDiscoveryAgent getAgent()
	{
		return (RegistryDiscoveryAgent)agent;
	}
}
