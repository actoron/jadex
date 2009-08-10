package jadex.adapter.base;

import jadex.bridge.IContentCodec;
import jadex.commons.xml.bean.BeanObjectReaderHandler;
import jadex.commons.xml.bean.BeanObjectWriterHandler;
import jadex.commons.xml.reader.Reader;
import jadex.commons.xml.writer.Writer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Properties;

public class JadexXMLContentCodec implements IContentCodec, Serializable
{
	//-------- constants --------
	
	/** The language identifier. */
	public static final String	JADEX_XML	= "nuggets-xml";
	
	/** The reader. */
	protected Reader reader = new Reader(new BeanObjectReaderHandler(), null);
	
	/** The writer. */
	protected Writer writer = new Writer(new BeanObjectWriterHandler(), null);

	
	/**
	 *  Test if the codec can be used with the provided meta information.
	 *  @param props The meta information.
	 *  @return True, if it can be used.
	 */
	public boolean match(Properties props)
	{
		return JADEX_XML.equals(props.getProperty("language"));	// Hack!!! avoid dependency to fipa
	}

	/**
	 *  Encode data with the codec.
	 *  @param val The value.
	 *  @return The encoded object.
	 */
	public String encode(Object val, ClassLoader classloader)
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			writer.write(val, bos, classloader, null);
			byte[] ret = bos.toByteArray();
			bos.close();
			System.out.println("encode: "+val+" "+ret);
			return new String(ret);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 *  Decode data with the codec.
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public Object decode(String val, ClassLoader classloader)
	{
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(val.getBytes());
			Object ret = reader.read(bis, classloader, null);
			bis.close();
			System.out.println("decode: "+ret);
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
