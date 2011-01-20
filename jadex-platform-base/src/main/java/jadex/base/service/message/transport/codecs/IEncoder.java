package jadex.base.service.message.transport.codecs;

/**
 *  Encode an object to a string representation.
 */
public interface IEncoder
{
	/**
	 *  Encode data with the codec.
	 *  @param val The value.
	 *  @return The encoded object.
	 */
	public byte[] encode(Object val, ClassLoader classloader);
}
