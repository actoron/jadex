package jadex.platform.service.message.transport.udpmtp.receiving.handlers;

import jadex.bridge.service.types.message.IMessageService;
import jadex.platform.service.message.transport.udpmtp.PeerInfo;
import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.L_MSG;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.S_MSG;
import jadex.platform.service.message.transport.udpmtp.STunables;
import jadex.platform.service.message.transport.udpmtp.TimedTask;
import jadex.platform.service.message.transport.udpmtp.TimedTaskDispatcher;
import jadex.platform.service.message.transport.udpmtp.receiving.PacketDispatcher;
import jadex.platform.service.message.transport.udpmtp.receiving.RxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.SendingThreadTask;
import jadex.platform.service.message.transport.udpmtp.sending.TxPacket;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public class MsgPacketHandler implements IPacketHandler
{
	/** The message service. */
	protected IMessageService msgservice;
	
	/** Incoming Message pool. */
	protected Map<InetSocketAddress, Map<Integer, RxMessage>> incomingmessages;
	
	/** Information about known peers. */
	protected Map<InetSocketAddress, PeerInfo> peerinfos;
	
	/** The transmission queue. */
	protected PriorityBlockingQueue<TxPacket> packetqueue;
	
	/** The timed task dispatcher. */
	protected TimedTaskDispatcher timedtaskdispatcher;
	
	/**
	 *  Creates the handler.
	 *  
	 *  @param msgservice The message service.
	 *  @param incomingsendermessages Incoming Messages pool.
	 *  @param peerinfos Information about known peers.
	 *  @param packetqueue Queue for scheduled packet transmissions.
	 *  @param timedtaskdispatcher The timed task dispatcher.
	 */
	public MsgPacketHandler(IMessageService msgservice, 
							Map<InetSocketAddress, Map<Integer, RxMessage>> incomingmessages,
							Map<InetSocketAddress, PeerInfo> peerinfos,
							PriorityBlockingQueue<TxPacket> packetqueue,
							TimedTaskDispatcher timedtaskdispatcher)
	{
		this.msgservice = msgservice;
		this.peerinfos = peerinfos;
		this.incomingmessages = incomingmessages;
		this.packetqueue = packetqueue;
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
		return S_MSG.PACKET_TYPE_ID== packettype || L_MSG.PACKET_TYPE_ID == packettype;
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
		// Check if packet contains any payload.
//		System.out.println("Decoding a new packet");
		
		int baseheadersize = 0;
		int msgidoffset = 0;
		int msgsizeoffset = 0;
//		int packetnumberoffset = 0;
//		int totalpacketsoffset = 0;
		int packetnum = 0;
		int totalpackets = 0;
		
		switch (packettype)
		{
			case L_MSG.PACKET_TYPE_ID:
				baseheadersize = L_MSG.HEADER_SIZE;
				msgidoffset = L_MSG.MSG_ID_OFFSET;
				msgsizeoffset = L_MSG.MSG_SIZE_OFFSET;
				packetnum = SCodingUtil.shortFromByteArray(packet, L_MSG.PACKET_NUMBER_OFFSET) & 0xFFFF;
				totalpackets = SCodingUtil.shortFromByteArray(packet, L_MSG.TOTAL_PACKETS_OFFSET) & 0xFFFF;
				
				break;
				
			case S_MSG.PACKET_TYPE_ID:
			default:
				baseheadersize = S_MSG.HEADER_SIZE;
				msgidoffset = S_MSG.MSG_ID_OFFSET;
				msgsizeoffset = S_MSG.MSG_SIZE_OFFSET;
				packetnum = packet[S_MSG.PACKET_NUMBER_OFFSET] & 0xFF;
				totalpackets = packet[S_MSG.TOTAL_PACKETS_OFFSET] & 0xFF; 
		}
		
		if (packet.length > baseheadersize)
		{
			int msgid = SCodingUtil.intFromByteArray(packet, msgidoffset);
			int msgsize = SCodingUtil.intFromByteArray(packet, msgsizeoffset);
			
			
			int headersize = baseheadersize;
			int checksum = 0;
			if (packetnum == 0)
			{
				headersize += SPacketDefs.CHECKSUM_SIZE;
				if (packet.length > headersize)
				{
					checksum = SCodingUtil.intFromByteArray(packet, baseheadersize);
				}
				else
				{
					// No payload.
					PacketDispatcher.packetSanityCheckFailed();
					return;
				}
			}
			
			writeMessagePacket(sender, packettype, msgid, msgsize, packetnum, totalpackets, packet, headersize, checksum);
		}
		else
		{
			// No payload.
			PacketDispatcher.packetSanityCheckFailed();
		}
	}
	
	/**
	 *  Attempts to write a packet into a message and handles errors.
	 *  
	 *  @param packettypeid The packet type ID.
	 *  @param msgid The message ID.
	 *  @param msgsize The message size.
	 *  @param packetnum The packet number.
	 *  @param totalpackets The total number of packets.
	 *  @param packet The packet.
	 *  @param dataoffset The offset where the packet payload starts.
	 *  @param checksum The checksum, only needed for first packet.
	 */
	protected void writeMessagePacket(final InetSocketAddress sender, byte packettypeid, final int msgid, int msgsize, int packetnum, int totalpackets, byte[] packet, int dataoffset, int checksum)
	{
		Map<Integer, RxMessage> incomingsendermessages = incomingmessages.get(sender);
		if (incomingsendermessages == null)
		{
			synchronized (incomingmessages)
			{
				incomingsendermessages = incomingmessages.get(sender);
				if (incomingsendermessages == null)
				{
					incomingsendermessages = Collections.synchronizedMap(new HashMap<Integer, RxMessage>());
					incomingmessages.put(sender, incomingsendermessages);
				}
			}
		}
		
		System.out.println("Writing packet number " + packetnum + " for msg with msgid " + msgid + ", total packets: " + totalpackets);
		RxMessage msg = incomingsendermessages.get(msgid);
		
		if (msg == null)
		{
			synchronized (incomingsendermessages)
			{
				msg = incomingsendermessages.get(msgid);
				if (msg == null)
				{
					msg = new RxMessage(msgsize, totalpackets);
					incomingsendermessages.put(msgid, msg);
					timedtaskdispatcher.scheduleTask(new TimedTask(msgid, System.currentTimeMillis() + STunables.RX_MESSAGE_DECAY)
					{
						public void run()
						{
							Map<Integer, RxMessage> incomingsendermessages = incomingmessages.get(sender);
							if (incomingsendermessages != null)
							{
								incomingsendermessages.remove(msgid);
							}
						}
					});
				}
			}
		}
		
		if (msg.getSize() != msgsize)
		{
			// Some sort of collision, let's try it again...
//			System.out.println("Size mismatch: " + msgsize + " " + msg.getSize());
			resetMessage(packettypeid, sender, msgid, msgsize, totalpackets, msg);
			return;
		}
		
//		System.out.println("Writing " + (packetnum + 1)+ " of " + totalpackets + " packets for msg " + msgid);
//		int old = msg.getMissingPacketsBitfield()[0];
		msg.writePacket(packetnum, packet, dataoffset);
		if (packetnum == 0)
		{
			msg.setReceivedChecksum(checksum);
//			System.out.println("Packet 0 received checksum: "+ checksum);
		}
//		System.out.println("Old/New for " + msgid + " " + old + " " + msg.getMissingPacketsBitfield()[0]);
		
//		System.out.println("Completeness check: " + msgid);
		if (msg.isComplete())
		{
			boolean result = false;
			synchronized(msg)
			{
//				System.out.println("Completeness check seems legit, checking for dispatched: " + msgid);
				if (msg.isComplete() && !msg.isDispatched())
				{
//					System.out.println("Not dispatched, checking checksum: " + msgid);
					result = msg.confirmChecksumAndLock();
					if (!result)
					{
						resetMessage(packettypeid, sender, msgid, msgsize, totalpackets, msg);
					}
				}
			}
			
			if (result)
			{
				final byte[] data = msg.getData();
				timedtaskdispatcher.executeNow(new Runnable()
				{
					public void run()
					{
						msgservice.deliverMessage(data);
					}
				});
				
//				System.out.println("Checksum ok, scheduling ack: " + msgid);
//				System.out.println("ACK_SCHED " +msgid+": "+ System.currentTimeMillis());
				timedtaskdispatcher.executeNow(new TimedTask(sender, Long.MIN_VALUE)
				{
					public void run()
					{
//						System.out.println("ACK_RUN " +msgid+": "+ System.currentTimeMillis());
//						System.out.println("Attempt sending ack for: " + msgid);
						byte[] ackmsgpacket = new byte[5];
						ackmsgpacket[0] = SPacketDefs.MSG_ACK;
						SCodingUtil.intIntoByteArray(ackmsgpacket, 1, msgid);
						
						TxPacket ackpacket = new TxPacket(sender, ackmsgpacket);
						
						PeerInfo info = peerinfos.get(sender);
						Map<Integer, RxMessage> incomingsendermessages = incomingmessages.get(sender);
						if (incomingsendermessages != null)
						{
							if (incomingsendermessages.get(msgid) != null)
							{
//								System.out.println("Sending ack for: " + msgid);
								SendingThreadTask.queuePacket(packetqueue, ackpacket);
								if (info == null)
								{
									executiontime = System.currentTimeMillis() + STunables.ACK_DELAY;
								}
								else
								{
									executiontime = System.currentTimeMillis() + (long)(info.getPing() * STunables.RESEND_DELAY_FACTOR);
								}
								timedtaskdispatcher.scheduleTask(this);
							}
//							else
//							{
//								System.err.println("but msg not found: " + msgid);
//							}
						}
//						else
//						{
//							System.err.println("but sender not found: " + msgid);
//						}
					}
				});
			}
		}
		
		int[] mp = msg.getMissingPacketsBitfield();
		byte[] confpacket = new byte[mp.length * 4 + 5];
		confpacket[0] = SPacketDefs.MSG_CONFIRM;
		SCodingUtil.intIntoByteArray(confpacket, 1, msgid);
		for (int i = 0; i < mp.length; ++i)
		{
			SCodingUtil.intIntoByteArray(confpacket, 5 + i * 4, mp[i]);
		}
		SendingThreadTask.queuePacket(packetqueue, new TxPacket(sender, confpacket));
		
//		PeerInfo info = peerinfos.get(sender);
//		if (info != null)
//		{
////			if (info.getReceivedBytes().addAndGet(packet.length) > STunables.CONFIRMATION_THRESHOLD)
//			{
//				info.getReceivedBytes().set(0);
//				synchronized(incomingsendermessages)
//				{
//					System.out.println("Sending confirmations at: " + info.getReceivedBytes().get());
//					for (Map.Entry<Integer, RxMessage> curmsg : incomingsendermessages.entrySet())
//					{
//						if (!curmsg.getValue().isComplete())// && curmsg.getValue().getUnconfirmedWrites() > 0)
//						{
//							int[] mp = curmsg.getValue().getMissingPacketsBitfield();
//							byte[] confpacket = new byte[mp.length * 4 + 5];
//							confpacket[0] = SPacketDefs.MSG_CONFIRM;
//							SCodingUtil.intIntoByteArray(confpacket, 1, curmsg.getKey());
//							for (int i = 0; i < mp.length; ++i)
//							{
//								SCodingUtil.intIntoByteArray(confpacket, 5 + i * 4, mp[i]);
//							}
//							
//							
//							SendingThreadTask.queuePacket(packetqueue, new TxPacket(sender, confpacket));
//							curmsg.getValue().setUnconfirmedWrites(0);
//						}
//					}
////					System.out.println("Done sending confirmations at: " + info.getReceivedBytes().get() + " " + count);
//				}
//			}
//		}
	}
	
	/**
	 *  Resets a message in case of a major problem (checksum failed, size mismatch)
	 *  
	 *  @param packettypeid Packet type of the packet that triggered the problem.
	 *  @param sender The sender of the message.
	 *  @param msgid The message ID.
	 *  @param msgsize The message size as indicated in the packet.
	 *  @param totalpackets The total packets as indicated in the packet.
	 *  @param msg The message.
	 */
	protected void resetMessage(byte packettypeid, InetSocketAddress sender, int msgid, int msgsize, int totalpackets, RxMessage msg)
	{
		synchronized (msg)
		{
			msg.reset(msgsize, totalpackets);
			
			TxPacket msggarbage = TxPacket.createGenericMsgIdPacket(SPacketDefs.MSG_GARBAGE, sender, msgid);
			
			SendingThreadTask.queuePacket(packetqueue, msggarbage);
		}
	}
}
