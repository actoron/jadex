package jadex.commons.transformation.binaryserializer;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context for decoding a binary-encoded object.
 *
 */
public class DecodingContext
{
	/** The postprocessors. */
	protected List<IDecoderHandler> postprocessors;
	
	/** The last decoded object */
	protected Object lastobject;
	
	/** A user context. */
	protected Object usercontext;
	
	/** The content being decoded.*/
	protected byte[] content;
	
	/** The classloader */
	protected ClassLoader classloader;
	
	/** The current offset */
	protected int offset;
	
	/** The String pool. */
	protected Map<Integer, String> stringpool;
	
	/** The current bitfield (used for boolean values). */
	protected byte bitfield;
	
	/** The current bit position within the bitfield */
	protected byte bitpos;
	
	/**
	 * Creates a new DecodingContext.
	 * @param classloader The classloader.
	 * @param content The content being decoded.
	 */
	public DecodingContext(byte[] content, List<IDecoderHandler> postprocessors, Object usercontext, ClassLoader classloader)
	{
		this(content, postprocessors, usercontext, classloader, 0);
	}
	
	/**
	 * Creates a new DecodingContext with specific offset.
	 * @param content The content being decoded.
	 * @param offset The offset.
	 */
	public DecodingContext(byte[] content, List<IDecoderHandler> postprocessors, Object usercontext, ClassLoader classloader, int offset)
	{
		this.content = content;
		this.postprocessors = postprocessors;
		this.classloader = classloader;
		this.usercontext = usercontext;
		this.offset = offset;
		this.stringpool = new HashMap<Integer, String>();
		//this.stringpool.put(0, "java.lang.String");
		//this.stringpool.put(1, "byte[]");
		this.bitfield = 0;
		this.bitpos = 8;
	}
	
	/**
	 *  Returns the handlers used for post-processing.
	 *  @return Post-processing handlers.
	 */
	public List<IDecoderHandler> getPostProcessors()
	{
		return postprocessors;
	}
	
	
	
	/**
	 *  Returns the last object decoded.
	 *  @return The last object decoded.
	 */
	public Object getLastObject()
	{
		return lastobject;
	}
	
	/**
	 *  Returns the user context.
	 *  @return The user context.
	 */
	public Object getUserContext()
	{
		return usercontext;
	}

	/**
	 *  Sets the last object decoded.
	 *  @param lastobject The last object decoded.
	 */
	public void setLastObject(Object lastobject)
	{
		this.lastobject = lastobject;
	}

	/**
	 * Increases the offset.
	 * @param val The value to increase the offset.
	 */
	public void incOffset(int val)
	{
		this.offset += val;
	}
	
	/**
	 * Gets the current offset.
	 * @param offset The offset.
	 */
	public int getOffset()
	{
		return offset;
	}
	
	/**
	 * Gets the classloader.
	 * @return The classloader.
	 */
	public ClassLoader getClassloader()
	{
		return classloader;
	}
	
	/**
	 * Gets the content being decoded.
	 * @return The content.
	 */
	public byte[] getContent()
	{
		return content;
	}
	
	/**
	 * Gets the String pool.
	 * @return The string pool.
	 */
	public Map<Integer, String> getStringPool()
	{
		return stringpool;
	}
	
	/**
	 *  Reads a number of bytes from the buffer.
	 *  
	 *  @param count Number of bytes.
	 *  @return Byte array with the bytes.
	 */
	public byte[] read(int count)
	{
		byte[] ret = new byte[count];
		
		System.arraycopy(content, offset, ret, 0, count);
		offset += count;
		
		return ret;
	}
	
	/**
	 *  Reads a boolean value from the buffer.
	 *  @return Boolean value.
	 */
	public Boolean readBool()
	{
		if (bitpos > 7)
		{
			bitfield = content[offset];
			++offset;
			bitpos = 0;
		}
		
		Boolean ret = ((bitfield >>> bitpos) & 1) == 1? Boolean.TRUE: Boolean.FALSE;
		++bitpos;
		
		return ret;
	}
	
	/**
	 *  Helper method for decoding a string.
	 *  @return String encoded at the current position.
	 */
	public String readString()
	{
		int sid = (int) readVarInt();
		String ret = stringpool.get(sid);
		if (ret == null)
		{
			int length = (int) readVarInt();
			try
			{
				ret = new String(content, offset, length, "UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
			offset += length;
			stringpool.put(sid, ret);
		}
		return ret;
	}
	
	/**
	 *  Helper method for decoding a variable-sized integer (VarInt).
	 *  @return The decoded value.
	 */
	public long readVarInt()
	{
		byte ext = VarInt.getExtensionSize(content, offset);
		long ret = VarInt.decodeWithKnownSize(content, offset, ext);
		offset += (ext + 1);
		return ret;
	}
	
	/**
	 *  Helper method for decoding a signed variable-sized integer (VarInt).
	 *  @return The decoded value.
	 */
	public long readSignedVarInt()
	{
		boolean neg = readBool();
		byte ext = VarInt.getExtensionSize(content, offset);
		long ret = VarInt.decodeWithKnownSize(content, offset, ext);
		offset += (ext + 1);
		if (neg)
			ret = -ret;
		return ret;
	}
}
