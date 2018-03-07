package jadex.binary;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import jadex.commons.transformation.traverser.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 * Context for decoding a binary-encoded object.
 *
 */
public class StreamDecodingContext extends AbstractDecodingContext
{
	/** The stream being decoded.*/
	protected InputStream is;
	
	/** Current offset marker */
	protected int offset;
	
	/**
	 * Creates a new DecodingContext.
	 * @param classloader The classloader.
	 * @param content The content being decoded.
	 */
	public StreamDecodingContext(InputStream is, List<IDecoderHandler> decoderhandlers, List<ITraverseProcessor> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter, SerializationConfig config)
	{
		this(is, decoderhandlers, postprocessors, usercontext, classloader, errorreporter, config, 0);
	}
	
	/**
	 * Creates a new DecodingContext with specific offset.
	 * @param content The content being decoded.
	 * @param offset The offset.
	 */
	public StreamDecodingContext(InputStream is, List<IDecoderHandler> decoderhandlers, List<ITraverseProcessor> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter, SerializationConfig config, int offset)
	{
		super(decoderhandlers, postprocessors, usercontext, classloader, errorreporter, config);
		this.is = is;
		this.offset = 0;
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
			ret = is.read();
			++offset;
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
		
		int read = 0;
		while (read < wlength)
		{
			try
			{
				int curread = is.read(array, woffset + read, wlength - read);
				read += curread;
				offset += curread;
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
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
