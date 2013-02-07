package jadex.platform.service.message.transport.udpmtp.sending;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.zip.CRC32;

import jadex.commons.future.Future;
import jadex.platform.service.message.transport.udpmtp.ParsedAddress;
import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.L_MSG;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.S_MSG;

/**
 *  Class representing a message being send.
 *
 */
public class TxMessage implements ITxTask
{
	/** The receiver of the message. */
	protected ParsedAddress receiver;
	
	/** The resolved receiver. */
	protected InetSocketAddress resolvedreceiver;
	
	/** Future confirming the transfer. */
	protected Future<Void> confirmationfuture;
	
	/** The priority of the message */
	protected int priority;
	
	/** The packets being transmitted. */
	protected byte[][] packets;
	
	/** The packets that need to be re-send. */
	protected short[] resendpackets;
	
	/**
	 *  Creates a new message for transmission.
	 *  
	 *  @param receiver The receiver of the message.
	 *  @param conffuture The confirmation future.
	 *  @param priority Initial priority of the message.
	 *  @param packets The packets of the message.
	 */
	public TxMessage(ParsedAddress receiver, Future<Void> conffuture, int priority, byte[][] packets)
	{
		this.receiver = receiver;
		confirmationfuture = conffuture;
		this.priority = priority;
		this.packets = packets;
	}
	
	/**
	 *  Gets the receiver.
	 *
	 *  @return The receiver.
	 */
	public ParsedAddress getReceiver()
	{
		return receiver;
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
	public byte[][] getPackets()
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
	 *  Sets the re-send packets.
	 *
	 *  @param resendpackets The re-send packets.
	 */
	public void setResendPackets(short[] resendpackets)
	{
		this.resendpackets = resendpackets;
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
		return resendpackets;
	}
	
	/**
	 *  Reduces the priority of the message.
	 */
	public void reducePriority()
	{
		++priority;
	}
	
	/**
	 *  Confirms the transmission of the message.
	 */
	public void confirmTransmission()
	{
		confirmationfuture.setResult(null);
	}
	
	/**
	 *  Notification about a transmission failure.
	 *  
	 *  @param reason The reason for the failure.
	 */
	public void transmissionFailed(String reason)
	{
		confirmationfuture.setException(new IOException(reason));
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
	public static final byte[][] fragmentMessage(int msgid, byte[] prolog, byte[] data, int packetsize)
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
		byte[][] ret = new byte[packettotal][];
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
			ret[i] = packet;
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
