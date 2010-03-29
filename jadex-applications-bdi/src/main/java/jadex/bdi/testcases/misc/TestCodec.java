package jadex.bdi.testcases.misc;

import java.io.Serializable;
import java.util.Properties;

import jadex.bridge.IContentCodec;

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
	public String encode(Object val, ClassLoader classloader)
	{
		return ""+val;
	}

	/**
	 *  Decode data with the codec.
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public Object decode(String val, ClassLoader classloader)
	{
		return new Integer(98);
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