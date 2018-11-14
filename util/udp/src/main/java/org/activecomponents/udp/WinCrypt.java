package org.activecomponents.udp;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;

public class WinCrypt
{
	public static final String JNA_LIBRARY_NAME = "Advapi32";
	public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(WinCrypt.JNA_LIBRARY_NAME);
	public static final int PROV_RSA_FULL = 1;
	static
	{
		Native.register(WinCrypt.JNA_LIBRARY_NAME);
	}
	
	/** The instance */
	//WinCrypt INSTANCE = (WinCrypt) Native.loadLibrary("Advapi32.dll", WinCrypt.class, W32APIOptions.DEFAULT_OPTIONS);
	
	/** Acquires the crypt context. */
	public static native BOOL CryptAcquireContextW(Pointer phProv, WString pszContainer, WString pszProvider, int dwProvType, int dwFlags);
	/** Releases the crypt context. */
	public static native BOOL CryptReleaseContext(ULONG hProv, int dwFlags);
	/** Generates random data. */
	public static native BOOL CryptGenRandom(ULONG hProv, int dwLen, Pointer pbBuffer);
	
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
			if (CryptAcquireContextW(hProv.getPointer(), null, null, PROV_RSA_FULL, 0).booleanValue())
			{
				Memory buf = new Memory(numBytes);
				if (CryptGenRandom(hProv.getValue(), numBytes, buf).booleanValue())
				{
					CryptReleaseContext(hProv.getValue(), 0);
					ret = buf.getByteArray(0, numBytes);
				}
			}
		}
		catch (Exception e)
		{
		}
		return ret;
	}
	
//	public static void main(String[] args)
//	{
//		System.out.println(System.getProperty("os.version"));
//		String osversion = System.getProperty("os.version");
//		if (Platform.isWindows() && osversion != null && osversion.contains(".") && Integer.parseInt(osversion.substring(0, osversion.indexOf('.'))) > 7)
//		{
//			System.out.println("Yes");
//		}
//		else
//		{
//			System.out.println("No");
//		}
//		ULONGByReference hProv = new WinDef.ULONGByReference();
//		BOOL res = CryptAcquireContextW(hProv.getPointer(), null, null, PROV_RSA_FULL, 0);
//		System.out.println("Acquire: " + res.booleanValue());
//		System.out.println(hProv.getValue().longValue());
//		System.out.println(Kernel32.INSTANCE.GetLastError());
//		Memory buf = new Memory(32);
//		System.out.println("Gen: " + CryptGenRandom(hProv.getValue(), 32, buf).booleanValue());
//		System.out.println(Kernel32.INSTANCE.GetLastError());
//		System.out.println("Release " + CryptReleaseContext(hProv.getValue(), 0).booleanValue());
//		System.out.println(Kernel32.INSTANCE.GetLastError());
//		byte[] rnd = getRandomFromWindows(64);
//		System.out.println(Arrays.toString(rnd));
//	}
}
