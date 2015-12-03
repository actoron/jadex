package jadex.platform.service.message.transport.codecs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.IEncodingContext;
import jadex.commons.beans.ExceptionListener;
import jadex.commons.beans.XMLDecoder;
import jadex.commons.beans.XMLEncoder;
import jadex.commons.transformation.binaryserializer.IErrorReporter;

/**
 *  The XML codec.
 *  Codec supports parallel calls of multiple concurrent 
 *  clients (no method synchronization necessary).
 *  
 *  Converts object -> byte[] and byte[] -> object.
 */
public class XMLCodec implements ICodec
{
	//-------- constants --------
	
	/** The xml codec id. */
	public static final byte CODEC_ID = 3;

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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    XMLEncoder enc = new XMLEncoder(baos);
		enc.setExceptionListener(new ExceptionListener()
		{
			public void exceptionThrown(Exception e)
			{
				System.out.println("XML encoding ERROR: ");
				e.printStackTrace();
			}
		});
	    enc.writeObject(val);
	    enc.close();
	    try
	    {
	    	baos.close();
	    }
	    catch(Exception e)
	    {
			System.out.println("XML encoding ERROR: ");
			e.printStackTrace();	    	
	    }
	    return baos.toByteArray();
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
//	public Object decode(byte[] bytes, ClassLoader classloader)
	public Object decode(Object bytes, ClassLoader classloader, final IErrorReporter rep)
	{
		final InputStream bais = bytes instanceof byte[] ? new ByteArrayInputStream((byte[])bytes) : (InputStream)bytes;
		
		XMLDecoder dec = new XMLDecoder(bais, null, new ExceptionListener()
		{
			public void exceptionThrown(Exception e)
			{
				rep.exceptionOccurred(e);
//				System.out.println("XML decoding ERROR: "+bais);
//				e.printStackTrace();
			}
		}, classloader);
		
		Object ret = dec.readObject();
		dec.close();
		try
		{
			bais.close();
		}
		catch(Exception e)
		{
			rep.exceptionOccurred(e);
		}
		return ret;
	}
}
