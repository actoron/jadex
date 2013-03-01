package jadex.platform.service.message.transport.udpmtp.sending;

import jadex.commons.future.Future;
import jadex.platform.service.message.ISendTask;
import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.L_MSG;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.S_MSG;
import jadex.platform.service.message.transport.udpmtp.STunables;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.zip.CRC32;

/**
 *  Class representing a message being send.
 *
 */
public class TxMessage
{
	/** The message ID. */
	protected int msgid;
	
	/** The resolved receiver. */
	protected InetSocketAddress resolvedreceiver;
	
	/** Future confirming the transfer. */
	protected Future<Void> confirmationfuture;
	
	/** The priority of the message */
	protected int priority;
	
	/** The packets being transmitted. */
	protected TxPacket[] packets;
	
	/** Re-send counter. */
	protected volatile int resendcounter;
	
	/** Callback used when resends should start. */
	protected Runnable sentcallback;
	
	/** Counter for sent packets. */
	protected volatile int lastsentpacket;
	
	public volatile long ls;
	
	/**
	 *  Creates a new message for transmission.
	 *  
	 *  @param msgid The message ID.
	 *  @param receiver The receiver of the message.
	 *  @param conffuture The confirmation future.
	 *  @param priority Initial priority of the message.
	 *  @param packets The packets of the message.
	 *  @param txcallback Called when transmission is done.
	 */
	protected TxMessage(int msgid, InetSocketAddress receiver, Future<Void> conffuture, int priority, TxPacket[] packets, Runnable sentcallback)
	{
		this.msgid = msgid;
		confirmationfuture = conffuture;
		resolvedreceiver = receiver;
		this.priority = priority;
		this.packets = packets;
		this.lastsentpacket = packets.length - 1;
		this.sentcallback = sentcallback;
//		this.confirmedpackets = new int[packets.length / 32 + 1];
		this.resendcounter = 0;
		resetConfirmedPackets();
		
	}
	
	/**
	 *  Resets the confirmed packets.
	 */
	public void resetConfirmedPackets()
	{
		for (int i = 0; i < packets.length; ++i)
		{
			packets[i].setConfirmed(false);
		}
//		synchronized (confirmedpackets)
//		{
//			Arrays.fill(confirmedpackets, 0);
//			int padmax = (confirmedpackets.length * 32);
//			for (int i = packets.length; i <  padmax; ++i)
//			{
//				confirmedpackets[i / 32] |= (1L << (i % 32));
//			}
//		}
	}
	
	/**
	 *  Gets the message ID.
	 *
	 *  @return The message ID.
	 */
	public int getMsgId()
	{
		return msgid;
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
	 *  Sets the resolved receiver.
	 *
	 *  @param resolvedreceiver The resolved receiver.
	 */
	public void setResolvedReceiver(InetSocketAddress resolvedreceiver)
	{
		this.resolvedreceiver = resolvedreceiver;
	}

	/**
	 *  Gets the packets.
	 *
	 *  @return The packets.
	 */
	public TxPacket[] getPackets()
	{
		return packets;
	}
	
	/**
	 *  Gets the priority.
	 *
	 *  @return The priority.
	 */
	public int getPriority()
	{
		return priority;
	}
	
	/**
	 *  Gets the number of sent packets.
	 *
	 *  @return The number of sent packets.
	 */
//	public int getSentPackets()
//	{
//		return sentpackets;
//	}

	/**
	 *  Sets the number of sent packets.
	 *
	 *  @param sentpackets The number of sent packets.
	 */
//	public void setSentPackets(int sentpackets)
//	{
//		this.sentpackets = sentpackets;
//	}

	/**
	 *  Gets the confirmed packets bit-field.
	 *
	 *  @return The confirmed packets bit-field.
	 */
//	public int[] getConfirmedPackets()
//	{
//		return confirmedpackets;
//	}
	
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
	 *  Gets the sent packet.
	 *
	 *  @return The sent packets.
	 */
	public int getLastSentPacket()
	{
		return lastsentpacket;
	}

	/**
	 *  Sets the sent packet.
	 *
	 *  @param lastsentpacket The sent packet.
	 */
	public void setLastSentPacket(int lastsentpacket)
	{
		this.lastsentpacket = lastsentpacket;
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
	 *  Increases the priority of the message.
	 */
	public void increasePriority()
	{
		priority = Math.max(0, priority - 1);
	}

	/**
	 *  Reduces the priority of the message.
	 */
	public void decreasePriority()
	{
		++priority;
	}
	
	/**
	 *  Confirms the transmission of the message.
	 */
	public void confirmTransmission()
	{
		confirmationfuture.setResultIfUndone(null);
	}
	
	/**
	 *  Sets the transmission callback.
	 *
	 *  @param txcallback The transmission callback.
	 */
//	public void setTxCallback(Runnable txcallback)
//	{
//		this.txcallback = txcallback;
//	}

	/**
	 *  Notification when transmission is done.
	 */
//	public void done()
//	{
//		if (txcallback != null)
//		{
//			txcallback.run();
//		}
//	}
	
	/**
	 *  Notification about a transmission failure.
	 *  
	 *  @param reason The reason for the failure.
	 */
	public void transmissionFailed(String reason)
	{
		System.err.println("Transmission failed: " + reason);
		confirmationfuture.setExceptionIfUndone(new IOException(reason));
	}
	
	/**
	 *  Creates a message for transmission.
	 *  
	 *  @param msgid The allocated message ID.
	 *  @param address
	 *  @param task
	 *  @param conffuture
	 *  @param txcallback Called when transmission is done.
	 *  @param allowtiny Allow tiny packet mode.
	 *  @param allowsmall Allow small packet mode.
	 *  @return
	 */
	public static final TxMessage createTxMessage(InetSocketAddress receiver, int msgid, ISendTask task, Future<Void> conffuture, Runnable txcallback, boolean allowtiny, boolean allowsmall)
	{
		int payloadsize = task.getProlog().length + task.getData().length;
		System.out.println("New Message " + msgid + ", size: " + payloadsize);
		int priority = STunables.LARGE_MESSAGES_DEFAULT_PRIORITY;
		
		// Packet size for medium and large mode.
		int packetsize = 8192;
		
		if (allowtiny && payloadsize < 131073)
		{
			// Tiny mode
			packetsize = 512;
			priority = STunables.TINY_MESSAGES_DEFAULT_PRIORITY;
		}
		else if (allowsmall && payloadsize < 262145)
		{
			// Small mode
			packetsize = 1024;
			priority = STunables.SMALL_MESSAGES_DEFAULT_PRIORITY;
		}
		else if (payloadsize < 2097153)
		{
			// Medium mode
			priority = STunables.MEDIUM_MESSAGES_DEFAULT_PRIORITY;
		}
		
		TxPacket[] packets = TxMessage.fragmentMessage(receiver, msgid, priority, task.getProlog(), task.getData(), packetsize, txcallback);
		
		TxMessage ret = new TxMessage(msgid, receiver, conffuture, priority, packets, txcallback);
		
		return ret;
	}
	
	/**
	 *  Fragments a message into packets and prefixes them with the packet header.
	 *  
	 *  @param msgid The message ID.
	 *  @param prolog The message prolog.
	 *  @param data The message data.
	 *  @param packetsize The chosen packet size.
	 *  @return The fragmented message.
	 */
	public static final TxPacket[] fragmentMessage(InetSocketAddress receiver, int msgid, int priority, byte[] prolog, byte[] data, int packetsize, final Runnable txcallback)
	{
//		System.out.println("Fragmenting message of size " + (prolog.length + data.length) + " msg id " + msgid);
		int payloadsize = prolog.length + data.length;
		int packettotal = ((payloadsize - 1) / packetsize) + 1;
		int baseheadersize = packettotal > S_MSG.MAX_PACKETS?
						 L_MSG.HEADER_SIZE : S_MSG.HEADER_SIZE;
		
//		System.out.println("Fragmented message into " + packettotal + " packets of " + packetsize + " bytes");
		CRC32 crc = new CRC32();
		crc.update(prolog);
		crc.update(data);
		
		int checksum = (int) crc.getValue();
//		System.out.print("cs1 : " + checksum);
		int pos = 0;
		TxPacket[] ret = new TxPacket[packettotal];
		for (int i = 0; i < packettotal; ++i)
		{
			byte[] packet = null;
			int headersize = i != 0? baseheadersize: baseheadersize + SPacketDefs.CHECKSUM_SIZE;
			if (pos < prolog.length)
			{
				// Prolog not yet written.
				
				int premains = prolog.length - pos;
				if (premains < packetsize)
				{
					// Prolog / Data Packet Overlap
					
					if ((premains + data.length) <= packetsize)
					{
						// Remaining Data fits in packet.
						
						packet = new byte[headersize + premains + data.length];
						createMsgPacketHeader(packet, msgid, payloadsize, i, packettotal, checksum);
						System.arraycopy(prolog, pos, packet, headersize, premains);
						System.arraycopy(data, 0, packet, headersize + premains, data.length);
					}
					else
					{
						// Rest of Prolog plus some of the data.
						
						packet = new byte[headersize + packetsize];
						createMsgPacketHeader(packet, msgid, payloadsize, i, packettotal, checksum);
						System.arraycopy(prolog, pos, packet, headersize, premains);
						System.arraycopy(data, 0, packet, headersize +  premains, packetsize - premains);
					}
				}
				else
				{
					// Partial Prolog packet.
					
					packet = new byte[headersize + packetsize];
					createMsgPacketHeader(packet, msgid, payloadsize, i, packettotal, checksum);
					System.arraycopy(prolog, pos, packet, headersize, packetsize);
				}
			}
			else
			{
				// Just data remains.
				
				int dpos = pos - prolog.length;
				
				int dremains = data.length - dpos;
				if (dremains <= packetsize)
				{
					// Rest of the data.
					
					packet = new byte[headersize + dremains];
					createMsgPacketHeader(packet, msgid, payloadsize, i, packettotal, checksum);
					System.arraycopy(data, dpos, packet, headersize, dremains);
				}
				else
				{
					// Partial data packet.
					
					packet = new byte[headersize + packetsize];
					createMsgPacketHeader(packet, msgid, payloadsize, i, packettotal, checksum);
					System.arraycopy(data, dpos, packet, headersize, packetsize);
				}
			}
			
			pos += (packet.length - headersize);
			
			
			ret[i] = new TxPacket(receiver, packet, priority, true);
			ret[i].setPacketNumber(i);
		}
		
		return ret;
	}
	
	/**
	 *  Creates the header of a message packet.
	 *  
	 *  @param packet The packet array in which the header is written.
	 *  @param msgid The message ID.
	 *  @param msgsize The total size of the message.
	 *  @param pnum The packet number.
	 *  @param ptotal The total number of packets in the message.
	 *  @param checksum The message checksum, only relevant for the first packet.
	 */
	protected static final void createMsgPacketHeader(byte[] packet, int msgid, int msgsize, int pnum, int ptotal, int checksum)
	{
		if (pnum > S_MSG.MAX_PACKETS)
		{
			SCodingUtil.intIntoByteArray(packet, L_MSG.MSG_ID_OFFSET, msgid);
			SCodingUtil.intIntoByteArray(packet, L_MSG.MSG_SIZE_OFFSET, msgsize);
			packet[0] = L_MSG.PACKET_TYPE_ID;
			SCodingUtil.shortIntoByteArray(packet, L_MSG.PACKET_NUMBER_OFFSET, (short) pnum);
			SCodingUtil.shortIntoByteArray(packet, L_MSG.TOTAL_PACKETS_OFFSET, (short) ptotal);
			
			if (pnum == 0)
			{
				SCodingUtil.intIntoByteArray(packet, L_MSG.HEADER_SIZE, checksum);
			}
		}
		else
		{
			SCodingUtil.intIntoByteArray(packet, S_MSG.MSG_ID_OFFSET, msgid);
			SCodingUtil.intIntoByteArray(packet, S_MSG.MSG_SIZE_OFFSET, msgsize);
			packet[0] = S_MSG.PACKET_TYPE_ID;
			packet[S_MSG.PACKET_NUMBER_OFFSET] = (byte)pnum;
			packet[S_MSG.TOTAL_PACKETS_OFFSET] = (byte)ptotal;
			
			if (pnum == 0)
			{
//				System.out.println("Writing checksum: " + checksum);
				SCodingUtil.intIntoByteArray(packet, S_MSG.HEADER_SIZE, checksum);
//				System.out.println("Reading checksum: " + SCodingUtil.intFromByteArray(packet, S_MSG.HEADER_SIZE));
			}
		}
	}
}
