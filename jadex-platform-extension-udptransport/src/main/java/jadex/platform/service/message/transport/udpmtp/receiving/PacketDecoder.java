package jadex.platform.service.message.transport.udpmtp.receiving;

import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.Tuple3;
import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.L_MSG;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.S_MSG;
import jadex.platform.service.message.transport.udpmtp.sending.ITxTask;
import jadex.platform.service.message.transport.udpmtp.sending.TxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.TxPacket;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

public class PacketDecoder implements Runnable
{
	/** The packet sender. */
	protected InetSocketAddress sender;
	
	/** The incoming packet. */
	protected byte[] packet;
	
	/** Messages in-flight. */
	protected Map<Integer, TxMessage> inflightmessages;
	
	/** Currently used message IDs */
	protected Set<Integer> usedids;
	
	/** Incoming Message pool. */
	protected Map<Integer, RxMessage> incomingsendermessages;
	
	/** Queue for scheduled transmissions */
	protected PriorityBlockingQueue<ITxTask> txqueue;
	
	/** The thread pool. */
	//protected IDaemonThreadPoolService threadpool;
	
	/** The message service . */
	protected IMessageService msgservice;
	
	/** Delated last packet */
	protected Tuple3<Integer, byte[], Integer> delayedlastpacket;
	
	public PacketDecoder(InetSocketAddress sender, byte[] packet, Map<Integer, TxMessage> inflightmessages, Set<Integer> usedids, Map<Integer, RxMessage> incomingsendermessages, PriorityBlockingQueue<ITxTask> txqueue, IMessageService msgservice)
	{
		this.sender = sender;
		this.packet = packet;
		this.inflightmessages = inflightmessages;
		this.usedids = usedids;
		this.incomingsendermessages = incomingsendermessages;
		this.txqueue = txqueue;
		this.msgservice = msgservice;
	}
	
	/**
	 *  Runs the message decoding.
	 */
	public void run()
	{
//		System.out.println("Executing packet dispatch task");
		// Check if packet contains anything at all.
		if (packet.length > 0)
		{
			byte packettypeid = packet[0];
//			System.out.println("packetid" + packettypeid);
			switch(packettypeid)
			{
				case S_MSG.PACKET_TYPE_ID:
				case L_MSG.PACKET_TYPE_ID:
					handleMessagePacket(packettypeid, packet);
					break;
					
				case S_MSG.RESEND_REQ_ID:
				case L_MSG.RESEND_REQ_ID:
					handleResendRequest(packettypeid, packet);
					
				case SPacketDefs.MSG_ACK:
					if (packet.length == 5)
					{
						int msgid = SCodingUtil.intFromByteArray(packet, 1);
						inflightmessages.remove(msgid);
						//TODO: Delay if re-sent?
						usedids.remove(msgid);
						txqueue.offer(TxPacket.createMsgFin(sender, msgid));
					}
					else
					{
						packetSanityCheckFailed();
					}
					
				case SPacketDefs.MSG_FIN:
					if (packet.length == 5)
					{
						short msgid = SCodingUtil.shortFromByteArray(packet, 1);
						incomingsendermessages.remove(msgid);
					}
					else
					{
						packetSanityCheckFailed();
					}
					break;
				
				default:
					unknownPacketTypeError();
			}
		}
		else
		{
			// Empty packet.
			packetSanityCheckFailed();
		}
	}
	
	/**
	 *  Method handling re-send requests.
	 */
	protected void handleResendRequest(byte packettypeid, byte[] packet)
	{
		if (packet.length >= 5)
		{
			int msgid = SCodingUtil.intFromByteArray(packet, 1);
			
			TxMessage msg = inflightmessages.get(msgid);
			if (msg != null)
			{
				if (packet.length == 5)
				{
					// Full resend
					txqueue.offer(msg);
				}
				else
				{
					if (packettypeid == L_MSG.RESEND_REQ_ID)
					{
						if ((packet.length - 5) % 2 == 0)
						{
							short[] resendpackets = new short[(packet.length - 5) / 2];
							for (int i = 5; i < packet.length; i = i + 2)
							{
								resendpackets[i] = SCodingUtil.shortFromByteArray(packet, i);
							}
						}
						else
						{
							packetSanityCheckFailed();
						}
					}
					else
					{
						short[] resendpackets = new short[packet.length - 5];
						for (int i = 5; i < packet.length; ++i)
						{
							resendpackets[i] = packet[i];
						}
					}
				}
			}
			else
			{
				//TODO Re-send error.
			}
		}
		else
		{
			packetSanityCheckFailed();
		}
	}
	
	/**
	 *  Method handling message packets.
	 *  
	 *  @param packet The packet.
	 */
	protected void handleMessagePacket(byte packettypeid, byte[] packet)
	{
		// Check if packet contains any payload.
//		System.out.println("Decoding a new packet");
		
		int baseheadersize = 0;
		int msgidoffset = 0;
		int msgsizeoffset = 0;
		int packetnumberoffset = 0;
		int totalpacketsoffset = 0;
		
		switch (packettypeid)
		{
			case L_MSG.PACKET_TYPE_ID:
				baseheadersize = L_MSG.HEADER_SIZE;
				msgidoffset = L_MSG.MSG_ID_OFFSET;
				msgsizeoffset = L_MSG.MSG_SIZE_OFFSET;
				packetnumberoffset = L_MSG.PACKET_NUMBER_OFFSET;
				totalpacketsoffset = L_MSG.TOTAL_PACKETS_OFFSET;
				break;
				
			case S_MSG.PACKET_TYPE_ID:
			default:
				baseheadersize = S_MSG.HEADER_SIZE;
				msgidoffset = S_MSG.MSG_ID_OFFSET;
				msgsizeoffset = L_MSG.MSG_SIZE_OFFSET;
				packetnumberoffset = S_MSG.PACKET_NUMBER_OFFSET;
				totalpacketsoffset = S_MSG.TOTAL_PACKETS_OFFSET;
		}
		
		if (packet.length > baseheadersize)
		{
			int msgid = SCodingUtil.intFromByteArray(packet, msgidoffset);
			int msgsize = SCodingUtil.intFromByteArray(packet, msgsizeoffset);
			int packetnum = packet[packetnumberoffset];
			int totalpackets = packet[totalpacketsoffset];
			
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
					packetSanityCheckFailed();
					return;
				}
			}
			
			writeMessagePacket(packettypeid, msgid, msgsize, packetnum, totalpackets, packet, headersize, checksum);
		}
		else
		{
			// No payload.
			packetSanityCheckFailed();
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
	protected void writeMessagePacket(byte packettypeid, int msgid, int msgsize, int packetnum, int totalpackets, byte[] packet, int dataoffset, int checksum)
	{
//		System.out.println("Writing packet with msgid: " + msgid);
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
		
//		System.out.println("Entering " + (packetnum + 1)+ " of " + totalpackets + " packets.");
		msg.writePacket(packetnum, packet, dataoffset);
		if (packetnum == 0)
		{
			msg.setReceivedChecksum(checksum);
//			System.out.println("Packet 0 received checksum: "+ checksum);
		}
		
		if (msg.isComplete())
		{
			boolean result = false;
			synchronized(msg)
			{
				result = msg.confirmChecksumAndLock();
				if (!result)
				{
					resetMessage(packettypeid, sender, msgid, msgsize, totalpackets, msg);
				}
			}
			
			if (result)
			{
				byte[] ackmsgpacket = new byte[5];
				ackmsgpacket[0] = SPacketDefs.MSG_ACK;
				SCodingUtil.intIntoByteArray(ackmsgpacket, 1, msgid);
				
				TxPacket ackpacket = new TxPacket(sender, ackmsgpacket);
				txqueue.offer(ackpacket);
				
				msgservice.deliverMessage(msg.getData());
			}
		}
	}
	
	protected void resetMessage(byte packettypeid, InetSocketAddress sender, int msgid, int msgsize, int totalpackets, RxMessage msg)
	{
		synchronized (msg)
		{
			msg.reset(msgsize, totalpackets);
			
			TxPacket resendreq = null;
			if (packettypeid == S_MSG.PACKET_TYPE_ID)
			{
				resendreq = TxPacket.createSmallResendRequest(sender, msgid, new int[0]);
			}
			else
			{
				resendreq = TxPacket.createLargeResendRequest(sender, msgid, new int[0]);
			}
			
			txqueue.offer(resendreq);
		}
	}
	
	protected void packetSanityCheckFailed()
	{
		System.err.println("Packet sanity failed!");
	}
	
	protected void unknownPacketTypeError()
	{
		System.err.println("Unknown Packet!");
	}
	
	protected void checksumError()
	{
		System.err.println("Checksum failed!");
	}
}
