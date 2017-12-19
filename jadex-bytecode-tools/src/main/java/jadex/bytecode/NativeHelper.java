package jadex.bytecode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

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
	// Initialize native code.
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
			System.out.println("Loading " + libname+libsuffix);
			
			String libres = "nativelibs/" + libname + libsuffix;
			InputStream is = NativeHelper.class.getClassLoader().getResourceAsStream(libres);
			File libfile = File.createTempFile(libname, libsuffix);
			FileOutputStream os = new FileOutputStream(libfile);
			SUtil.copyStream(is, os);
			is.close();
			os.close();
			System.load(libfile.getAbsolutePath());
		}
		catch (Throwable t)
		{
//			t.printStackTrace();
			SUtil.throwUnchecked(t);
		}
	}
	
	/** Name of the AccessibleObject override flag. */
	protected static final String OVERRIDE;
	static
	{
		// Sometimes called "flag"?
		String flagname = "flag";
		try
		{
			Field f = AccessibleObject.class.getDeclaredField("override");
			flagname = "override";
		}
		catch (Exception e)
		{
		}
		OVERRIDE = flagname;
	}
	
	/**
     * Define a class in any ClassLoader.
     */
	protected static final Class<?> defineClass(String name, byte[] b, ClassLoader loader)
	{
		return defineClass(name, b, b.length, loader);
	}
	
	/**
	 *  Sets reflective object accessible without checks.
	 *  
	 *  @param accobj The accessible object.
	 *  @param flag The flag value.
	 */
	protected static final void setAccessible(AccessibleObject accobj, boolean flag)
	{
		setAccessible(OVERRIDE, accobj, flag);
	}
	
	/**
	 *  Sets reflective object accessible without checks.
	 *  
	 *  @param accobj The accessible object.
	 *  @param flag The flag value.
	 */
	private static final native void setAccessible(String flagname, AccessibleObject accobj, boolean flag);
	
	/**
     * Define a class in any ClassLoader.
     */
	private static final native Class<?> defineClass(String name, byte[] b, int len, ClassLoader loader);
}
