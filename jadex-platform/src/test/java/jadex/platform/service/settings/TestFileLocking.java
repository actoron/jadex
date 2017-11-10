package jadex.platform.service.settings;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Assert;
import org.junit.Before;

/**
 *  Check that file locking works as expected for thread safe settings writing.
 */
public class TestFileLocking
{
	private static final String TESTFILE = "testfile.txt";

	@Before
	public void setup()
	{
		new File(TESTFILE).delete();
	}

//	@Test
	public void test() throws IOException
	{
		RandomAccessFile	raf1=null, raf2;

		// First read access should fail.
		try
		{
			raf1 = new RandomAccessFile(TESTFILE, "r");
			Assert.fail("First read access succeeded: "+raf1);
		}
		catch(Exception e)
		{
		}
		
		// First write access should be ok.
		try
		{
			raf1 = new RandomAccessFile(TESTFILE, "rws");
		}
		catch(Exception e)
		{
			Assert.fail("First access failed: "+e);
		}
		
		// Concurrent write access should fail.
		try
		{
			raf2 = new RandomAccessFile(TESTFILE, "rws");
			Assert.fail("Concurrent write access succeeded: "+raf2);
		}
		catch(Exception e)
		{
		}

		raf1.writeBytes("Hallo\n");
		
		// Concurrent read access should fail.
		try
		{
			raf2 = new RandomAccessFile(TESTFILE, "r");
			Assert.fail("Concurrent read access succeeded: "+raf2);
		}
		catch(Exception e)
		{
		}
		
		raf1.close();

		// Sequential read access should succeed.
		try
		{
			raf2 = new RandomAccessFile(TESTFILE, "r");
			raf2.close();
		}
		catch(Exception e)
		{
			Assert.fail("Sequential read access failed: "+e);
		}
	}
}
