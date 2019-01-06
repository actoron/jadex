package jadex.binary;

import java.io.DataOutput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jadex.commons.SUtil;
import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Context for encoding (serializing) an object in a binary format.
 *
 */
public class DataOutputEncodingContext extends AbstractEncodingContext
{
	/** The binary output */
	protected DataOutput dato;
	
	/**
	 *  Creates an encoding context.
	 *  @param usercontext A user context.
	 *  @param preprocessors The preprocessors.
	 *  @param classloader The classloader.
	 */
	public DataOutputEncodingContext(DataOutput dato, Object rootobject, Object usercontext, List<ITraverseProcessor> preprocessors, ClassLoader classloader, SerializationConfig config)
	{
		super(rootobject, usercontext, preprocessors, classloader, config);
		this.dato = dato;
	}
	
	/**
	 *  Writes a byte.
	 *  @param b The byte.
	 */
	public void writeByte(byte b)
	{
		try
		{
			dato.writeByte(b);
			++writtenbytes;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Writes a byte array, appending it to the buffer.
	 *  @param b The byte array.
	 */
	public void write(byte[] b)
	{
		try
		{
			dato.write(b);
			writtenbytes += b.length;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	
}
