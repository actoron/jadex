package jadex.adapter.standalone.transport.codecs;

import jadex.commons.xml.bean.BeanObjectReaderHandler;
import jadex.commons.xml.bean.BeanObjectWriterHandler;
import jadex.commons.xml.reader.Reader;
import jadex.commons.xml.writer.Writer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

	/** The reader. */
	protected Reader reader = new Reader(new BeanObjectReaderHandler(), null);
	
	/** The writer. */
	protected Writer writer = new Writer(new BeanObjectWriterHandler(), null);
	
	/** The debug flag. */
	protected boolean DEBUG;
	
	//-------- methods --------
	
	/**
	 *  Encode an object.
	 *  @param obj The object.
	 *  @throws IOException
	 */
	public byte[] encode(Object val, ClassLoader classloader)
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			writer.write(val, bos, classloader, null);
			byte[] ret = bos.toByteArray();
			bos.close();
			if(DEBUG)
				System.out.println("encode: "+val+" "+ret);
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(byte[] bytes, ClassLoader classloader)
	{
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			Object ret = reader.read(bis, classloader, null);
			bis.close();
			if(DEBUG)
				System.out.println("decode: "+ret);
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}