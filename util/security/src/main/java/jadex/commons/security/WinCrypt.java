package jadex.commons.security;

import com.sun.jna.Function;
import com.sun.jna.Memory;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;

import jadex.commons.SUtil;

/**
 *  Access to windows cryptography API.
 *
 */
public class WinCrypt
{
	/** Library name used to access the API */
	public static final String WIN_LIB_NAME = "Advapi32";
	
	/** Default provider to use. */
	public static final int PROV_RSA_FULL = 1;
	
	/**
	 *  Gets random numbers from Windows API.
	 *  @param numBytes Number of bytes requested.
	 *  @return Random data, null on failure.
	 */
	public static byte[] getRandomFromWindows(int numBytes)
	{
		byte[] ret = null;
		try
		{
			ULONGByReference hProv = new WinDef.ULONGByReference();
			System.out.println("Trying Wincrypt 0");
			if (CryptAcquireContextW(hProv.getPointer(), null, null, PROV_RSA_FULL, 0))
			{
				System.out.println("Trying Wincrypt 1");
				Memory buf = new Memory(numBytes);
				if (CryptGenRandom(hProv.getValue(), numBytes, buf))
				{
					System.out.println("Trying Wincrypt 2");
					CryptReleaseContext(hProv.getValue(), 0);
					System.out.println("Trying Wincrypt 3");
					ret = buf.getByteArray(0, numBytes);
				}
			}
		}
		catch (Throwable e)
		{
			SUtil.throwUnchecked(e);
		}
		return ret;
	}
	
	/** Acquires the crypt context. */
	private static boolean CryptAcquireContextW(Pointer phProv, WString pszContainer, WString pszProvider, int dwProvType, int dwFlags)
	{
		Function f = NativeLibrary.getInstance(WIN_LIB_NAME).getFunction("CryptAcquireContextW");
		return f.invokeInt(new Object[] { phProv, pszContainer, pszProvider, dwProvType, dwFlags }) != 0;
	}
	
	/** Releases the crypt context. */
	private static boolean CryptReleaseContext(ULONG hProv, int dwFlags)
	{
		Function f = NativeLibrary.getInstance(WIN_LIB_NAME).getFunction("CryptReleaseContext");
		return f.invokeInt(new Object[] { hProv, dwFlags }) != 0;
	}
	
	/** Generates random data. */
	private static boolean CryptGenRandom(ULONG hProv, int dwLen, Pointer pbBuffer)
	{
		Function f = NativeLibrary.getInstance(WIN_LIB_NAME).getFunction("CryptGenRandom");
		return f.invokeInt(new Object[] { hProv, dwLen, pbBuffer }) != 0;
	}
}
