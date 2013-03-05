package jadex.platform.service.message.transport.udpmtp.sending;

import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.STunables;

import java.net.InetSocketAddress;

/**
 *  A simple packet scheduled for transmission.
 *
 */
public class TxPacket
{
	/** The resolved receiver. */
	protected InetSocketAddress resolvedreceiver;
	
	/** The packet being transmitted. */
	protected byte[] packet;
	
	/** The priority of the packet. */
	protected int priority;
	
	/** The packet number. */
	protected int packetnumber;
	
	/** Flag specifying if the packet costs quota. */
	protected volatile boolean cost;
	
	/** Flag if the packet has been confirmed. */
	protected volatile boolean confirmed;
	
	/** Callback used when resends should resume. */
	protected volatile Runnable sentcallback;
	
	/** Re-send counter. */
	protected volatile int resendcounter;
	
	public volatile long sentts;
	
	/**
	 *  Creates a new packet.
	 *  
	 *  @param resolvedreceiver The receiver.
	 *  @param packet The packet.
	 */
	public TxPacket(InetSocketAddress resolvedreceiver, byte[] packet)
	{
		this(resolvedreceiver, packet, STunables.CONTROL_PACKETS_DEFAULT_PRIORITY, false);
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
		this(resolvedreceiver, packet, priority, false);
	}
	
	/**
	 *  Creates a new packet.
	 *  
	 *  @param resolvedreceiver The receiver.
	 *  @param packet The packet.
	 *  @param priority The packet priority.
	 *  @param message Enables the packet quota cost.
	 */
	public TxPacket(InetSocketAddress resolvedreceiver, byte[] packet, int priority, boolean cost)
	{
		this.resolvedreceiver = resolvedreceiver;
		this.packet = packet;
		this.priority = priority;
		this.cost = cost;
		this.confirmed = false;
		this.packetnumber = 0;
		this.resendcounter = 0;
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
	 *  Gets the raw packet.
	 *  
	 *  @return The raw packet.
	 */
	public byte[] getRawPacket()
	{
		return packet;
	}
	
	/**
	 *  Returns the packet IDs of all the packets
	 *  that should be transmitted.
	 *  Returning null requests transmitting all packets.
	 *  
	 *  @return The IDs of packets that should be transmitted, null for all packets.
	 */
//	public short[] getTxPacketIds()
//	{
//		return null;
//	}
	
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
	 *  Sets the priority.
	 *
	 *  @param priority The priority.
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
	
	/**
	 *  Gets the packet number.
	 *
	 *  @return The packet number.
	 */
	public int getPacketNumber()
	{
		return packetnumber;
	}

	/**
	 *  Sets the packet number.
	 *
	 *  @param packetnumber The packet number.
	 */
	public void setPacketNumber(int packetnumber)
	{
		this.packetnumber = packetnumber;
	}
	
	/**
	 *  Gets the re-send counter.
	 *
	 *  @return The re-send counter.
	 */
	public int getResendCounter()
	{
		return resendcounter;
	}

	/**
	 *  Sets the re-send counter.
	 *
	 *  @param resendcounter The re-send counter.
	 */
	public void setResendCounter(int resendcounter)
	{
		this.resendcounter = resendcounter;
	}
	
	/**
	 *  Gets the sent callback.
	 *
	 *  @return The sent callback.
	 */
	public Runnable getSentCallback()
	{
		return sentcallback;
	}

	/**
	 *  Sets the sent callback.
	 *
	 *  @param sentcallback The sent callback.
	 */
	public void setSentCallback(Runnable sentcallback)
	{
		this.sentcallback = sentcallback;
	}

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
	 *  Returns if the packet costs quota.
	 *
	 *  @return True, if the packet costs quota.
	 */
	public boolean isCost()
	{
		return cost;
	}
	
	

	/**
	 *  Sets the packet quota cost.
	 *
	 *  @param cost The packet quota cost value.
	 */
	public void enableCost(boolean cost)
	{
		this.cost = cost;
	}
	
	

	/**
	 *  Gets the packet confirmed value.
	 *
	 *  @return The confirmed value.
	 */
	public boolean isConfirmed()
	{
		return confirmed;
	}

	/**
	 *  Sets the packet confirmed value.
	 *
	 *  @param confirmed The packet confirmed value.
	 */
	public void setConfirmed(boolean confirmed)
	{
		this.confirmed = confirmed;
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
