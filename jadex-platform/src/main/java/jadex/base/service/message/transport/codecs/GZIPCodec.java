package jadex.base.service.message.transport.codecs;

import jadex.bridge.service.types.message.ICodec;
import jadex.commons.SUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *  Converts byte[] -> byte[] in both directions.
 */
public class GZIPCodec implements ICodec
{
	//-------- constants --------
	
	/** The gzip codec id. */
	public static final byte CODEC_ID = 5;

	//-------- methods --------
	
	/**
	 *  Create a new codec.
	 */
	public GZIPCodec()
	{
	}
	
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
	public Object encode(Object val, ClassLoader classloader)
	{
		return encodeBytes((byte[])val, classloader);
	}
	
	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(Object bytes, ClassLoader classloader)
	{
		return decodeBytes(bytes instanceof byte[] ? new ByteArrayInputStream((byte[])bytes) : (ByteArrayInputStream)bytes, classloader);
	}
	
	/**
	 *  Encode an object.
	 *  @param obj The object.
	 *  @throws IOException
	 */
	public static byte[] encodeBytes(byte[] val, ClassLoader classloader)
	{
		byte[] ret = (byte[])val;

		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(SUtil.intToBytes(val.length));
			GZIPOutputStream gzos = new GZIPOutputStream(baos);
			gzos.write(val);
			gzos.close();
			ret = baos.toByteArray();
		}
		catch(Exception e) 
		{
			throw e instanceof RuntimeException? (RuntimeException)e: new RuntimeException(e);
		}
		
		return ret;
	}

	/**
	 *  Decode bytes.
	 *  @return The decoded bytes.
	 *  @throws IOException
	 */
	public static byte[] decodeBytes(byte[] bytes, ClassLoader classloader)
	{
		return decodeBytes(new ByteArrayInputStream(bytes), classloader);
	}
	
	/**
	 *  Decode bytes.
	 *  @return The decoded bytes.
	 *  @throws IOException
	 */
	public static byte[] decodeBytes(ByteArrayInputStream bais, ClassLoader classloader)
	{
		byte[] ret = null;
		try
		{
			byte[] buf = new byte[4];
			int	read;
			int count	= 0;
			while(count<4 && (read=bais.read(buf, count, 4-count))!=-1)
			{
				count	+= read;
			}
			
			int len = SUtil.bytesToInt(buf);
			ret = new byte[len];
			GZIPInputStream gzis = new GZIPInputStream(bais);
			// read method only reads up to length of bytes :-(
			int sum = 0;
			while((len = gzis.read(ret, sum, ret.length-sum))>0) 
			{
				sum += len;
			}
		}
		catch(Exception e) 
		{
			throw e instanceof RuntimeException? (RuntimeException)e: new RuntimeException(e);
		}
	
		return ret;
	}
}