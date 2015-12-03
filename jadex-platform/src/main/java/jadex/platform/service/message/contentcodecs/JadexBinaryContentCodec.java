package jadex.platform.service.message.contentcodecs;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jadex.bridge.service.types.message.IContentCodec;
import jadex.bridge.service.types.message.IEncodingContext;
import jadex.commons.transformation.binaryserializer.BinarySerializer;
import jadex.commons.transformation.binaryserializer.IDecoderHandler;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.platform.service.message.transport.codecs.JadexBinaryCodec;

/**
 *  Content codec using the Jadex binary framework.
 */
public class JadexBinaryContentCodec implements IContentCodec, Serializable
{
	//-------- constants --------
	
	/** The language identifier. */
	public static final String	JADEX_BINARY	= "jadex-binary";
	
	/** The debug flag. */
	protected boolean DEBUG = false;
	
	/**
	 *  Test if the codec can be used with the provided meta information.
	 *  @param props The meta information.
	 *  @return True, if it can be used.
	 */
	public boolean match(Properties props)
	{
		return JADEX_BINARY.equals(props.getProperty("language"));	// Hack!!! avoid dependency to fipa
	}

	/**
	 *  Encode data with the codec.
	 *  @param val The value.
	 *  @return The encoded object.
	 */
	public byte[] encode(Object val, ClassLoader classloader, Map<Class<?>, Object[]> info, IEncodingContext context)
	{
		Object[] infos = info==null? null: info.get(getClass());
		List<ITraverseProcessor> preprocessors = (List<ITraverseProcessor>)(infos!=null? infos[1]: null);
		byte[] ret = BinarySerializer.objectToByteArray(val, preprocessors, JadexBinaryCodec.getEncoderChain(context), null, classloader);
		if(DEBUG)
		{
			try
			{
				System.out.println("encode content: "+new String(ret, "UTF-8"));
			}
			catch (UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
		}
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
		List<IDecoderHandler> postprocessors = (List<IDecoderHandler>)(infos!=null? infos[0]: null);
		Object ret = BinarySerializer.objectFromByteArray(val, postprocessors, null, classloader, rep);
		if(DEBUG)
			System.out.println("decode content: "+ret);
		return ret;
	}
}
