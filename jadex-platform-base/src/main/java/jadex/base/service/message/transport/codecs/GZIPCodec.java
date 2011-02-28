package jadex.base.service.message.transport.codecs;

import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 
 */
public class GZIPCodec implements IEncoder, IDecoder
{
	//-------- constants --------
	
	/** The gzip codec id. */
	public static final byte CODEC_ID = 4;

	/** The debug flag. */
	protected boolean DEBUG = false;
	
	/** The underlying codec. */
	protected IEncoder encoder;
	protected IDecoder decoder;
	
	protected ByteArrayOutputStream baos;
	protected GZIPOutputStream gzos;

	protected ByteArrayInputStream bais;
	protected GZIPInputStream gzis;

	
	//-------- methods --------
	
	/**
	 * 
	 */
	protected GZIPCodec()
	{
		JadexXMLCodec codec = new JadexXMLCodec();
		encoder = codec;
		decoder = codec;
		try
		{
			baos = new ByteArrayOutputStream();
			gzos = new GZIPOutputStream(baos);
			bais = new ByteArrayInputStream(new byte[0]);
			gzis = new GZIPInputStream(bais);
		}
		catch(Exception e)
		{
		}
	}
	
	/**
	 *  Encode an object.
	 *  @param obj The object.
	 *  @throws IOException
	 */
	public byte[] encode(Object val, ClassLoader classloader)
	{
		byte[] ret = encoder.encode(val, classloader);

		try
		{
			gzos.write(ret);
			ret = baos.toByteArray();
		}
		catch (Exception e) 
		{
		}
		finally
		{
			baos.reset();
		}
		
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
		byte[] decbytes = new byte[bytes.length];
		try
		{
			bais = new ByteArrayInputStream(decbytes);
			gzis = new GZIPInputStream(bais);
			gzis.read(bytes);
		}
		catch (Exception e) 
		{
		}
		finally
		{
			baos.reset();
		}
	
		Object ret = decoder.decode(bytes, classloader);
		
		if(DEBUG)
			System.out.println("decode message: "+(new String(bytes)));
		return ret;
	}
}