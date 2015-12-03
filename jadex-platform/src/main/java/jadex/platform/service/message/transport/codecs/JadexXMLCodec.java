package jadex.platform.service.message.transport.codecs;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.IEncodingContext;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

/**
 *  The Jadex XML codec. Codec supports parallel
 *  calls of multiple concurrent clients (no method
 *  synchronization necessary).
 *  
 *  Converts object -> byte[] and byte[] -> object.
 */
public class JadexXMLCodec implements ICodec
{
	//-------- constants --------
	
	/** The JadexXML codec id. */
	public static final byte CODEC_ID = 4;

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
//	public byte[] encode(Object val, ClassLoader classloader)
	public Object encode(Object val, ClassLoader classloader, IEncodingContext context)
	{
		byte[] ret = JavaWriter.objectToByteArray(val, classloader);
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
//	public Object decode(byte[] bytes, ClassLoader classloader)
	public Object decode(Object bytes, ClassLoader classloader, IErrorReporter rep)
	{
		Object ret = bytes instanceof byte[]
			? JavaReader.objectFromByteArray((byte[])bytes, classloader, rep)
			: JavaReader.objectFromInputStream((InputStream)bytes, classloader, rep);
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