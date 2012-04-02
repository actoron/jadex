package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
	//protected Map<Integer, String> stringpool;
	protected List<String> stringpool;
	
	/** The class name pool. */
	protected List<String> classnamepool;
	
	/** The package fragment pool. */
	protected List<String> pkgpool;
	
	/** The current bitfield (used for boolean values). */
	protected byte bitfield;
	
	/** The current bit position within the bitfield */
	protected byte bitpos;
	
	/** Already known objects */
	protected Map<Integer, Object> knownobjects;
	
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
		this.stringpool = new ArrayList<String>();
		this.classnamepool = new ArrayList<String>();
		this.pkgpool = new ArrayList<String>();
		//this.stringpool.addAll(BinarySerializer.DEFAULT_STRINGS);
		this.knownobjects = new HashMap<Integer, Object>();
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
	 *  Returns the known objects.
	 *  @return Known objects.
	 */
	public Map<Integer, Object> getKnownObjects()
	{
		return knownobjects;
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
	/*public Map<Integer, String> getStringPool()
	{
		return stringpool;
	}*/
	
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
	public boolean readBoolean()
	{
		if (bitpos > 7)
		{
			bitfield = content[offset];
			++offset;
			bitpos = 0;
		}
		
		boolean ret = ((bitfield >>> bitpos) & 1) == 1? true: false;
		++bitpos;
		
		return ret;
	}
	
	/**
	 *  Gets a ByteBuffer window of the content.
	 *  
	 *  @param length The length in bytes.
	 *  @return The ByteBuffer.
	 */
	public ByteBuffer getByteBuffer(int length)
	{
		ByteBuffer ret = ByteBuffer.wrap(content, offset, length);
		offset += length;
		return ret;
	}
	
	/**
	 *  Helper method for decoding a class name.
	 *  @return String encoded at the current position.
	 */
	public String readClassname()
	{
		String ret = null;
		
		int classid = (int) readVarInt();
		if (classid >= classnamepool.size())
		{
			int count = (int) readVarInt();
			StringBuilder cnb = new StringBuilder();
			for (int i = 0; i < count; ++i)
			{
				int fragid = (int) readVarInt();
				String frag = null;
				if (fragid >= pkgpool.size())
				{
					frag = readString();
					pkgpool.add(frag);
				}
				else
				{
					frag = pkgpool.get(fragid);
				}
				cnb.append(frag);
				cnb.append(".");
			}
			cnb.append(readString());
			ret = cnb.toString();
			classnamepool.add(ret);
		}
		else
		{
			ret = classnamepool.get(classid);
		}
		
		return ret;
	}
	
	/**
	 *  Helper method for decoding a string.
	 *  @return String encoded at the current position.
	 */
	public String readString()
	{
		int sid = (int) readVarInt();
		String ret = null;
		if (sid < stringpool.size())
		{
			ret = stringpool.get(sid);
		}
		else
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
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
			offset += length;
			stringpool.add(ret);
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
		boolean neg = readBoolean();
		byte ext = VarInt.getExtensionSize(content, offset);
		long ret = VarInt.decodeWithKnownSize(content, offset, ext);
		offset += (ext + 1);
		if (neg)
			ret = -ret;
		return ret;
	}
}
