package jadex.bdi.testcases.misc;

import jadex.bridge.service.types.message.IContentCodec;
import jadex.bridge.service.types.message.IEncodingContext;
import jadex.commons.transformation.binaryserializer.IErrorReporter;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

/**
 *  Simple test codec.
 */
public class TestCodec implements IContentCodec, Serializable
{
	//-------- constants --------
	
	/** The language identifier. */
	public static final String	TEST_LANGUAGE	= "test-language";
	
	//-------- methods --------
	
	/**
	 *  Test if the codec can be used with the provided meta information.
	 *  @param props The meta information.
	 *  @return True, if it can be used.
	 */
	public boolean match(Properties props)
	{
		return TEST_LANGUAGE.equals(props.getProperty("language"));	// Hack!!! avoid dependency to fipa
	}

	/**
	 *  Encode data with the codec.
	 *  @param val The value.
	 *  @return The encoded object.
	 */
	public byte[] encode(Object val, ClassLoader classloader, Map<Class<?>, Object[]> info, IEncodingContext context)
	{
		return "97".getBytes();
	}

	/**
	 *  Decode data with the codec.
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public Object decode(byte[] val, ClassLoader classloader, Map<Class<?>, Object[]> info, IErrorReporter rep)
	{
		return "97".equals(new String(val, Charset.forName("UTF-8"))) ? new Integer(98) : new Integer(96);
//		try
//		{
//			return new Integer(val);
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException("Decode error, no integer: "+val);
//		}
	}
}