package jadex.commons.security;

import com.sun.jna.Function;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

/**
 *  Access to cryptographically secure entropy on some UNIX systems (BSD, Linux).
 *
 */
public class UnixEntropyApi
{
	
	/**
	 *  Gets random numbers from UNIX syscall.
	 *  @param numBytes Number of bytes requested.
	 *  @return Random data, null on failure.
	 */
	public static byte[] getEntropy(int numbytes)
	{
		Function getentropy = NativeLibrary.getInstance("c").getFunction("getentropy");
		Memory buf = new Memory(numbytes);
		int read = 0;
		while (read < numbytes)
		{
			int reading = Math.min(256, numbytes - read);
			int res = getentropy.invokeInt(new Object[] { buf.share(read), reading });
			if (res != 0)
				return null;
			read += reading;
		}
		return buf.getByteArray(0, numbytes);
	}
}
