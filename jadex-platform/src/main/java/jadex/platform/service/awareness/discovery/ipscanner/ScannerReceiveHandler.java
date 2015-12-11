package jadex.platform.service.awareness.discovery.ipscanner;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import jadex.platform.service.awareness.discovery.MasterSlaveReceiveHandler;

/**
 *  Receiver handler for scanner.
 */
public class ScannerReceiveHandler extends MasterSlaveReceiveHandler
{
	/** The receive buffer. */
	protected ByteBuffer buffer;
	
	/**
	 *  Create a new receive handler.
	 */
	public ScannerReceiveHandler(ScannerDiscoveryAgent agent)
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
				buffer = ByteBuffer.allocate(8192);
			}
			else
			{
				buffer.clear();
			}
			InetSocketAddress address = (InetSocketAddress)getAgent().getChannel().receive(buffer);
			if(address!=null)
			{
				buffer.flip();
				byte[] data = new byte[buffer.remaining()];
				buffer.get(data);
				ret = new Object[]{address.getAddress(), address.getPort(), data};
			}
//		}
//		catch(Exception e)
//		{
////			getAgent().getMicroAgent().getLogger().warning("Receive message failed: "+e);
////			e.printStackTrace();
////			System.out.println("ex: "+address);
//		}
		
		return ret;
	}
	
	/**
	 *  Get the agent.
	 */
	public ScannerDiscoveryAgent getAgent()
	{
		return (ScannerDiscoveryAgent)agent;
	}
}
