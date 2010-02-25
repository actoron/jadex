package jadex.adapter.standalone.transport.codecs;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *  The XML codec.
 *  Codec supports parallel calls of multiple concurrent 
 *  clients (no method synchronization necessary).
 */
public class XMLCodec implements IEncoder, IDecoder
{
	//-------- constants --------
	
	/** The xml codec id. */
	public static final byte CODEC_ID = 2;

	//-------- methods --------
	
	/**
	 *  Encode an object.
	 *  @param obj The object.
	 *  @throws IOException
	 */
	public byte[] encode(Object val, ClassLoader classloader)
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
	    try{baos.close();} catch(Exception e) {}
	    return baos.toByteArray();
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(byte[] bytes, ClassLoader classloader)
	{
		final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		
		XMLDecoder dec = new XMLDecoder(bais, null, new ExceptionListener()
		{
			public void exceptionThrown(Exception e)
			{
				System.out.println("XML decoding ERROR: "+bais);
				e.printStackTrace();
			}
		}, classloader);
		
		Object ret = dec.readObject();
		dec.close();
		try{bais.close();} catch(Exception e) {}
		return ret;
	}
}
