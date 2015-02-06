package jadex.commons;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

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
//		File	src	= new File("../jadex-applications-micro/target/classes");
		File	src1	= new File("../jadex-commons/target/classes");	// maven
		File	src2	= new File("../jadex-commons/build/classes/main");	// gradle
		File	src	= src1.exists() ? src1 : src2;
		File	dest	= new File("temp", "test.jar");
		dest.getParentFile().mkdirs();
		
		FileOutputStream	fos	= new FileOutputStream(dest);
		SUtil.writeDirectory(src, new BufferedOutputStream(fos));
		fos.close();
		
		Assert.assertEquals(SUtil.getHashCode(src), SUtil.getHashCode(dest));
		
		SUtil.deleteDirectory(dest.getParentFile());
	}
}
