package jadex.platform.service.message.transport.codecs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import jadex.bridge.service.types.message.IBinaryCodec;
import jadex.commons.SUtil;

/**
 *  Converts byte[] -> byte[] in both directions.
 */
public class GZIPCodec implements IBinaryCodec
{
	//-------- constants --------
	
	/** The gzip codec id. */
	public static final byte CODEC_ID = 0;

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
	public byte[] encode(byte[] val)
	{
		return encodeBytes(val);
	}
	
	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public byte[] decode(Object bytes)
	{
		return decodeBytes(bytes instanceof byte[] ? new ByteArrayInputStream((byte[])bytes) : (ByteArrayInputStream)bytes);
	}
	
	/**
	 *  Encode an object.
	 *  @param obj The object.
	 *  @throws IOException
	 */
	public static byte[] encodeBytes(byte[] val)
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
	public static byte[] decodeBytes(byte[] bytes)
	{
		return decodeBytes(new ByteArrayInputStream(bytes));
	}
	
	/**
	 *  Decode bytes.
	 *  @return The decoded bytes.
	 *  @throws IOException
	 */
	public static byte[] decodeBytes(ByteArrayInputStream bais)
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