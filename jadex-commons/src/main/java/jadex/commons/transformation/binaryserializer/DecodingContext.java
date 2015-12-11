package jadex.commons.transformation.binaryserializer;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import jadex.commons.transformation.STransformation;

/**
 * Context for decoding a binary-encoded object.
 *
 */
public class DecodingContext extends AbstractDecodingContext
{
	/** The content being decoded.*/
	protected byte[] content;
	
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
	
	/**
	 * Creates a new DecodingContext.
	 * @param classloader The classloader.
	 * @param content The content being decoded.
	 */
	public DecodingContext(byte[] content, List<IDecoderHandler> decoderhandlers, List<IDecoderHandler> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter)
	{
		this(content, decoderhandlers, postprocessors, usercontext, classloader, errorreporter, 0);
	}
	
	/**
	 * Creates a new DecodingContext with specific offset.
	 * @param content The content being decoded.
	 * @param offset The offset.
	 */
	public DecodingContext(byte[] content, List<IDecoderHandler> decoderhandlers, List<IDecoderHandler> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter, int offset)
	{
		super(decoderhandlers, postprocessors, usercontext, classloader, errorreporter);
		this.content = content;
		this.offset = offset;
		this.stringpool = new ArrayList<String>();
		this.classnamepool = new ArrayList<String>();
		this.pkgpool = new ArrayList<String>();
		//this.stringpool.addAll(BinarySerializer.DEFAULT_STRINGS);
		this.bitfield = 0;
		this.bitpos = 8;
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
	 *  Reads a byte from the buffer.
	 *  
	 *  @return A byte.
	 */
	public byte readByte()
	{
		return content[offset++];
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
	 *  Reads a number of bytes from the buffer and fills the array.
	 *  
	 *  @param array The byte array.
	 *  @return The byte array for convenience.
	 */
	public byte[] read(byte[] array)
	{
		System.arraycopy(content, offset, array, 0, array.length);
		offset += array.length;
		return array;
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
		
		ret	= STransformation.getClassname(ret);
			
		setCurrentClassName(ret);
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
