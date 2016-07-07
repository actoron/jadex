package jadex.base.test.gzip;

import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import jadex.bridge.service.types.message.IBinaryCodec;
import jadex.platform.service.message.transport.codecs.GZIPCodec;

/**
 *  Test gzip.
 */
public class GZIPCodecTest //extends TestCase
{
	@Test
	public void	testGZIPCodec()
	{
		IBinaryCodec	codec	= new GZIPCodec();
		String	input	= "Hello World!";
		byte[]	encoded	= (byte[])codec.encode(input.getBytes(Charset.forName("UTF-8")));
		byte[]	decoded	= (byte[])codec.decode(encoded);
		String	result	= new String(decoded, Charset.forName("UTF-8"));
		Assert.assertEquals(input, result);
	}
}
