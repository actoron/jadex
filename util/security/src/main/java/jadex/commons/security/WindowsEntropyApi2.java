package jadex.commons.security;

import com.sun.jna.Function;
import com.sun.jna.Memory;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.ULONG;

import jadex.commons.SUtil;

/**
 *  Access to windows cryptographically secure entropy.
 *
 */
public class WindowsEntropyApi2
{
	/** Library name used to access the API */
	public static final String WIN_LIB_NAME = "Bcrypt";
	
	/** Default provider to use. */
	public static final int PROV_RSA_FULL = 1;
	
	/**
	 *  Gets random numbers from Windows API.
	 *  @param numBytes Number of bytes requested.
	 *  @return Random data, null on failure.
	 */
	public static byte[] getEntropy(int numbytes)
	{
		byte[] ret = null;
		try
		{
			Memory buf = new Memory(numbytes);
			if (BCryptGenRandom(buf, new ULONG(numbytes)))
			{
				ret = buf.getByteArray(0, numbytes);
			}
		}
		catch (Throwable e)
		{
			SUtil.throwUnchecked(e);
		}
		return ret;
	}
	
	/** Generates random data. */
	private static boolean BCryptGenRandom(Pointer pbBuffer, ULONG cbBuffer)
	{
		ULONG dwFlags = new ULONG(2);
		Function f = NativeLibrary.getInstance(WIN_LIB_NAME).getFunction("BCryptGenRandom");
		boolean ret = f.invokeInt(new Object[] { null, pbBuffer, cbBuffer, dwFlags }) != 0;
		return ret;
	}
}
