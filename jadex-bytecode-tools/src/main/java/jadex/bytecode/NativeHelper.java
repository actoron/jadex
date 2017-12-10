package jadex.bytecode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.ProtectionDomain;

import jadex.commons.SUtil;

/**
 *  Class with native helper method to lift JVM restrictions.
 *	
 *	Various methods are used to get around restrictions, this is
 *  just one of them. Therefore, do not use this class directly,
 *  use SASM.UNSAFE.
 */
public class NativeHelper
{
	static
	{
		try
		{
			String archstr = "64";
			String osarch = System.getProperty("os.arch");
			if ("x86".equals(osarch))
				archstr = "32";
			
			String libmainname = "nativehelper";
			String osname = System.getProperty("os.name");
			String libname = null;
			String libsuffix = null;
			if ("Linux".equals(osname))
			{
				libsuffix = ".so";
				libname = "lib" + libmainname + archstr;
			}
			else if ("Windows".equals(osname))
			{
				libsuffix = ".dll";
				libname = libmainname + archstr;
			}
			
			String libres = "nativelibs/" + libname + libsuffix;
			InputStream is = NativeHelper.class.getClassLoader().getResourceAsStream(libres);
			File libfile = File.createTempFile(libname, libsuffix);
			FileOutputStream os = new FileOutputStream(libfile);
			SUtil.copyStream(is, os);
			is.close();
			os.close();
			System.load(libfile.getAbsolutePath());
		}
		catch (Exception e)
		{
			SUtil.throwUnchecked(e);
		}
	}
	
	/**
     * Define a class in any ClassLoader.
     */
	public Class<?> defineClass(String name, byte[] b, ClassLoader loader)
	{
		return defineClass(name, b, b.length, loader);
	}
	
	/**
     * Define a class in any ClassLoader.
     */
	private static final native Class<?> defineClass(String name, byte[] b, int len, ClassLoader loader);
}
