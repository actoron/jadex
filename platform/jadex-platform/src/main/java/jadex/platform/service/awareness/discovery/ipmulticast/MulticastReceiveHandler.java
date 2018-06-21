package jadex.platform.service.awareness.discovery.ipmulticast;

import java.net.DatagramPacket;

import jadex.platform.service.awareness.discovery.DiscoveryAgent;
import jadex.platform.service.awareness.discovery.ReceiveHandler;

/**
 * 
 */
public class MulticastReceiveHandler extends ReceiveHandler
{
	/** The receive buffer. */
	protected byte[] buffer;
	
	/**
	 *  Create a new receive handler.
	 */
	public MulticastReceiveHandler(DiscoveryAgent agent)
	{
		super(agent);
	}
	
	/**
	 *  Receive a packet.
	 */
	public Object[] receive()	throws Exception
	{
		Object[] ret = null;
//		try
//		{

			if(buffer==null)
			{
				// todo: max ip datagram length (is there a better way to determine length?)
				buffer = new byte[8192];
			}

			final DatagramPacket pack = new DatagramPacket(buffer, buffer.length);
			getAgent().getSocket().receive(pack);
			byte[] data = new byte[pack.getLength()];
			System.arraycopy(buffer, 0, data, 0, pack.getLength());
			ret = new Object[]{pack.getAddress(), Integer.valueOf(pack.getPort()), data};
//		}
//		catch(IOException e)
//		{
////			getAgent().getMicroAgent().getLogger().warning("Message receival error: "+e);
//		}
		
		return ret;
	}
	
	/**
	 *  Get the agent.
	 */
	public MulticastDiscoveryAgent getAgent()
	{
		return (MulticastDiscoveryAgent)agent;
	}
}

