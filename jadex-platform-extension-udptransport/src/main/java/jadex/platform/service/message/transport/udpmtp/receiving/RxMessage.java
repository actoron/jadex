package jadex.platform.service.message.transport.udpmtp.receiving;

import java.util.zip.CRC32;

/**
 *  An incoming message being assembled.
 *
 */
public class RxMessage
{
	/** Bit-field of received packets. */
	protected int[] receivedpacketflags;
	//protected boolean[] receivedpacketflags;
	
	/** Number of received packets. */
	protected int receivedpackets;
	
	/** The received checksum */
	protected int receivedchecksum;
	
	/** The total number of packets */
	protected int totalpackets;
	
	/** The message data */
	protected byte[] data;
	
	/** The size of the raw packets. */
	protected byte rawsize;
	
	/** Number of unconfirmed writes */
	protected volatile int unconfirmedwrites;
	
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
		this.rawsize = 0;
		this.totalpackets = totalpackets;
		this.locked = false;
		receivedpackets = 0;
		data = new byte[messagesize];
		//receivedpacketflags = new boolean[totalpackets];
		
		receivedpacketflags = new int[((totalpackets - 1) / 32) + 1];
		int padmax = (receivedpacketflags.length * 32);
		for (int i = totalpackets; i <  padmax; ++i)
		{
			receivedpacketflags[i / 32] |= (1L << (i % 32));
		}
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
			int flag = 1 << (packetnum % 32);
			try
			{
				int flagblock = packetnum / 32;
				if (!locked && (receivedpacketflags[flagblock] & flag) == 0)
				{
					int packetdatasize = packet.length - packetdataoffset;
					
					int dataoffset = packetnum * packetdatasize;
					if (packetnum == totalpackets - 1)
					{
						dataoffset = data.length - packetdatasize;
					}
					
					System.arraycopy(packet, packetdataoffset, data, dataoffset, packetdatasize);
					++receivedpackets;
					++unconfirmedwrites;
					rawsize += packet.length;
					receivedpacketflags[flagblock] |= flag;
				}
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				System.out.println("AOOB: " + packetnum + " " + totalpackets + " " + receivedpacketflags.length);
			}
		}
	}
	
	
	
	/**
	 *  Gets the unconfirmed writes.
	 *
	 *  @return The unconfirmed writes.
	 */
	public int getUnconfirmedWrites()
	{
		return unconfirmedwrites;
	}

	/**
	 *  Sets the unconfirmed writes.
	 *
	 *  @param unconfirmedwrites The unconfirmed writes.
	 */
	public void setUnconfirmedWrites(int unconfirmedwrites)
	{
		this.unconfirmedwrites = unconfirmedwrites;
	}

	/**
	 *  Checks if the message is complete.
	 *  
	 *  @return True, if all packets have been received.
	 */
	public boolean isComplete()
	{
		return totalpackets == receivedpackets;
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
//		System.err.println("Calculated checksum on rx packet: " + cs + " should be: " + receivedchecksum);
		
		if (cs == receivedchecksum)
		{
			locked = true;
		}
		return locked;
	}
	
	/**
	 *  Returns the packets currently missing in this message.
	 *  
	 *  @return The missing packet numbers.
	 */
	public int[] getMissingPacketsBitfield()
	{
//		int[] tmpret = new int[receivedpacketflags.length];
//		int count = 0;
//		for (int i = 0; i < receivedpacketflags.length; ++i)
//		{
//			if (!receivedpacketflags[i])
//			{
//				tmpret[++count] = i;
//			}
//		}
//		
//		int[] ret = new int[count];
//		System.arraycopy(tmpret, 0, ret, 0, ret.length);
		
		int[] ret = new int[receivedpacketflags.length];
		synchronized (this)
		{
			System.arraycopy(receivedpacketflags, 0, ret, 0, ret.length);
		}
		
		return ret;
	}
	
	public int getSize()
	{
		return data.length;
	}
	
	
	
	/**
	 *  Gets the raw size.
	 *
	 *  @return The raw size.
	 */
	public byte getRawSize()
	{
		return rawsize;
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
