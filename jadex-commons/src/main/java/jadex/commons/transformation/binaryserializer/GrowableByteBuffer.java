package jadex.commons.transformation.binaryserializer;


/**
 *  A growable byte buffer similar to ByteArrayOutputStream but allowing
 *  access to all written parts of the array.
 *  
 * @author jander
 *
 */
public class GrowableByteBuffer
{
	/** The buffer */
	protected byte[] buffer;
	
	/** Current position */
	protected int pos;
	
	/**
	 *  Creates an empty buffer.
	 */
	public GrowableByteBuffer()
	{
		buffer = new byte[512];
		pos = 0;
	}
	
	/**
	 *  Writes a byte array, appending it to the buffer.
	 *  @param b The byte array.
	 */
	public void write (byte[] b)
	{
		if (b.length > (buffer.length - pos))
		{
			int newlength = buffer.length << 1;
			while (b.length > (newlength - pos))
				newlength <<= 1;
			byte[] newbuffer = new byte[newlength];
			System.arraycopy(buffer, 0, newbuffer, 0, pos);
			buffer = newbuffer;
		}
		
		System.arraycopy(b, 0, buffer, pos, b.length);
		pos += b.length;
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
	
	public byte[] toByteArray()
	{
		byte[] ret = new byte[pos];
		System.arraycopy(buffer, 0, ret, 0, pos);
		return ret;
	}
}
