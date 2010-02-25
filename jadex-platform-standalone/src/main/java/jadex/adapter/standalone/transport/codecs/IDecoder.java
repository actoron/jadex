package jadex.adapter.standalone.transport.codecs;

/**
 *  Decode an object from a string representation.
 */
public interface IDecoder
{
	/**
	 *  Decode data with the codec.
	 *  @param bytes The value bytes.
	 *  @return The encoded object.
	 */
	public Object decode(byte[] bytes, ClassLoader classloader);
}
