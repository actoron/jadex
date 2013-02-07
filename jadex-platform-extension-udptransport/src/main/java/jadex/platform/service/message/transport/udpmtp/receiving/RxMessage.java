package jadex.platform.service.message.transport.udpmtp.receiving;

import java.util.zip.CRC32;

/**
 *  An incoming message being assembled.
 *
 */
public class RxMessage
{
	/** Bit-field of received packets. */
	protected boolean[] receivedpacketflags;
	
	/** Number of received packets. */
	protected int receivedpackets;
	
	/** The received checksum */
	protected int receivedchecksum;
	
	/** The message data */
	protected byte[] data;
	
	/** Flag whether the message is locked awaiting dispatch. */
	protected boolean locked;
	
	/**
	 *  Creates a new message being received.
	 *  
	 *  @param totalpackets Total number of packets in the message.
	 *  @param packetsize The size of the packets.
	 */
	public RxMessage(int messagesize, int totalpackets)
	{
		reset(messagesize, totalpackets);
	}
	
	/** 
	 *  Resets the received packets.
	 *  
	 *  @param packetsize The packet size.
	 */
	public void reset(int messagesize, int totalpackets)
	{
		receivedpackets = 0;
		data = new byte[messagesize];
		receivedpacketflags = new boolean[totalpackets];
	}
	
	/**
	 *  Writes a packet into the message.
	 *  
	 *  @param packetnum The packet number.
	 *  @param packet The packet.
	 *  @param packetdataoffset Offset where the data is found in the packet.
	 */
	public void writePacket(int packetnum, byte[] packet, int packetdataoffset)
	{
		synchronized (this)
		{
			if (!locked && !receivedpacketflags[packetnum])
			{
				int packetdatasize = packet.length - packetdataoffset;
				
				int dataoffset = packetnum * packetdatasize;
				if (packetnum == receivedpacketflags.length - 1)
				{
					dataoffset = data.length - packetdatasize;
				}
				
				System.arraycopy(packet, packetdataoffset, data, dataoffset, packetdatasize);
				++receivedpackets;
				receivedpacketflags[packetnum] = true;
			}
		}
	}
	
	/**
	 *  Checks if the message is complete.
	 *  
	 *  @return True, if all packets have been received.
	 */
	public boolean isComplete()
	{
		return receivedpacketflags.length == receivedpackets;
	}
	
	/**
	 *  Sets the received checksum.
	 *
	 *  @param receivedchecksum The received checksum.
	 */
	public void setReceivedChecksum(int receivedchecksum)
	{
//		System.out.println("Setting received checksum for received packet: " + receivedchecksum);
		this.receivedchecksum = receivedchecksum;
	}

	/**
	 *  Checks if the checksum matches and, if so, locks the message against further writes.
	 *  
	 *  @return True, if the checksum matches and the message is locked.
	 */
	public boolean confirmChecksumAndLock()
	{
		CRC32 crc = new CRC32();
		crc.update(data);
		
		int cs = (int) crc.getValue();
//		System.out.println("Calculated checksum on rx packet: " + cs);
		
		if (cs == receivedchecksum)
		{
			locked = true;
			return true;
		}
		return false;
	}
	
	/**
	 *  Returns the packets currently missing in this message.
	 *  
	 *  @return The missing packet numbers.
	 */
	public int[] getMissingPackets()
	{
		int[] tmpret = new int[receivedpacketflags.length];
		int count = 0;
		for (int i = 0; i < receivedpacketflags.length; ++i)
		{
			if (!receivedpacketflags[i])
			{
				tmpret[++count] = i;
			}
		}
		
		int[] ret = new int[count];
		System.arraycopy(tmpret, 0, ret, 0, ret.length);
		
		return ret;
	}
	
	public int getSize()
	{
		return data.length;
	}
	
	/**
	 *  Checks if the message is dispatched.
	 *  
	 *  @return True, if the message has been dispatched.
	 */
	public boolean isDispatched()
	{
		return locked;
	}

	/**
	 *  Gets the message data.
	 *
	 *  @return The message data.
	 */
	public byte[] getData()
	{
		return data;
	}
}
