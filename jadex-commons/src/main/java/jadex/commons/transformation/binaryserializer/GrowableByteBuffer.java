package jadex.commons.transformation.binaryserializer;

import java.nio.ByteBuffer;


/**
 *  A growable byte buffer similar to ByteArrayOutputStream but allowing
 *  access to all written parts of the array.
 */
public class GrowableByteBuffer
{
	/** Initial buffer size. */
	protected static final int INITIAL_SIZE = 4096;
	
	/** Buffer growth aggressiveness. */
	protected static final int GROWTH_EXPONENT = 2;
	
	/** Constant for a single zero byte. */
	public static final byte[] ZERO_BYTE = new byte[] {0};
	
	/** The buffer */
	protected byte[] buffer;
	
	/** Current position */
	protected int pos;
	
	/**
	 *  Creates an empty buffer.
	 */
	public GrowableByteBuffer()
	{
		buffer = new byte[INITIAL_SIZE];
		pos = 0;
	}
	
	/**
	 *  Writes a byte, appending it to the buffer.
	 *  @param b The byte.
	 */
	public void write(byte b)
	{
		allocateSpace(1);
		
		buffer[pos++] = b;
	}
	
	/**
	 *  Writes a byte array, appending it to the buffer.
	 *  @param b The byte array.
	 */
	public void write(byte[] b)
	{
		allocateSpace(b.length);
		
		System.arraycopy(b, 0, buffer, pos, b.length);
		pos += b.length;
	}
	
	/**
	 *  Reserves a byte buffer.
	 */
	public ByteBuffer getByteBuffer(int length)
	{
		allocateSpace(length);
		
		ByteBuffer ret = ByteBuffer.wrap(buffer, pos, length);
		pos += length;
		
		return ret;
	}
	
	/**
	 *  Writes a single byte to a position in the buffer.
	 *  @param p The position.
	 *  @param b The byte.
	 */
	public void writeTo(int p, byte b)
	{
		buffer[p] = b;
	}
	
	/** 
	 *  Returns the position in the buffer that is next written to.
	 *  @return The position.
	 */
	public int getPosition()
	{
		return pos;
	}
	
	/**
	 *  Direct buffer access, handle with care.
	 *  @return The internal buffer.
	 */
	public byte[] getBufferAccess()
	{
		return buffer;
	}
	
	/**
	 *  Reserves a minimum amount of free space in the buffer and shifts the position past it.
	 *  
	 */
	protected void reserveSpace(int length)
	{
		allocateSpace(length);
		pos += length;
	}
	
	public byte[] toByteArray()
	{
		byte[] ret = new byte[pos];
		System.arraycopy(buffer, 0, ret, 0, pos);
		return ret;
	}
	
	/**
	 *  Allocates a minimum amount of free space in the buffer.
	 *  
	 */
	protected void allocateSpace(int length)
	{
		if (length > (buffer.length - pos))
		{
			int newlength = buffer.length << GROWTH_EXPONENT;
			while (length > (newlength - pos))
				newlength <<= GROWTH_EXPONENT;
			byte[] newbuffer = new byte[newlength];
			System.arraycopy(buffer, 0, newbuffer, 0, pos);
			buffer = newbuffer;
		}
	}
}
