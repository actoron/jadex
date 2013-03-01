package jadex.platform.service.message.transport.udpmtp.receiving.handlers;

import jadex.platform.service.message.transport.udpmtp.PeerInfo;
import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.STunables;
import jadex.platform.service.message.transport.udpmtp.TimedTaskDispatcher;
import jadex.platform.service.message.transport.udpmtp.receiving.PacketDispatcher;
import jadex.platform.service.message.transport.udpmtp.receiving.RxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.FlowControl;
import jadex.platform.service.message.transport.udpmtp.sending.SendingThreadTask;
import jadex.platform.service.message.transport.udpmtp.sending.TxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.TxPacket;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  Handler for message acknowledgment and fin packets.
 *
 */
public class MsgAckFinHandler implements IPacketHandler
{
	/** Incoming Message pool. */
	protected Map<InetSocketAddress, Map<Integer, RxMessage>> incomingmessages;
	
	/** Messages in-flight. */
	protected Map<Integer, TxMessage> inflightmessages;
	
	/** The transmission queue. */
	protected PriorityBlockingQueue<TxPacket> packetqueue;
	
	/** Currently used message IDs. */
	protected Set<Integer> usedids;
	
	/** The timed task dispatcher. */
	protected TimedTaskDispatcher timedtaskdispatcher;
	
	/** Information about known peers. */
	protected Map<InetSocketAddress, PeerInfo> peerinfos;
	
	/** The remaining send quota. */
	protected AtomicInteger sendquota;
	
	/** The flow control. */
	protected FlowControl flowcontrol;
	
	/**
	 *  Creates the handler.
	 *  
	 *  @param incomingsendermessages Incoming Messages pool.
	 *  @param inflightmessages The messages in flight.
	 *  @param sendquota The remaining send quota.
	 *  @param flowcontrol The flow control.
	 *  @param txqueue Queue for scheduled transmissions.
	 *  @param peerinfos Information about known peers.
	 *  @param usedids Currently used message IDs.
	 */
	public MsgAckFinHandler(Map<InetSocketAddress, Map<Integer, RxMessage>> incomingmessages,
							Map<Integer, TxMessage> inflightmessages,
							AtomicInteger sendquota,
							FlowControl flowcontrol,
							PriorityBlockingQueue<TxPacket> packetqueue,
							Map<InetSocketAddress, PeerInfo> peerinfos,
							Set<Integer> usedids,
							TimedTaskDispatcher timedtaskdispatcher)
	{
		this.incomingmessages = incomingmessages;
		this.inflightmessages = inflightmessages;
		this.sendquota = sendquota;
		this.flowcontrol = flowcontrol;
		this.packetqueue = packetqueue;
		this.usedids = usedids;
		this.peerinfos = peerinfos;
		this.timedtaskdispatcher = timedtaskdispatcher;
	}
	
	/**
	 *  Returns if the handler is applicable for this packet type.
	 *  
	 *  @param packettype The packet type.
	 *  @return True, if the handler is applicable.
	 */
	public boolean isApplicable(byte packettype)
	{
		return SPacketDefs.MSG_ACK == packettype ||
			   SPacketDefs.MSG_FIN == packettype ||
			   SPacketDefs.MSG_GARBAGE == packettype;
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
	
	static double ackstat = 0.0;
	static double rsstat = 0.0;
	static int maxrs = 0;
	static long count = 0;
	
	/**
	 *  Handles the packet
	 *  
	 *  @param sender The sender of the packet.
	 *  @param packettype The packet type.
	 *  @param packet The raw packet.
	 */
	public void handlePacket(InetSocketAddress sender, byte packettype, byte[] packet)
	{
		if (packet.length == 5)
		{
			if (SPacketDefs.MSG_ACK == packettype)
			{
				final int msgid = SCodingUtil.intFromByteArray(packet, 1);
				TxMessage msg = inflightmessages.remove(msgid);
//				if (msg != null)
//				{
//					SendingThreadTask.schedmsg.remove(msg.getMsgId());
//					synchronized (SendingThreadTask.schedmsg)
//					{
//						System.out.println("msgaccounting-: " + Arrays.toString(SendingThreadTask.schedmsg.toArray()));
//					}
//				}
//				System.out.println("Ack rcv: " + msgid + " " + msg);
				timedtaskdispatcher.cancel(msg);
				usedids.remove(msgid);
				SendingThreadTask.queuePacket(packetqueue, TxPacket.createGenericMsgIdPacket(SPacketDefs.MSG_FIN, sender, msgid));
				
				if (msg != null)
				{
					final TxMessage dmsg = msg;
					timedtaskdispatcher.executeNow(new Runnable()
					{
						public void run()
						{
							dmsg.confirmTransmission();
						}
					});
					
					long roundtrip = System.currentTimeMillis() - msg.ls;
					//info.newPing(roundtrip);
					synchronized (MsgAckFinHandler.class)
					{
						++count;
						ackstat = ackstat * 0.99 + roundtrip * 0.01;
						rsstat = rsstat * 0.99 + msg.getResendCounter() * 0.01;
						maxrs = Math.max(maxrs, msg.getResendCounter());
						if (count % 1000 == 0)
							System.out.println("Stats: " + ackstat + " " + String.valueOf(rsstat) + " " + maxrs + " rt " + roundtrip +" schedtosend " + (msg.getPackets()[msg.getPackets().length-1].sentts - msg.ls));
					}
					int quota = 0;
					synchronized (msg)
					{
						for (int i = 0; i < msg.getPackets().length; ++i)
						{
							if (!msg.getPackets()[i].isConfirmed())
							{
								msg.getPackets()[i].setConfirmed(true);
								quota += msg.getPackets()[i].getRawPacket().length;
//								sendquota.addAndGet(msg.getPackets()[i].getRawPacket().length);
//								System.out.println("IncA: " + msgid + " " + sendquota.get());
							}
						}
					}
					int remains = sendquota.addAndGet(quota);
					if (remains >= STunables.CONFIRMATION_THRESHOLD)
					{
						synchronized (packetqueue)
						{
							packetqueue.notifyAll();
						}
					}
					flowcontrol.ack();
				}
			}
			else if (SPacketDefs.MSG_FIN == packettype)
			{
				int msgid = SCodingUtil.intFromByteArray(packet, 1);
//				System.out.println("Fin: " + msgid);
				Map<Integer, RxMessage> incomingsendermessages = incomingmessages.get(sender);
				if (incomingsendermessages != null)
				{
					RxMessage msg = incomingsendermessages.remove(msgid);
					timedtaskdispatcher.cancel(msgid);
					PeerInfo info = peerinfos.get(sender);
					if (info != null && msg != null)
					{
						info.getReceivedBytes().addAndGet(-msg.getRawSize());
					}
				}
			}
			else if (SPacketDefs.MSG_GARBAGE == packettype)
			{
				int msgid = SCodingUtil.intFromByteArray(packet, 1);
				TxMessage msg = inflightmessages.get(msgid);
				msg.resetConfirmedPackets();
				msg.setResendCounter(msg.getResendCounter() + 1);
			}
		}
		else
		{
			PacketDispatcher.packetSanityCheckFailed();
		}
	}
}
