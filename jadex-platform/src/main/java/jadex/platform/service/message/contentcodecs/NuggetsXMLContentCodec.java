package jadex.platform.service.message.contentcodecs;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import jadex.bridge.service.types.message.IContentCodec;
import jadex.bridge.service.types.message.IEncodingContext;
import jadex.commons.SReflect;
import jadex.commons.transformation.binaryserializer.IErrorReporter;

//import nuggets.Nuggets;

/**
 *  The XML codec based on the nuggets framework.
 */
public class NuggetsXMLContentCodec implements IContentCodec, Serializable
{
	//-------- constants --------
	
	/** The language identifier. */
	public static final String	NUGGETS_XML	= "nuggets-xml";
	
	/** ObjectToXML method. */
	protected static Method otx;
	
	/** ObjectFromXML method. */
	protected static Method ofx;

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
	 *  Test if the codec can be used with the provided meta information.
	 *  @param props The meta information.
	 *  @return True, if it can be used.
	 */
	public boolean match(Properties props)
	{
		return NUGGETS_XML.equals(props.getProperty("language"));	// Hack!!! avoid dependency to fipa
	}

	/**
	 *  Encode data with the codec.
	 *  @param val The value.
	 *  @return The encoded object.
	 */
	public byte[] encode(Object val, ClassLoader classloader, Map<Class<?>, Object[]> info, IEncodingContext context)
	{
		if(otx==null)
			init(classloader);

		try
		{
			// todo: native byte[] methods in nuggets
			String ret = ((String)otx.invoke(null, new Object[]{val, classloader}));
			return ret.getBytes("UTF-8");
		}
		catch(Exception e)
		{
			throw new RuntimeException("Encoding error: "+e);
		}
	}

	/**
	 *  Decode data with the codec.
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public Object decode(byte[] val, ClassLoader classloader, Map<Class<?>, Object[]> info, IErrorReporter rep)
	{
		if(otx==null)
			init(classloader);

		try
		{
			// todo: native byte[] methods in nuggets
			return ofx.invoke(null, new Object[]{new String(val, "UTF-8"), classloader});
		}
		catch(Exception e)
		{
			throw new RuntimeException("Decoding error: "+e);
		}
	}
}


//public class NuggetsXMLContentCodec implements IContentCodec, Serializable
//{
//	//-------- constants --------
//	
//	/** The language identifier. */
//	public static final String	NUGGETS_XML	= "nuggets-xml";
//	
//	/** The nuggets codec. */
//	protected static Nuggets nuggets;
//
//	/**
//	 *  Test if the codec can be used with the provided meta information.
//	 *  @param props The meta information.
//	 *  @return True, if it can be used.
//	 */
//	public boolean match(Properties props)
//	{
//		return NUGGETS_XML.equals(props.getProperty("language"));	// Hack!!! avoid dependency to fipa
//	}
//
//	/**
//	 *  Encode data with the codec.
//	 *  @param val The value.
//	 *  @return The encoded object.
//	 */
//	public String encode(Object val, ClassLoader classloader)
//	{
//		if(nuggets == null)
//			nuggets = new Nuggets();
//
//		// Hack!!! context classloader is sometimes null. argl 
////		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
////		System.err.println("encoding with class loader: "+Thread.currentThread()+", "+Thread.currentThread().getContextClassLoader());
//
//		return nuggets.toXML(val, classloader);
//	}
//
//	/**
//	 *  Decode data with the codec.
//	 *  @param val The string value.
//	 *  @return The encoded object.
//	 */
//	public Object decode(String val, ClassLoader classloader)
//	{
//		if(nuggets == null)
//			nuggets = new Nuggets();
//
//		// Hack!!! context classloader is sometimes null. argl 
////		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
////		System.err.println("decoding with class loader: "+Thread.currentThread()+", "+Thread.currentThread().getContextClassLoader());
//
//		return nuggets.fromXML(val, classloader);
//	}
//}
