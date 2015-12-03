package jadex.platform.service.message.transport.codecs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.IEncodingContext;
import jadex.commons.SReflect;
import jadex.commons.transformation.binaryserializer.IErrorReporter;

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
	 *  Get the codec id.
	 *  @return The codec id.
	 */
	public byte getCodecId()
	{
		return CODEC_ID;
	}
	
	/**
	 *  Init the static methods.
	 */
	public static void init(ClassLoader classloader)
	{
		try
		{
			Class nug = SReflect.classForName("nuggets.Nuggets", classloader);
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
	public Object encode(Object val, ClassLoader classloader, IEncodingContext context)
	{
		if(otx==null)
			init(classloader);
		
		try
		{
			return ((String)otx.invoke(null, new Object[]{val, classloader})).getBytes("UTF-8");
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
	public Object decode(Object bytes, ClassLoader classloader, IErrorReporter rep)
	{
		if(otx==null)
			init(classloader);
		
		try
		{
			String	input;
			if(bytes instanceof byte[])
			{
				input	= new String((byte[])bytes,  "UTF-8");
			}
			else
			{
				InputStream	is	= (InputStream)bytes;
				byte[]	buf	= new byte[Math.max(is.available(), 1024)];
				StringBuffer	sbuf	= new StringBuffer();
				int read;
				while((read=is.read(buf))!=-1)
				{
					sbuf.append(new String(buf, 0, read, "UTF-8"));
				}
				input	= sbuf.toString();
			}
			return ofx.invoke(null, new Object[]{input, classloader});
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
