package jadex.commons;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

import org.junit.Assert;
import org.junit.Test;

/**
 *  Some SUtil methods
 */
public class SUtilTest //extends TestCase
{
	@Test
	public void testLongConversion()
	{
		long[]	totest	= new long[]
		{
			0, 1, -1, 255, 256,
			Long.MAX_VALUE, Long.MIN_VALUE,
			Byte.MIN_VALUE, Byte.MIN_VALUE-1,
			Byte.MAX_VALUE, Byte.MAX_VALUE+1,
			Short.MIN_VALUE, Short.MIN_VALUE-1,
			Short.MAX_VALUE, Short.MAX_VALUE+1,
			Integer.MIN_VALUE, Integer.MIN_VALUE-1L,
			Integer.MAX_VALUE, Integer.MAX_VALUE+1L
		};
		
		for(int i=0; i<totest.length; i++)
		{
			byte[]	ba	= SUtil.longToBytes(totest[i]);
			long	val	= SUtil.bytesToLong(ba);
			Assert.assertEquals("Array "+i+": "+SUtil.arrayToString(ba), totest[i], val);
		}
	}
	
	@Test
	public void	testFileZippingAndHashing() throws Exception
	{
		// for maven?:
		File	src	= new File("../jadex-commons/src/main/java");
		if (!src.exists()) {
			// for gradle:
			src = new File("jadex-commons/src/main/java");
		}

		System.out.println(src.getAbsolutePath());
		File dest = File.createTempFile("jadextemp", ".jar");
//		File	dest	= new File("temp", "test.jar");
//		System.out.println("zipping: "+dest.getAbsolutePath());
		dest.getParentFile().mkdirs();
		
		FileOutputStream	fos	= new FileOutputStream(dest);
		SUtil.writeDirectory(src, new BufferedOutputStream(fos));
		fos.close();
		
		Assert.assertEquals(SUtil.getHashCode(src, false), SUtil.getHashCode(dest, false));
		
		SUtil.deleteDirectory(dest.getParentFile());
	}

	@Test
	public void toUri_correctFormat() {
		URI uri = SUtil.toURI("tcp-mtp://[fe80:0:0:0:8cf:5aff:feeb:f199%eth0]:42716");
		Assert.assertEquals("tcp-mtp", uri.getScheme());
		Assert.assertEquals("[fe80:0:0:0:8cf:5aff:feeb:f199%eth0]", uri.getHost());
		Assert.assertEquals(42716, uri.getPort());
	}

	@Test
	public void toUri_correctFormat2() {
		URI uri = SUtil.toURI("tcp-mtp://[fe80:0:0:0:8cf:5aff:feeb:f199]:42716");
		Assert.assertEquals("tcp-mtp", uri.getScheme());
		Assert.assertEquals("[fe80:0:0:0:8cf:5aff:feeb:f199]", uri.getHost());
		Assert.assertEquals(42716, uri.getPort());
	}

	@Test
	public void toUri_wrongButSupportedFormat() {
		URI uri = SUtil.toURI("tcp-mtp://fe80:0:0:0:8cf:5aff:feeb:f199%eth0:42716");
		Assert.assertEquals("tcp-mtp", uri.getScheme());
		Assert.assertEquals("[fe80:0:0:0:8cf:5aff:feeb:f199%eth0]", uri.getHost());
		Assert.assertEquals(42716, uri.getPort());
	}

//	@Test
//	public void toUri_wrongButSupportedFormat2() {
//		URI uri = SUtil.toURI("tcp-mtp://fe80:0:0:0:8cf:5aff:feeb:f199:42716");
//		Assert.assertEquals("tcp-mtp", uri.getScheme());
//		Assert.assertEquals("[fe80:0:0:0:8cf:5aff:feeb:f199]", uri.getHost());
//		Assert.assertEquals(42716, uri.getPort());
//	}

	@Test
	public void toUri_wrongUnsupportedFormat() {
		try {
			URI uri = SUtil.toURI("tcp-mtp://fe80:0:0:0:8cf:5aff:feeb:f199:42716");
			org.junit.Assert.fail("should have thrown");
		} catch (Exception e) {
		}
	}
}
