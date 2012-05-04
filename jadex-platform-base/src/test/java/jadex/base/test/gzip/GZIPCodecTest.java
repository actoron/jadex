package jadex.base.test.gzip;

import jadex.base.service.message.transport.codecs.GZIPCodec;
import jadex.bridge.service.types.message.ICodec;
import junit.framework.TestCase;

public class GZIPCodecTest extends TestCase
{
	public void	testGZIPCodec()
	{
		ICodec	codec	= new GZIPCodec();
		String	input	= "Hello World!";
		byte[]	encoded	= (byte[])codec.encode(input.getBytes(), null);
		byte[]	decoded	= (byte[])codec.decode(encoded, null);
		String	result	= new String(decoded);
		assertEquals(input, result);
	}
}
