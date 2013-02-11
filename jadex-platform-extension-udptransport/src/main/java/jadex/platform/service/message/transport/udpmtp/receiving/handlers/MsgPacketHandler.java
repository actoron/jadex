package jadex.platform.service.message.transport.udpmtp.receiving.handlers;

import jadex.bridge.service.types.message.IMessageService;
import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.L_MSG;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.S_MSG;
import jadex.platform.service.message.transport.udpmtp.receiving.PacketDispatcher;
import jadex.platform.service.message.transport.udpmtp.receiving.RxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.ITxTask;
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
	
	/** Queue for scheduled transmissions */
	protected PriorityBlockingQueue<ITxTask> txqueue;
	
	/**
	 *  Creates the handler.
	 *  
	 *  @param msgservice The message service.
	 *  @param incomingsendermessages Incoming Messages pool.
	 *  @param txqueue Queue for scheduled transmissions.
	 */
	public MsgPacketHandler(IMessageService msgservice, 
							Map<InetSocketAddress, Map<Integer, RxMessage>> incomingmessages,
							PriorityBlockingQueue<ITxTask> txqueue)
	{
		this.msgservice = msgservice;
		this.txqueue = txqueue;
		this.incomingmessages = incomingmessages;
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
		int packetnumberoffset = 0;
		int totalpacketsoffset = 0;
		
		switch (packettype)
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
	protected void writeMessagePacket(InetSocketAddress sender, byte packettypeid, int msgid, int msgsize, int packetnum, int totalpackets, byte[] packet, int dataoffset, int checksum)
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
				txqueue.put(ackpacket);
				
				msgservice.deliverMessage(msg.getData());
			}
		}
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
			
			TxPacket resendreq = null;
			if (packettypeid == S_MSG.PACKET_TYPE_ID)
			{
				resendreq = TxPacket.createSmallResendRequest(sender, msgid, new int[0]);
			}
			else
			{
				resendreq = TxPacket.createLargeResendRequest(sender, msgid, new int[0]);
			}
			
			txqueue.put(resendreq);
		}
	}
}
