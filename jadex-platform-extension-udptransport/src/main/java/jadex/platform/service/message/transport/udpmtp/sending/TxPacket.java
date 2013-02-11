package jadex.platform.service.message.transport.udpmtp.sending;

import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.L_MSG;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.S_MSG;
import jadex.platform.service.message.transport.udpmtp.STunables;

import java.net.InetSocketAddress;

/**
 *  A simple packet scheduled for transmission.
 *
 */
public class TxPacket implements ITxTask
{
	/** Callback on send failures. */
	protected Runnable sendfailuretask;
	
	/** The resolved receiver. */
	protected InetSocketAddress resolvedreceiver;
	
	/** The packets being transmitted, in this case one. */
	protected byte[][] packets;
	
	/** The priority of the packet. */
	protected int priority;
	
	/**
	 *  Creates a new packet.
	 *  
	 *  @param resolvedreceiver The receiver.
	 *  @param packet The packet.
	 */
	public TxPacket(InetSocketAddress resolvedreceiver, byte[] packet)
	{
		this(resolvedreceiver, packet, STunables.CONTROL_PACKETS_DEFAULT_PRIORITY, null);
	}
	
	/**
	 *  Creates a new packet.
	 *  
	 *  @param resolvedreceiver The receiver.
	 *  @param priority The packet priority.
	 *  @param packet The packet.
	 */
	public TxPacket(InetSocketAddress resolvedreceiver, int priority, byte[] packet)
	{
		this(resolvedreceiver, packet, priority, null);
	}
	
	/**
	 *  Creates a new packet.
	 *  
	 *  @param resolvedreceiver The receiver.
	 *  @param packet The packet.
	 *  @param priority The packet priority.
	 *  @param sendfailuretask Callback for send failures.
	 */
	public TxPacket(InetSocketAddress resolvedreceiver, byte[] packet, int priority, Runnable sendfailuretask)
	{
		this.resolvedreceiver = resolvedreceiver;
		this.packets = new byte[][] { packet };
		this.priority = priority;
		this.sendfailuretask = sendfailuretask;
	}
	
	/**
	 *  Gets the resolved receiver.
	 *
	 *  @return The resolved receiver.
	 */
	public InetSocketAddress getResolvedReceiver()
	{
		return resolvedreceiver;
	}
	
	/**
	 *  Gets all packets in this task.
	 *  
	 *  @return All packets in this task.
	 */
	public byte[][] getPackets()
	{
		return packets;
	}
	
	/**
	 *  Returns the packet IDs of all the packets
	 *  that should be transmitted.
	 *  Returning null requests transmitting all packets.
	 *  
	 *  @return The IDs of packets that should be transmitted, null for all packets.
	 */
	public short[] getTxPacketIds()
	{
		return null;
	}
	
	/**
	 *  Gets the priority of the transmission.
	 *
	 *  @return The priority.
	 */
	public int getPriority()
	{
		return priority;
	}
	
	/**
	 *  Notification about a transmission failure.
	 *  
	 *  @param reason The reason for the failure.
	 */
	public void transmissionFailed(String reason)
	{
		if (sendfailuretask != null)
		{
			sendfailuretask.run();
		}
	}
	
	/**
	 *  Creates a re-send request for a message with small packet IDs.
	 *  
	 *  @param receiver The request receiver.
	 *  @param msgid The message ID.
	 *  @param missingpackets Array of missing packets.
	 *  @return The generated re-send request packet.
	 */
	public static final TxPacket createSmallResendRequest(InetSocketAddress receiver, int msgid, int[] missingpackets)
	{
		byte[] packetarray = new byte[5 + missingpackets.length];
		packetarray[0] = S_MSG.RESEND_REQ_ID;
		SCodingUtil.intIntoByteArray(packetarray, 1, msgid);
		
		for (int i = 0; i < missingpackets.length; ++i)
		{
			packetarray[5 + i] = (byte) missingpackets[i];
		}
		
		return new TxPacket(receiver, packetarray);
	}
	
	/**
	 *  Creates a re-send request for a message with large packet IDs.
	 *  
	 *  @param receiver The request receiver.
	 *  @param msgid The message ID.
	 *  @param missingpackets Array of missing packets.
	 *  @return The generated re-send request packet.
	 */
	public static final TxPacket createLargeResendRequest(InetSocketAddress receiver, int msgid, int[] missingpackets)
	{
		byte[] packetarray = new byte[5 + (missingpackets.length << 1)];
		packetarray[0] = L_MSG.RESEND_REQ_ID;
		SCodingUtil.intIntoByteArray(packetarray, 1, msgid);
		
		for (int i = 0; i < missingpackets.length; ++i)
		{
			SCodingUtil.shortIntoByteArray(packetarray, 5 + (i << 1), (short) missingpackets[i]);
		}
		
		return new TxPacket(receiver, packetarray);
	}
	
	/**
	 *  Creates a message re-send error packet, used when an invalid re-send request was received.
	 *  
	 *  @param receiver The packet receiver.
	 *  @param msgid The message ID.
	 *  @return The created packet.
	 */
//	public static final TxPacket createMsgResendError(InetSocketAddress receiver, int msgid)
//	{
//		return createGenericMsgIdPacket(SPacketDefs.MSG_RESEND_ERROR, receiver, msgid);
//	}
	
	/**
	 *  Creates a message fin packet, confirming an acknowledgment.
	 *  
	 *  @param receiver The packet receiver.
	 *  @param msgid The message ID.
	 *  @return The created packet.
	 */
//	public static final TxPacket createMsgFin(InetSocketAddress receiver, int msgid)
//	{
//		return createGenericMsgIdPacket(SPacketDefs.MSG_FIN, receiver, msgid);
//	}
	
	/**
	 *  Creates a packet containing only the packet type ID and a time stamp.
	 *  
	 *  @param packettype The packet type.
	 *  @param receiver The packet receiver.
	 *  @param timestamp1 The time stamp.
	 *  @return The created packet.
	 */
	public static final TxPacket createGenericTimestampPacket(byte packettype, InetSocketAddress receiver,
															  long timestamp)
	{
		byte[] rawpacket = new byte[9];
		rawpacket[0] = packettype;
		SCodingUtil.longIntoByteArray(rawpacket, 1, timestamp);
		return new TxPacket(receiver, rawpacket);
	}
	
	/**
	 *  Creates a packet containing only the packet type ID and a message ID.
	 *  
	 *  @param packettype The packet type.
	 *  @param receiver The packet receiver.
	 *  @param msgid The message ID.
	 *  @return The created packet.
	 */
	public static final TxPacket createGenericMsgIdPacket(byte packettype, InetSocketAddress receiver, int msgid)
	{
		byte[] rawpacket = new byte[5];
		rawpacket[0] = packettype;
		SCodingUtil.intIntoByteArray(rawpacket, 1, msgid);
		
		return new TxPacket(receiver, rawpacket);
	}
}
