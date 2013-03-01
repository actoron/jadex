package jadex.platform.service.message.transport.udpmtp.receiving.handlers;

import jadex.platform.service.message.transport.udpmtp.PeerInfo;
import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.STunables;
import jadex.platform.service.message.transport.udpmtp.receiving.PacketDispatcher;
import jadex.platform.service.message.transport.udpmtp.receiving.RxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.TxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.TxPacket;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  Handler dealing with re-sends.
 *
 */
public class MsgConfirmationHandler implements IPacketHandler
{
	/** Incoming Message pool. */
	protected Map<InetSocketAddress, Map<Integer, RxMessage>> incomingmessages;
	
	/** Messages in-flight. */
	protected Map<Integer, TxMessage> inflightmessages;
	
	/** Information about known peers. */
	protected Map<InetSocketAddress, PeerInfo> peerinfos;
	
	/** The remaining send quota. */
	protected AtomicInteger sendquota;
	
	/** The transmission queue. */
	protected PriorityBlockingQueue<TxPacket> packetqueue;
	
	/**
	 *  Creates the handler.
	 *  
	 *  @param incomingsendermessages Incoming Messages pool.
	 *  @param inflightmessages The messages in flight.
	 *  @param txqueue Queue for scheduled transmissions.
	 */
	public MsgConfirmationHandler(Map<InetSocketAddress, Map<Integer, RxMessage>> incomingmessages,
							Map<Integer, TxMessage> inflightmessages,
							AtomicInteger sendquota,
							PriorityBlockingQueue<TxPacket> packetqueue,
							Map<InetSocketAddress, PeerInfo> peerinfos)
	{
		this.incomingmessages = incomingmessages;
		this.inflightmessages = inflightmessages;
		this.sendquota = sendquota;
		this.packetqueue = packetqueue;
		this.peerinfos = peerinfos;
	}
	
	/**
	 *  Returns if the handler is applicable for this packet type.
	 *  
	 *  @param packettype The packet type.
	 *  @return True, if the handler is applicable.
	 */
	public boolean isApplicable(byte packettype)
	{
		return SPacketDefs.MSG_CONFIRM == packettype ||
			   SPacketDefs.MSG_CONFIRM_ERROR == packettype;
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
		if (packet.length >= 5)
		{
			if (SPacketDefs.MSG_CONFIRM == packettype)
			{
				if (packet.length > 5 && ((packet.length - 5) & 3) == 0)
				{
					int msgid = SCodingUtil.intFromByteArray(packet, 1);
					TxMessage msg = inflightmessages.get(msgid);
					if (msg != null)
					{
					
						int[] pflags = new int[(packet.length - 5) / 4];
						for (int i = 0; i < pflags.length; ++i)
						{
							pflags[i] = SCodingUtil.intFromByteArray(packet, 5 + (i * 4));
						}
						
						int quota = 0;
						synchronized (msg)
						{
//							for (int packetnum = 0; packetnum < msg.getPackets().length; ++packetnum)
//							{
//								int flag = 1 << (packetnum % 32);
//								
//								if ((pflags[packetnum / 32] & flag) != 0 && !msg.getPackets()[packetnum].isConfirmed())
//								{
//									msg.getPackets()[packetnum].setConfirmed(true);
//									quota += msg.getPackets()[packetnum].getRawPacket().length;
//								}
//							}
							
							
							for (int i = 0; i < pflags.length; ++i)
							{
								int max = Math.min(32, 32 - (((i + 1) * 32) - msg.getPackets().length));
								int pnum = i * 32;
								for (int j = 0; j < max; ++j)
								{
									if ((pflags[i] & (1 << j)) != 0 && !msg.getPackets()[pnum].isConfirmed())
									{
										msg.getPackets()[pnum].setConfirmed(true);
										System.out.println("Confirmed: " + pnum);
										quota += msg.getPackets()[pnum].getRawPacket().length;
									}
									++pnum;
								}
							}
						}
						int remains = sendquota.addAndGet(quota);
//						System.out.println("IncC " + msg.getMsgId() + ":" + quota);
						if (remains >= STunables.CONFIRMATION_THRESHOLD)
						{
							synchronized (packetqueue)
							{
								packetqueue.notifyAll();
							}
						}
					}
				}
				else
				{
					PacketDispatcher.packetSanityCheckFailed();
				}
			}
			else if (packettype == SPacketDefs.MSG_CONFIRM_ERROR)
			{
				Map<Integer, RxMessage> incomingsendermessages = incomingmessages.get(sender);
				if (incomingsendermessages != null)
				{
					int msgid = SCodingUtil.intFromByteArray(packet, 1);
					incomingsendermessages.remove(msgid);
				}
			}
		}
		else
		{
			PacketDispatcher.packetSanityCheckFailed();
		}
	}
}
