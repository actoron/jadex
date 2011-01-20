package jadex.base.service.message.transport.codecs;

import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.io.IOException;

/**
 *  The Jadex XML codec. Codec supports parallel
 *  calls of multiple concurrent clients (no method
 *  synchronization necessary).
 */
public class JadexXMLCodec implements IEncoder, IDecoder
{
	//-------- constants --------
	
	/** The nuggets codec id. */
	public static final byte CODEC_ID = 3;

	/** The debug flag. */
	protected boolean DEBUG = false;
	
	//-------- methods --------
	
	/**
	 *  Encode an object.
	 *  @param obj The object.
	 *  @throws IOException
	 */
	public byte[] encode(Object val, ClassLoader classloader)
	{
		byte[] ret = JavaWriter.objectToByteArray(val, classloader);
		if(DEBUG)
			System.out.println("encode message: "+(new String(ret)));
		return ret;
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(byte[] bytes, ClassLoader classloader)
	{
		Object ret = JavaReader.objectFromByteArray(bytes, classloader);
		if(DEBUG)
			System.out.println("decode message: "+(new String(bytes)));
		return ret;
	}
}