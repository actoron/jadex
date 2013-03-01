package jadex.platform.service.message.transport.udpmtp.receiving.handlers;

import jadex.platform.service.message.transport.udpmtp.PeerInfo;
import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.STunables;
import jadex.platform.service.message.transport.udpmtp.TimedTaskDispatcher;
import jadex.platform.service.message.transport.udpmtp.receiving.PacketDispatcher;
import jadex.platform.service.message.transport.udpmtp.sending.SendingThreadTask;
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
	
	/** The transmission queue. */
	protected PriorityBlockingQueue<TxPacket> packetqueue;
	
	/** The timed task dispatcher. */
	protected TimedTaskDispatcher timedtaskdispatcher;
	
	/**
	 *  Creates the handler.
	 *  
	 *  @param peerinfos Information about known peers.
	 *  @param packetqueue Queue for scheduled packets.
	 *  @param timedtaskdispatcher The timed task dispatcher.
	 */
	public ProbeHandler(Map<InetSocketAddress, PeerInfo> peerinfos, PriorityBlockingQueue<TxPacket> packetqueue, TimedTaskDispatcher timedtaskdispatcher)
	{
		this.peerinfos = peerinfos;
		this.packetqueue = packetqueue;
	}
	
	/**
	 *  Returns if the handler is applicable for this packet type.
	 *  
	 *  @param packettype The packet type.
	 *  @return True, if the handler is applicable.
	 */
	public boolean isApplicable(byte packettype)
	{
		boolean ret = SPacketDefs.PROBE == packettype ||
			   SPacketDefs.PROBE_ACK == packettype ||
			   SPacketDefs.PROBE_FIN == packettype;
		
		return ret;
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
					SendingThreadTask.queuePacket(packetqueue, new TxPacket(sender, STunables.PROBE_PACKETS_DEFAULT_PRIORITY, reply));
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
						long current = System.currentTimeMillis();
						info.setLastProbe(current);
						info.setState(PeerInfo.STATE_OK);
						
						long calcping = SCodingUtil.longFromByteArray(packet, 1);
						
						calcping = current - calcping;
						info.newPing(calcping);
						
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
//				PeerInfo info = peerinfos.get(sender);
//				if (info == null)
//				{
//					synchronized (peerinfos)
//					{
//						info = peerinfos.get(sender);
//						if (info == null)
//						{
//							info = new PeerInfo(sender, timedtaskdispatcher);
//							System.out.println("Creating peer:" + info);
//							peerinfos.put(sender, info);
//							
//							TimedTask peerprober = new PeerProber(peerinfos, info, timedtaskdispatcher, packetqueue);
//							timedtaskdispatcher.scheduleTask(peerprober);
//						}
//					}
//				}
				break;
		}
	}
}
