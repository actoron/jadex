package jadex.platform.service.serialization.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import jadex.binary.SBinarySerializer;
import jadex.bridge.service.types.message.ISerializer;
import jadex.commons.SUtil;
import jadex.commons.transformation.traverser.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  The Jadex Binary serializer. Codec supports parallel
 *  calls of multiple concurrent clients (no method
 *  synchronization necessary).
 *  
 *  Converts object -> byte[] and byte[] -> object.
 */
public class JadexFramedBinarySerializer implements ISerializer
{
	//-------- constants --------
	
	/** The JadexBinary serializer id. */
	public static final int SERIALIZER_ID = 2;
	
	/** The debug flag. */
	protected boolean DEBUG = false;
	
	//-------- methods --------
	
	/**
	 *  Get the serializer id.
	 *  @return The serializer id.
	 */
	public int getSerializerId()
	{
		return SERIALIZER_ID;
	}
	
	/**
	 *  Encode data with the serializer.
	 *  @param val The value.
	 *  @param classloader The classloader.
	 *  @param preproc The encoding preprocessors.
	 *  @return The encoded object.
	 */
	public byte[] encode(Object val, ClassLoader classloader, ITraverseProcessor[] preprocs, Object usercontext)
	{
		byte[] ret = SBinarySerializer.writeObjectToFramedArray(val, preprocs!=null?Arrays.asList(preprocs):null, null, usercontext, classloader, null);
		
		if(DEBUG)
			System.out.println("encode message: "+(new String(ret, SUtil.UTF8)));
		return ret;
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(byte[] bytes, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep, Object usercontext)
	{
		if(DEBUG)
			System.out.println("decode message: "+(new String((byte[])bytes, SUtil.UTF8)));
		
		InputStream is = new ByteArrayInputStream((byte[]) bytes);
		
		return decode(is, classloader, postprocs, rep, usercontext);
	}
	
	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(InputStream is, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep)
	{
		return decode(is, classloader, postprocs, rep, null);
	}
	
	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(InputStream is, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep, Object usercontext)
	{
		
		Object ret = SBinarySerializer.readObjectFromFramedStream(is, postprocs!=null?Arrays.asList(postprocs):null, usercontext, classloader, null, null);// CONFIG);
		
		try
		{
			is.close();
		}
		catch (IOException e)
		{
		}
		
		
		return ret;
	}
}