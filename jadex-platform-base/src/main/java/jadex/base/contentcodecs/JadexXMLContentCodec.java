package jadex.base.contentcodecs;

import jadex.bridge.IContentCodec;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.io.Serializable;
import java.util.Properties;

/**
 *  Content condec using the Jadex-XML framework.
 */
public class JadexXMLContentCodec implements IContentCodec, Serializable
{
	//-------- constants --------
	
	/** The language identifier. */
	public static final String	JADEX_XML	= "jadex-xml";
	
	/** The debug flag. */
	protected boolean DEBUG = false;
	
	/**
	 *  Test if the codec can be used with the provided meta information.
	 *  @param props The meta information.
	 *  @return True, if it can be used.
	 */
	public boolean match(Properties props)
	{
		return JADEX_XML.equals(props.getProperty("language"));	// Hack!!! avoid dependency to fipa
	}

	/**
	 *  Encode data with the codec.
	 *  @param val The value.
	 *  @return The encoded object.
	 */
	public String encode(Object val, ClassLoader classloader)
	{
		String ret = JavaWriter.objectToXML(val, classloader);
		if(DEBUG)
			System.out.println("encode content: "+ret);
		return ret;
	}

	/**
	 *  Decode data with the codec.
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public Object decode(String val, ClassLoader classloader)
	{
		Object ret = JavaReader.objectFromXML(val, classloader);
		if(DEBUG)
			System.out.println("decode content: "+ret);
		return ret;
	}
}
