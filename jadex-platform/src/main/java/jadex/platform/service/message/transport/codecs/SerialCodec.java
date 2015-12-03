package jadex.platform.service.message.transport.codecs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.IEncodingContext;
import jadex.commons.CLObjectInputStream;
import jadex.commons.transformation.binaryserializer.IErrorReporter;

/**
 *  The serial codec allows for encoding and decoding
 *  objects via the Java serialization mechanism. 
 *  Codec supports parallel calls of multiple concurrent 
 *  clients (no method synchronization necessary).
 *  
 *  Converts object -> byte[] and byte[] -> object.
 */
public class SerialCodec implements ICodec
{
	//-------- constants --------
	
	/** The serial codec id. */
	public static final byte CODEC_ID = 1;

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
	 *  Encode data with the codec.
	 *  @param val The value.
	 *  @return The encoded object.
	 */
//	public byte[] encode(Object object, ClassLoader classloader)
	public Object encode(Object object, ClassLoader classloader, IEncodingContext context)
	{
		byte[] ret = null;
		try
		{
			// todo: use classloader for sth?
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ret = baos.toByteArray();
			baos.close();
			oos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Error encoding value: "+object);
		}
		return ret;
	}

	/**
	 *  Decode data with the codec.
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
//	public Object decode(byte[] bytes, ClassLoader classloader)
	public Object decode(Object bytes, ClassLoader classloader, IErrorReporter rep)
	{
		Object ret = null;
		try
		{
			InputStream baos = bytes instanceof byte[] ? new ByteArrayInputStream((byte[])bytes) : (InputStream)bytes;
			CLObjectInputStream ois = new CLObjectInputStream(baos, classloader);
			ret = ois.readObject();
			baos.close();
			ois.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Error decoding value: "+bytes);
		}
		return ret;
	}
}
