package jadex.base.service.message.transport.codecs;

import jadex.commons.SReflect;

import java.io.IOException;
import java.lang.reflect.Method;

//import nuggets.Nuggets;

/**
 *  The Nuggets XML codec. Codec supports parallel
 *  calls of multiple concurrent clients (no method
 *  synchronization necessary).
 *  
 *  Converts object -> byte[] and byte[] -> object.
 */
public class NuggetsCodec implements ICodec
{
	//-------- constants --------
	
	/** The nuggets codec id. */
	public static final byte CODEC_ID = 2;

	/** ObjectToXML method. */
	protected static Method otx;
	
	/** ObjectFromXML method. */
	protected static Method ofx;
	
	//-------- methods --------

	/**
	 *  Init the static methods.
	 */
	public static void init(ClassLoader classloader)
	{
		try
		{
			Class nug = SReflect.findClass("nuggets.Nuggets", null, classloader);
			otx = nug.getMethod("objectToXML", new Class[]{Object.class, ClassLoader.class});
			ofx = nug.getMethod("objectFromXML", new Class[]{String.class, ClassLoader.class});
		}
		catch(Exception e)
		{
			throw new RuntimeException("Nuggets not in classpath.", e);
		}
	}
	
	/**
	 *  Encode an object.
	 *  @param obj The object.
	 *  @throws IOException
	 */
//	public byte[] encode(Object val, ClassLoader classloader)
	public Object encode(Object val, ClassLoader classloader)
	{
		if(otx==null)
			init(classloader);
		
		try
		{
			return ((String)otx.invoke(null, new Object[]{val, classloader})).getBytes();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Encoding error: "+e);
		}
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
//	public Object decode(byte[] bytes, ClassLoader classloader)
	public Object decode(Object bytes, ClassLoader classloader)
	{
		if(otx==null)
			init(classloader);
		
		try
		{
			return ofx.invoke(null, new Object[]{new String((byte[])bytes), classloader});
		}
		catch(Exception e)
		{
			throw new RuntimeException("Decoding error: "+e);
		}
	}
}

// Old implementation with direct nuggets dependency
//public class NuggetsCodec implements IEncoder, IDecoder
//{
//	//-------- constants --------
//	
//	/** The nuggets codec id. */
//	public static final byte CODEC_ID = 1;
//
//	//-------- methods --------
//	
//	/**
//	 *  Encode an object.
//	 *  @param obj The object.
//	 *  @throws IOException
//	 */
//	public byte[] encode(Object val, ClassLoader classloader)
//	{
//		return Nuggets.objectToXML(val, classloader).getBytes();
//	}
//
//	/**
//	 *  Decode an object.
//	 *  @return The decoded object.
//	 *  @throws IOException
//	 */
//	public Object decode(byte[] bytes, ClassLoader classloader)
//	{
//		return Nuggets.objectFromXML(new String(bytes), classloader);
//	}
//}
