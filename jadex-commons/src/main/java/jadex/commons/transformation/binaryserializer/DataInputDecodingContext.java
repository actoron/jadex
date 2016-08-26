package jadex.commons.transformation.binaryserializer;


import java.io.DataInput;
import java.io.IOException;
import java.util.List;

import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 * Context for decoding a binary-encoded object.
 *
 */
public class DataInputDecodingContext extends AbstractDecodingContext
{
	/** The stream being decoded.*/
//	protected InputStream is;
	protected DataInput di;
	
	/** Current offset marker */
	protected int offset;
	
	/**
	 * Creates a new DecodingContext.
	 * @param classloader The classloader.
	 * @param content The content being decoded.
	 */
	public DataInputDecodingContext(DataInput di, List<IDecoderHandler> decoderhandlers, List<ITraverseProcessor> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter, SerializationConfig config)
	{
		this(di, decoderhandlers, postprocessors, usercontext, classloader, errorreporter, config, 0);
	}
	
	/**
	 * Creates a new DecodingContext with specific offset.
	 * @param content The content being decoded.
	 * @param offset The offset.
	 */
	public DataInputDecodingContext(DataInput di, List<IDecoderHandler> decoderhandlers, List<ITraverseProcessor> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter, SerializationConfig config, int offset)
	{
		super(decoderhandlers, postprocessors, usercontext, classloader, errorreporter, config);
		this.di = di;
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
			++offset;
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
		return read(array, -1, -1);
	}
	
	/**
	 *  Reads a number of bytes from the buffer and fills the array.
	 *  
	 *  @param array The byte array.
	 *  @param woffset write offset.
	 *  @param wlength length to read.
	 *  @return The byte array for convenience.
	 */
	public byte[] read(byte[] array, int woffset, int wlength)
	{
		woffset = woffset < 0? 0: woffset;
		wlength = wlength < 0? array.length - woffset : wlength;
		if (wlength == 0)
			return array;
		
		try
		{
			di.readFully(array, woffset, wlength);
			offset+=wlength;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		return array;
	}
	
	/**
	 *  Returns the current offset of the decoding process for debugging.
	 *  @return Current offset.
	 */
	public int getCurrentOffset()
	{
		return offset;
	}
}
