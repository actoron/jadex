package jadex.base.test.gzip;

import java.nio.charset.Charset;

import jadex.bridge.service.types.message.ICodec;
import jadex.platform.service.message.transport.codecs.GZIPCodec;
import junit.framework.TestCase;

public class GZIPCodecTest extends TestCase
{
	public void	testGZIPCodec()
	{
		ICodec	codec	= new GZIPCodec();
		String	input	= "Hello World!";
		byte[]	encoded	= (byte[])codec.encode(input.getBytes(Charset.forName("UTF-8")), null, null);
		byte[]	decoded	= (byte[])codec.decode(encoded, null, null);
		String	result	= new String(decoded, Charset.forName("UTF-8"));
		assertEquals(input, result);
	}
}
