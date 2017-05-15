package jadex.platform.service.message.transport.codecs;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import jadex.commons.SUtil;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

/**
 *  Converts byte[] -> byte[] in both directions.
 */
public class LZ4Codec extends AbstractCodec
{
	//-------- constants --------
	
	/** Factory for LZ4 */
	protected static final LZ4Factory LZ4FACTORY = LZ4Factory.fastestInstance();
	
	/** The gzip codec id. */
	public static final byte CODEC_ID = 2;
	
	protected LZ4Compressor compressor = LZ4FACTORY.fastCompressor();
	
	protected LZ4FastDecompressor decompressor = LZ4FACTORY.fastDecompressor();

	//-------- methods --------
	
	/**
	 *  Create a new codec.
	 */
	public LZ4Codec()
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
		return encodeBytes(compressor, val);
	}
	
	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public byte[] decode(Object bytes)
	{
		return decodeBytes(decompressor, (ByteArrayInputStream)bytes);
	}
	
	/**
	 *  Decode bytes.
	 *  @return The decoded bytes.
	 *  @throws IOException
	 */
	public byte[] decode(byte[] bytes, int offset, int length)
	{
		return decodeBytes(decompressor, bytes, offset, length);
	}
	
	/**
	 *  Encode an object.
	 *  @param obj The object.
	 *  @throws IOException
	 */
	public static byte[] encodeBytes(LZ4Compressor compressor, byte[] val)
	{
		byte[] ret = new byte[compressor.maxCompressedLength(val.length)];
		
		try
		{
			int compsize = compressor.compress(val, 0, val.length, ret, 0, ret.length);
			byte[] tmp = new byte[compsize + 8];
			SUtil.intIntoBytes(compsize, tmp, 0);
			SUtil.intIntoBytes(val.length, tmp, 4);
			System.arraycopy(ret, 0, tmp, 8, compsize);
			ret = tmp;
		}
		catch(Exception e) 
		{
			SUtil.rethrowAsUnchecked(e);
		}
		
		return ret;
	}

	/**
	 *  Decode bytes.
	 *  @return The decoded bytes.
	 *  @throws IOException
	 */
	public static byte[] decodeBytes(LZ4FastDecompressor decompressor, byte[] bytes, int offset, int length)
	{
		return decodeBytes(decompressor, new ByteArrayInputStream(bytes, offset, length));
	}
	
	/**
	 *  Decode bytes.
	 *  @return The decoded bytes.
	 *  @throws IOException
	 */
	public static byte[] decodeBytes(LZ4FastDecompressor decompressor, ByteArrayInputStream bais)
	{
		byte[] ret = null;
		try
		{
			byte[] buf = new byte[8];
			int	read;
			int count	= 0;
			while(count<8 && (read=bais.read(buf, count, 8-count))!=-1)
			{
				count	+= read;
			}
			
			int clen = SUtil.bytesToInt(buf, 0);
			int ulen = SUtil.bytesToInt(buf, 4);
			byte[] in = new byte[clen];
			SUtil.readStream(in, 0, -1, bais);
			ret = new byte[ulen];
			decompressor.decompress(in, 0, ret, 0, ret.length);
		}
		catch(Exception e) 
		{
			SUtil.rethrowAsUnchecked(e);
		}
	
		return ret;
	}
}