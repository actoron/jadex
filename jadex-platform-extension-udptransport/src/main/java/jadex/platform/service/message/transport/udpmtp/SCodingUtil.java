package jadex.platform.service.message.transport.udpmtp;


public class SCodingUtil
{
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
}
