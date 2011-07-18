package jadex.base.service.message.transport.codecs;

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

	/** The debug flag. */
	protected static boolean DEBUG = false;
	
	//-------- methods --------
	
	/**
	 *  Create a new codec.
	 */
	protected GZIPCodec()
	{
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
		return decodeBytes((byte[])bytes, classloader);
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
			int origlen = ret.length;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gzos = new GZIPOutputStream(baos);
			gzos.write(ret);
			gzos.close();
			ret = new byte[baos.size()+4];
			byte[] tmp = baos.toByteArray();
			System.arraycopy(tmp, 0, ret, 4, tmp.length);
			byte[] len = SUtil.intToBytes(origlen);	
			for(int i=0; i<len.length; i++)
			{
				ret[i] = len[i];
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		if(DEBUG)
			System.out.println("encode message: "+(new String(ret)));
		return ret;
	}

	/**
	 *  Decode bytes.
	 *  @return The decoded bytes.
	 *  @throws IOException
	 */
	public static byte[] decodeBytes(byte[] bytes, ClassLoader classloader)
	{
		byte[] ret = null;
		try
		{
			byte[] buf = new byte[4];
			ByteArrayInputStream bais = new ByteArrayInputStream((byte[])bytes);
			bais.read(buf);
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
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	
		if(DEBUG)
			System.out.println("decode message: "+(new String((byte[])bytes)));
		return ret;
	}
}