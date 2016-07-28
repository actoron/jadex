package jadex.platform.service.message.transport.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.ISerializer;
import jadex.commons.transformation.binaryserializer.IDecoderHandler;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.binaryserializer.SBinarySerializer2;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  The Jadex Binary serializer. Codec supports parallel
 *  calls of multiple concurrent clients (no method
 *  synchronization necessary).
 *  
 *  Converts object -> byte[] and byte[] -> object.
 */
public class JadexBinarySerializer implements ISerializer
{
	//-------- constants --------
	
	/** The JadexBinary serializer id. */
	public static final byte SERIALIZER_ID = 0;
	
	/** The debug flag. */
	protected boolean DEBUG = false;
	
	/** Current preprocessors. */
	protected ITraverseProcessor[] preprocessors;
	
	/** Current postprocessors. */
	protected ITraverseProcessor[] postprocessors;
	
	
	//-------- methods --------
	
	/**
	 *  Get the serializer id.
	 *  @return The serializer id.
	 */
	public byte getSerializerId()
	{
		return SERIALIZER_ID;
	}
	
	/**
	 *  Configures the preprocessor stage of the encoding.
	 *  @param processors The preprocessors.
	 */
	public void setPreprocessors(ITraverseProcessor[] processors)
	{
		preprocessors = processors;
	}
	
	/**
	 *  Configures the postprocessor stage of the encoding.
	 *  @param processors The postprocessors.
	 */
	public void setPostprocessors(ITraverseProcessor[] processors)
	{
		postprocessors = processors;
	}
	
	/**
	 *  Encode data with the serializer.
	 *  @param val The value.
	 *  @param classloader The classloader.
	 *  @param preproc The encoding preprocessors.
	 *  @return The encoded object.
	 */
	public byte[] encode(Object val, ClassLoader classloader, ITraverseProcessor[] preprocs)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SBinarySerializer2.writeObjectToStream(baos, val, preprocs!=null?Arrays.asList(preprocs):null, null, null, classloader);
		
		byte[] ret = baos.toByteArray();
		
		if(DEBUG)
		{
			try
			{
				System.out.println("encode message: "+(new String(ret, "UTF-8")));
			}
			catch(UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
		}
		return ret;
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(Object bytes, ClassLoader classloader, IDecoderHandler[] postprocs, IErrorReporter rep)
	{
		InputStream is = null;
		if (bytes instanceof byte[])
		{
			is = new ByteArrayInputStream((byte[]) bytes);
		}
		else
		{
			is = (InputStream) bytes;
		}
		
		Object ret = SBinarySerializer2.readObjectFromStream(is, postprocs!=null?Arrays.asList(postprocs):null, null, classloader, null);
		
		try
		{
			is.close();
		}
		catch (IOException e)
		{
		}
		
		if(DEBUG)
		{
			try
			{
				System.out.println("decode message: "+(new String((byte[])bytes, "UTF-8")));
			}
			catch(UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
		}
		return ret;
	}
}