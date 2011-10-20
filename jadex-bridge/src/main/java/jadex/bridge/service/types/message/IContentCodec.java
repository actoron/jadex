package jadex.bridge.service.types.message;

import java.util.Properties;

/**
 *  The interface for content codecs. 
 */
public interface IContentCodec
{
	/**
	 *  Test if the codec can be used with the provided meta information.
	 *  @param props The meta information.
	 *  @return True, if it can be used.
	 */
	public boolean match(Properties props);

	/**
	 *  Encode data with the codec.
	 *  @param val The value.
	 *  @return The encoded object.
	 */
//	public String encode(Object val, ClassLoader classloader);
	public byte[] encode(Object val, ClassLoader classloader);

	/**
	 *  Decode data with the codec.
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
//	public Object decode(String val, ClassLoader classloader);
	public Object decode(byte[] val, ClassLoader classloader);
}
