package jadex.adapter.standalone.transport.codecs;

import jadex.commons.ObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 *  The serial codec allows for encoding and decoding
 *  objects via the Java serialization mechanism. 
 *  Codec supports parallel calls of multiple concurrent 
 *  clients (no method synchronization necessary).
 */
public class SerialCodec implements IEncoder, IDecoder
{
	//-------- constants --------
	
	/** The serial codec id. */
	public static final byte CODEC_ID = 0;

	//-------- methods --------

	/**
	 *  Encode data with the codec.
	 *  @param val The value.
	 *  @return The encoded object.
	 */
	public byte[] encode(Object object)
	{
		byte[] ret = null;
		try
		{
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
	public Object decode(byte[] bytes, ClassLoader classloader)
	{
		Object ret = null;
		try
		{
			ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(baos, classloader);
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
