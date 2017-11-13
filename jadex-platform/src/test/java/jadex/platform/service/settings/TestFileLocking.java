package jadex.platform.service.settings;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *  Check that file locking works as expected for thread safe settings writing.
 */
// http://www.linuxtopia.org/online_books/programming_books/thinking_in_java/TIJ314_030.htm
// http://javabeat.net/locking-files-using-java/
public class TestFileLocking
{
	private static final File TESTFILE = new File("testfile.txt");

	@Before
	public void setup()
	{
		TESTFILE.delete();
	}

	@Test
	public void test() throws IOException
	{
		RandomAccessFile	raf	= null;
		FileLock	lock	= null;
		
		// First access should fail.
		assertFalse(TESTFILE+" should not exist", TESTFILE.exists());
		
		// First write access should be ok.
		try
		{
			raf	= new RandomAccessFile(TESTFILE, "rw");
			lock	= raf.getChannel().lock();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("First access failed: "+e);
		}
		
		// Concurrent write access should fail.
		try
		{
			RandomAccessFile	raf2	= new RandomAccessFile(TESTFILE, "rw");
			lock	= raf2.getChannel().lock();
			Assert.fail("Concurrent write access succeeded: "+raf2);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}

		raf.write("Hallo\n".getBytes());
		
		// Concurrent read/write access with lock should fail.
		try
		{
			RandomAccessFile	raf2	= new RandomAccessFile(TESTFILE, "rw");
			lock	= raf.getChannel().lock();
			raf2.read();
			raf2.close();
			lock.release();
			Assert.fail("Concurrent read access succeeded: "+raf2);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
		
		// Concurrent read access without lock doesn't fail on jenkins -> use lock for read, too!
//		try
//		{
//			RandomAccessFile	raf2	= new RandomAccessFile(TESTFILE, "r");
//			raf2.read();
//			raf2.close();
//			Assert.fail("Concurrent read access succeeded: "+raf2);
//		}
//		catch(Exception e)
//		{
////			e.printStackTrace();
//		}
		
		// Todo: access from different vm?
		
		lock.release();
		raf.close();

		// Sequential read access should succeed.
		try
		{
			RandomAccessFile	raf2	= new RandomAccessFile(TESTFILE, "r");
			System.out.println(raf2.readLine());
			raf2.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Sequential read access failed: "+e);
		}
	}
}
