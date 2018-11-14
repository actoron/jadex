package jadex.platform.service.serialization.codecs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import jadex.commons.SUtil;

/**
 *  Converts byte[] -> byte[] in both directions.
 */
public class XZCodec extends AbstractCodec
{
	//-------- constants --------
	
	/** The gzip codec id. */
	public static final int CODEC_ID = 1;

	//-------- methods --------
	
	/**
	 *  Create a new codec.
	 */
	public XZCodec()
	{
	}
	
	/**
	 *  Get the codec id.
	 *  @return The codec id.
	 */
	public int getCodecId()
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
		return decodeBytes((ByteArrayInputStream)bytes);
	}
	
	/**
	 *  Decode bytes.
	 *  @return The decoded bytes.
	 *  @throws IOException
	 */
	public byte[] decode(byte[] bytes, int offset, int length)
	{
		return decodeBytes(bytes, offset, length);
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
			
			XZOutputStream xzos = new XZOutputStream(baos, new LZMA2Options(LZMA2Options.PRESET_MAX));
			xzos.write(val);
			xzos.close();
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
	public static byte[] decodeBytes(byte[] bytes, int offset, int length)
	{
		return decodeBytes(new ByteArrayInputStream(bytes, offset, length));
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
			XZInputStream xzis = new XZInputStream(bais);
			SUtil.readStream(ret, 0, -1, xzis);
		}
		catch(Exception e) 
		{
			throw e instanceof RuntimeException? (RuntimeException)e: new RuntimeException(e);
		}
	
		return ret;
	}
}