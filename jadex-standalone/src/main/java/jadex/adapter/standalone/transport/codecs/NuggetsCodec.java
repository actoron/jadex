package jadex.adapter.standalone.transport.codecs;

import java.io.IOException;

import nuggets.Nuggets;

/**
 *  The Nuggets XML codec. Codec supports parallel
 *  calls of multiple concurrent clients (no method
 *  synchronization necessary).
 */
public class NuggetsCodec implements IEncoder, IDecoder
{
	//-------- constants --------
	
	/** The nuggets codec id. */
	public static final byte CODEC_ID = 1;

	//-------- methods --------
	
	/**
	 *  Encode an object.
	 *  @param obj The object.
	 *  @throws IOException
	 */
	public byte[] encode(Object val)
	{
		return Nuggets.objectToXML(val).getBytes();
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(byte[] bytes)
	{
		return Nuggets.objectFromXML(new String(bytes));
	}
}
