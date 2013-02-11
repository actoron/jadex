package jadex.platform.service.message.transport.udpmtp.receiving.handlers;

import jadex.platform.service.message.transport.udpmtp.PeerInfo;
import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.STunables;
import jadex.platform.service.message.transport.udpmtp.receiving.PacketDispatcher;
import jadex.platform.service.message.transport.udpmtp.sending.ITxTask;
import jadex.platform.service.message.transport.udpmtp.sending.TxPacket;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *  Standard probe handler.
 *
 */
public class ProbeHandler implements IPacketHandler
{
	/** Information about known peers. */
	protected Map<InetSocketAddress, PeerInfo> peerinfos;
	
	/** Queue for scheduled transmissions */
	protected PriorityBlockingQueue<ITxTask> txqueue;
	
	/**
	 *  Creates the handler.
	 *  
	 *  @param peerinfos Information about known peers.
	 *  @param txqueue Queue for scheduled transmissions.
	 */
	public ProbeHandler(Map<InetSocketAddress, PeerInfo> peerinfos, PriorityBlockingQueue<ITxTask> txqueue)
	{
		this.peerinfos = peerinfos;
		this.txqueue = txqueue;
	}
	
	/**
	 *  Returns if the handler is applicable for this packet type.
	 *  
	 *  @param packettype The packet type.
	 *  @return True, if the handler is applicable.
	 */
	public boolean isApplicable(byte packettype)
	{
		return SPacketDefs.PROBE == packettype ||
			   SPacketDefs.PROBE_ACK == packettype ||
			   SPacketDefs.PROBE_FIN == packettype;
	}
	
	/**
	 *  Returns if the handler is done and should be removed.
	 *  
	 *  @return True, if the handler is done.
	 */
	public boolean isDone()
	{
		return false;
	}
	
	/**
	 *  Handles the packet
	 *  
	 *  @param sender The sender of the packet.
	 *  @param packettype The packet type.
	 *  @param packet The raw packet.
	 */
	public void handlePacket(InetSocketAddress sender, byte packettype, byte[] packet)
	{
		switch(packettype)
		{
			case SPacketDefs.PROBE:
				if (packet.length == 9)
				{
					byte[] reply = new byte[17];
					reply[0] = SPacketDefs.PROBE_ACK;
					System.arraycopy(packet, 1, reply, 1, 8);
					SCodingUtil.longIntoByteArray(reply, 9, System.currentTimeMillis());
					txqueue.put(new TxPacket(sender, STunables.PROBE_PACKETS_DEFAULT_PRIORITY, reply));
				}
				else
				{
					PacketDispatcher.packetSanityCheckFailed();
				}
				break;
				
			case SPacketDefs.PROBE_ACK:
				if (packet.length == 17)
				{
					PeerInfo info = peerinfos.get(sender);
					if (info != null)
					{
						info.setLastProbe(System.currentTimeMillis());
						info.setState(PeerInfo.STATE_OK);
						Runnable waiter = null;
						while ((waiter = info.getStateWaiters().poll()) != null)
						{
							waiter.run();
						}
					}
				}
				else
				{
					PacketDispatcher.packetSanityCheckFailed();
				}
				break;
				
			case SPacketDefs.PROBE_FIN:
				//TODO: Update Peer Info with statistics
				break;
		}
	}
}
