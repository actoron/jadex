package jadex.extension.envsupport.observer.graphics.opengl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


/**
 * This class loads the native libraries for JOGL
 */
public class JOGLNativeLoader
{
	public static final String	LIBNAMES[]	= {"gluegen-rt", "jogl", "jogl_awt"};

	private static boolean		loaded;

	/**
	 * Loads the appropriate native libraries.
	 */
	public static void loadJOGLLibraries()
	{
		if(loaded)
		{
			return;
		}
		com.sun.gluegen.runtime.NativeLibLoader.disableLoading();
		com.sun.opengl.impl.NativeLibLoader.disableLoading();
		String system = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");

		if(system.equals("Linux"))
		{
			if(arch.equals("i386"))
			{
				loadLinux32();
				loaded = true;
				return;
			}
			if(arch.equals("amd64"))
			{
				loadLinux64();
				loaded = true;
				return;
			}
		}

		if(system.startsWith("Windows"))
		{
			if(arch.equals("x86"))
			{
				loadWindows32();
				loaded = true;
				return;
			}

			if(arch.equals("amd64"))
			{
				loadWindows64();
				loaded = true;
				return;
			}
		}

		if(system.equals("Mac OS X"))
		{
			loadMacOSX();
			loaded = true;
			return;
		}

		if(system.equals("SunOS"))
		{
			if(arch.equals("sparc"))
			{
				loadSolarisSparc();
				loaded = true;
				return;
			}
		}

		loadError();
		return;
	}

	/**
	 * Loads the libraries for 32bit Linux.
	 */
	private static void loadLinux32()
	{
		loadUnix("linux32/");
	}

	/**
	 * Loads the libraries for 64bit Linux.
	 */
	private static void loadLinux64()
	{
		loadUnix("linux64/");
	}

	/**
	 * Loads the libraries for Solaris on 32bit Sparc.
	 */
	private static void loadSolarisSparc()
	{
		loadUnix("solarissparc/");
	}

	/**
	 * Generalized Unix library loader
	 */
	private static void loadUnix(String pathPrefix)
	{
		loadLibraries(pathPrefix, "lib", ".so");
	}

	/**
	 * Loads the libraries for 32bit Windows.
	 */
	private static void loadWindows32()
	{
		loadWindows("windows32/");
	}

	/**
	 * Loads the libraries for 64bit Windows.
	 */
	private static void loadWindows64()
	{
		loadWindows("windows64/");
	}

	/**
	 * Generalized Windows library loader
	 */
	private static void loadWindows(String pathPrefix)
	{
		loadLibraries(pathPrefix, "", ".dll");
	}

	/**
	 * Generalized Mac OS X library loader
	 */
	private static void loadMacOSX()
	{
		String pathPrefix = "macosxuni/";
		loadLibraries(pathPrefix, "lib", ".jnilib");
	}

	private static void loadLibraries(String libPath, String prefix,
			String suffix)
	{
		// Stupid workaround to force Java to load awt-support...
		try
		{
			System.loadLibrary("jawt");
		}
		catch (UnsatisfiedLinkError e)
		{
		}
		
		for(int i = 0; i < LIBNAMES.length; ++i)
		{
			String path = libPath + prefix + LIBNAMES[i] + suffix;
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			String pckg = JOGLNativeLoader.class.getPackage().getName()
					.replaceAll("\\.", "/");
			URL libJarSrc = cl.getResource(pckg + "/nativelibs.jar");
			if(libJarSrc == null)
			{
				loadError();
			}

			InputStream libStream = null;
			try
			{
				JarInputStream jis = new JarInputStream(libJarSrc.openStream());
				JarEntry entry = null;
				do
				{
					entry = jis.getNextJarEntry();
				}
				while(!entry.getName().equals(path));

				libStream = jis;
			}
			catch(Exception e)
			{
				loadError();
			}

			File tmpLib = null;
			try
			{
				String tmpDir = System.getProperty("java.io.tmpdir");
				tmpLib = new File(tmpDir + File.separator + prefix
						+ LIBNAMES[i] + suffix);
				FileOutputStream tmpOut = new FileOutputStream(tmpLib);
				byte[] buf = new byte[4096];
				int len = 0;
				while((len = libStream.read(buf)) != -1)
				{
					tmpOut.write(buf, 0, len);
				}
				libStream.close();
				tmpOut.close();
			}
			catch(IOException e)
			{
			}
		}

		for(int i = 0; i < LIBNAMES.length; ++i)
		{
			String tmpDir = System.getProperty("java.io.tmpdir");
			File libFile = new File(tmpDir + File.separator + prefix
					+ LIBNAMES[i] + suffix);
			System.load(libFile.getAbsolutePath());
		}
	}

	private static void loadError()
	{
		throw new RuntimeException("Failed to identify operating system and"
				+ " load JOGL libraries.");
	}
}
