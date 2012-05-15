package jadex.base.contentcodecs;

import jadex.bridge.service.types.message.IContentCodec;
import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

/**
 *  The XML codec based on the standard Java 1.4 XMLCodec.
 */
public class JavaXMLContentCodec implements IContentCodec, Serializable
{
	//-------- constants --------
	
	/** The java xml language. */
	public static final String	JAVA_XML	= "java-xml";
	
	/**
	 *  Test if the codec can be used with the provided meta information.
	 *  @param props The meta information.
	 *  @return True, if it can be used.
	 */
	public boolean match(Properties props)
	{
		return JAVA_XML.equals(props.getProperty("language"));
	}

	/**
	 *  Encode data with the codec.
	 *  @param val The value.
	 *  @return The encoded object.
	 */
	public synchronized byte[] encode(Object val, ClassLoader classloader, Map<Class<?>, Object[]> info)
	{
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		XMLEncoder e = new XMLEncoder(bs);
		e.setExceptionListener(new ExceptionListener()
		{
			public void exceptionThrown(Exception e)
			{
				System.out.println("XML encoding ERROR: ");
				e.printStackTrace();
			}
		});
		Thread.currentThread().setContextClassLoader(classloader);
//		System.err.println("encoding with class loader: "+Thread.currentThread()+", "+Thread.currentThread().getContextClassLoader());
		e.writeObject(val);
		e.close();
		return bs.toByteArray();
	}

	/**
	 *  Decode data with the codec.
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public synchronized Object decode(final byte[] val, ClassLoader classloader, Map<Class<?>, Object[]> info)
	{
		assert val != null;

		ByteArrayInputStream bs = new ByteArrayInputStream(val);
		XMLDecoder d = new XMLDecoder(bs, null, new ExceptionListener()
		{
			public void exceptionThrown(Exception e)
			{
				System.err.println("XML decoding ERROR: "+val);
				e.printStackTrace();
			}
		});
		Thread.currentThread().setContextClassLoader(classloader);
//		System.err.println("decoding with class loader: "+Thread.currentThread()+", "+Thread.currentThread().getContextClassLoader());
		Object ob = d.readObject();
		d.close();
		return ob;
	}
}

