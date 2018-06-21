package jadex.platform.service.awareness.discovery.ipbroadcast;

import java.net.DatagramPacket;

import jadex.platform.service.awareness.discovery.MasterSlaveReceiveHandler;

/**
 *  Receiver handler for broadcast discovery.
 */
public class BroadcastReceiveHandler extends MasterSlaveReceiveHandler
{
	/** The receive buffer. */
	protected byte[] buffer;
	
	/**
	 *  Create a new receive handler.
	 */
	public BroadcastReceiveHandler(BroadcastDiscoveryAgent agent)
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
//			System.out.println("received packet: "+pack.getAddress());
//		}
//		catch(Exception e)
//		{
////			System.out.println("Message receival error: "+e);
//			getAgent().getMicroAgent().getLogger().warning("Message receival error: "+e);
//			e.printStackTrace();
//		}
		
		return ret;
	}
	
	/**
	 *  Get the agent.
	 */
	public BroadcastDiscoveryAgent getAgent()
	{
		return (BroadcastDiscoveryAgent)agent;
	}
}
