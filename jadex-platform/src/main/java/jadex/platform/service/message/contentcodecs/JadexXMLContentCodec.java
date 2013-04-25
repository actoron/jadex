package jadex.platform.service.message.contentcodecs;

import jadex.bridge.service.types.message.IContentCodec;
import jadex.commons.Tuple2;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;
import jadex.xml.reader.IObjectReaderHandler;
import jadex.xml.writer.IObjectWriterHandler;

import java.io.Serializable;
import java.util.Map;
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
	public byte[] encode(Object val, ClassLoader classloader, Map<Class<?>, Object[]> info)
	{
		Object[] infos = info==null? null: info.get(getClass());
		IObjectWriterHandler handler = (IObjectWriterHandler)(infos!=null? infos[1]: null);
		byte[] ret = JavaWriter.objectToByteArray(val, classloader, handler);
		if(DEBUG)
			System.out.println("encode content: "+ret);
		return ret;
	}

	/**
	 *  Decode data with the codec.
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public Object decode(byte[] val, ClassLoader classloader, Map<Class<?>, Object[]> info, IErrorReporter rep)
	{
		Object[] infos = info==null? null: info.get(getClass());
		Tuple2<TypeInfoPathManager, IObjectReaderHandler>  tup = (Tuple2<TypeInfoPathManager, IObjectReaderHandler>)(infos!=null? infos[0]: null);
		Object ret = JavaReader.objectFromByteArray(val, classloader, tup==null? null: tup.getFirstEntity(), tup==null? null: tup.getSecondEntity(), rep);
		if(DEBUG)
			System.out.println("decode content: "+ret);
		return ret;
	}
}
