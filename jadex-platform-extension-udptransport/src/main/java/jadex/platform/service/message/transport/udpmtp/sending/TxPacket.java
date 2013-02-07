package jadex.platform.service.message.transport.udpmtp.sending;

import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.L_MSG;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.S_MSG;

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
	
	/**
	 *  Creates a new packet.
	 *  
	 *  @param resolvedreceiver The receiver.
	 *  @param packet The packet.
	 */
	public TxPacket(InetSocketAddress resolvedreceiver, byte[] packet)
	{
		this(resolvedreceiver, packet, null);
	}
	
	/**
	 *  Creates a new packet.
	 *  
	 *  @param resolvedreceiver The receiver.
	 *  @param packet The packet.
	 *  @param sendfailuretask Callback for send failures.
	 */
	public TxPacket(InetSocketAddress resolvedreceiver, byte[] packet, Runnable sendfailuretask)
	{
		this.resolvedreceiver = resolvedreceiver;
		this.packets = new byte[][] { packet };
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
		return -1;
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
	
	public static final TxPacket createMsgFin(InetSocketAddress receiver, int msgid)
	{
		byte[] finmsgpacket = new byte[5];
		finmsgpacket[0] = SPacketDefs.MSG_FIN;
		SCodingUtil.intIntoByteArray(finmsgpacket, 1, msgid);
		
		return new TxPacket(receiver, finmsgpacket);
	}
}
