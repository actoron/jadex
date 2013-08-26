package jadex.base.test.gzip;

import jadex.bridge.service.types.message.ICodec;
import jadex.platform.service.message.transport.codecs.GZIPCodec;
import junit.framework.TestCase;

public class GZIPCodecTest extends TestCase
{
	public void	testGZIPCodec()
	{
		ICodec	codec	= new GZIPCodec();
		String	input	= "Hello World!";
		byte[]	encoded	= (byte[])codec.encode(input.getBytes(), null, null);
		byte[]	decoded	= (byte[])codec.decode(encoded, null, null);
		String	result	= new String(decoded);
		assertEquals(input, result);
	}
}
