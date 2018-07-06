package org.activecomponents.udp;

import java.security.SecureRandom;


/**
 *  Utility functions and variables
 *
 */
public class SUdpUtil
{
	/** Random number source. */
	public static volatile SecureRandom RANDOM;
	
	/**
	 *  Writes a short value into a byte array buffer in network byte order.
	 *  
	 *  @param buffer The buffer.
	 *  @param offset The offset.
	 *  @param val The value.
	 */
	public static final void shortIntoByteArray(byte[] buffer, int offset, short val)
	{
		assert offset + 1 < buffer.length;
		
		buffer[offset] = (byte)((val >>> 8) & 0xFF);
		buffer[offset + 1] = (byte)(val & 0xFF);
	}
	
	/**
	 *  Reads a short value from a byte array buffer in network byte order.
	 *  
	 *  @param buffer The buffer.
	 *  @param offset The offset.
	 *  @return The value.
	 */
	public static final short shortFromByteArray(byte[] buffer, int offset)
	{
		assert offset + 1 < buffer.length;
		
		short val = (short)((0xFF & buffer[offset]) << 8);
		val |= (0xFF & buffer[offset + 1]);
		
		return val;
	}
	
	/**
	 *  Writes an integer value into a byte array buffer in network byte order.
	 *  
	 *  @param buffer The buffer.
	 *  @param offset The offset.
	 *  @param val The value.
	 */
	public static final void intIntoByteArray(byte[] buffer, int offset, int val)
	{
		assert offset + 3 < buffer.length;
		
		buffer[offset] = (byte)((val >>> 24) & 0xFF);
		buffer[offset + 1] = (byte)((val >>> 16) & 0xFF);
		buffer[offset + 2] = (byte)((val >>> 8) & 0xFF);
		buffer[offset + 3] = (byte)(val & 0xFF);
	}
	
	/**
	 *  Reads an integer value from a byte array buffer in network byte order.
	 *  
	 *  @param buffer The buffer.
	 *  @param offset The offset.
	 *  @return The value.
	 */
	public static final int intFromByteArray(byte[] buffer, int offset)
	{
		assert offset + 3 < buffer.length;
		
		int val = ((0xFF & buffer[offset]) << 24);
		val |= (0xFF & buffer[offset + 1]) << 16;
		val |= (0xFF & buffer[offset + 2]) << 8;
		val |= (0xFF & buffer[offset + 3]);
		
		return val;
	}

	/**
	 *  Writes a long value into a byte array buffer in network byte order.
	 *  
	 *  @param buffer The buffer.
	 *  @param offset The offset.
	 *  @param val The value.
	 */
	public static final void longIntoByteArray(byte[] buffer, int offset, long val)
	{
		assert offset + 7 < buffer.length;
		
		buffer[offset] = (byte)((val >>> 56) & 0xFF);
		buffer[offset + 1] = (byte)((val >>> 48) & 0xFF);
		buffer[offset + 2] = (byte)((val >>> 40) & 0xFF);
		buffer[offset + 3] = (byte)((val >>> 32) & 0xFF);
		buffer[offset + 4] = (byte)((val >>> 24) & 0xFF);
		buffer[offset + 5] = (byte)((val >>> 16) & 0xFF);
		buffer[offset + 6] = (byte)((val >>> 8) & 0xFF);
		buffer[offset + 7] = (byte)(val & 0xFF);
	}
	
	/**
	 *  Reads a long value from a byte array buffer in network byte order.
	 *  
	 *  @param buffer The buffer.
	 *  @param offset The offset.
	 *  @return The value.
	 */
	public static final long longFromByteArray(byte[] buffer, int offset)
	{
		assert offset + 7 < buffer.length;

		long value = (0xFFL & buffer[offset]) << 56L;
		value |= (0xFFL & buffer[offset + 1]) << 48L;
		value |= (0xFFL & buffer[offset + 2]) << 40L;
		value |= (0xFFL & buffer[offset + 3]) << 32L;
		value |= (0xFFL & buffer[offset + 4]) << 24L;
		value |= (0xFFL & buffer[offset + 5]) << 16L;
		value |= (0xFFL & buffer[offset + 6]) << 8L;
		value |= (0xFFL & buffer[offset + 7]);

		return value;
	}
	
//	public static void main(String[] args)
//	{
//		byte[] bytes = new byte[1024];
//		long ts = System.currentTimeMillis();
//		for (int i = 0; i < 1024*1024; ++i)
//		{
//			getSecRandom().nextBytes(bytes);
//		}
//		System.out.println(1024.0/((System.currentTimeMillis() - ts)/1000.0));
//	}
	
	/**
	 *  Gets the secure PRNG.
	 *  @return Secure PRNG.
	 */
	public static final SecureRandom getSecRandom()
	{
		if (RANDOM == null)
		{
			synchronized (SUdpUtil.class)
			{
				if (RANDOM == null)
				{
					RANDOM = new SecureRandom();
				}
			}
		}
		return RANDOM;
	}
}
