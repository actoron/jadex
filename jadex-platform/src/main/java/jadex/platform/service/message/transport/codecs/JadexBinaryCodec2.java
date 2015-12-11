package jadex.platform.service.message.transport.codecs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.IEncodingContext;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.binaryserializer.SBinarySerializer2;

/**
 *  The Jadex Binary codec. Codec supports parallel
 *  calls of multiple concurrent clients (no method
 *  synchronization necessary).
 *  
 *  Converts object -> byte[] and byte[] -> object.
 */
public class JadexBinaryCodec2 implements ICodec
{
	//-------- constants --------
	
	/** The JadexBinary codec id. */
	public static final byte CODEC_ID = 7;
	
	/** The debug flag. */
	protected boolean DEBUG = false;
	
	
	//-------- methods --------
	
	/**
	 *  Get the codec id.
	 *  @return The codec id.
	 */
	public byte getCodecId()
	{
		return CODEC_ID;
	}
	
	/**
	 *  Encode an object.
	 *  @param obj The object.
	 *  @throws IOException
	 */
	public Object encode(Object val, ClassLoader classloader, IEncodingContext context)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SBinarySerializer2.writeObjectToStream(baos, val, null, null, null, classloader);
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
	public Object decode(Object bytes, ClassLoader classloader, IErrorReporter rep)
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
		
		Object ret = SBinarySerializer2.readObjectFromStream(is, null, null, classloader, null);
		
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