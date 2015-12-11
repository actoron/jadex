package jadex.commons.transformation.binaryserializer;


import java.io.DataInput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jadex.commons.transformation.STransformation;

/**
 * Context for decoding a binary-encoded object.
 *
 */
public class DataInputDecodingContext extends AbstractDecodingContext
{
	/** The stream being decoded.*/
//	protected InputStream is;
	protected DataInput di;
	
	/** The String pool. */
	//protected Map<Integer, String> stringpool;
	protected List<String> stringpool;
	
	/** The class name pool. */
	protected List<String> classnamepool;
	
	/** The package fragment pool. */
	protected List<String> pkgpool;
	
	/**
	 * Creates a new DecodingContext.
	 * @param classloader The classloader.
	 * @param content The content being decoded.
	 */
	public DataInputDecodingContext(DataInput di, List<IDecoderHandler> decoderhandlers, List<IDecoderHandler> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter)
	{
		this(di, decoderhandlers, postprocessors, usercontext, classloader, errorreporter, 0);
	}
	
	/**
	 * Creates a new DecodingContext with specific offset.
	 * @param content The content being decoded.
	 * @param offset The offset.
	 */
	public DataInputDecodingContext(DataInput di, List<IDecoderHandler> decoderhandlers, List<IDecoderHandler> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter, int offset)
	{
		super(decoderhandlers, postprocessors, usercontext, classloader, errorreporter);
		this.di = di;
		this.stringpool = new ArrayList<String>();
		this.classnamepool = new ArrayList<String>();
		this.pkgpool = new ArrayList<String>();
		//this.stringpool.addAll(BinarySerializer.DEFAULT_STRINGS);
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
		int ret = 0;
		try
		{
			ret = di.readByte();
			if (ret == -1)
			{
				throw new RuntimeException("Stream ended unexpectedly during read.");
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return (byte) ret;
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
		
		read(ret);
		
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
		try
		{
			di.readFully(array);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		return array;
	}
	
	/**
	 *  Reads a boolean value from the buffer.
	 *  @return Boolean value.
	 */
	public boolean readBoolean()
	{
		return readByte() > 0;
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
				byte[] content = read(length);
				ret = new String(content, "UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
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
		byte fb = readByte(); //VarInt.getExtensionSize(content, offset);
		byte ext = VarInt.getExtensionSize(fb);
		byte[] content = new byte[ext + 1];
		content[0] = fb;
		
		int read = 0;
		try
		{
			di.readFully(content, 1, content.length - 1);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		return VarInt.decodeWithKnownSize(content, 0, ext);
	}
	
	/**
	 *  Helper method for decoding a signed variable-sized integer (VarInt).
	 *  @return The decoded value.
	 */
	public long readSignedVarInt()
	{
		long ret = readVarInt();
		long mask = Long.highestOneBit(ret);
		mask |= mask >> 1;
		boolean neg = (Long.bitCount(ret & mask)) > 1;
		ret = ret & (~mask);
		
		if (neg)
			ret = -ret;
		return ret;
	}
}
