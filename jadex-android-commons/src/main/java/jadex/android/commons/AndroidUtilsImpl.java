package jadex.android.commons;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import dalvik.system.DexClassLoader;

import jadex.bridge.service.types.library.ISimpleDelegationClassLoader;
import jadex.commons.SUtil.AndroidUtils;
import android.os.Build;

public class AndroidUtilsImpl implements AndroidUtils
{
	/** Cached android version */
	protected Integer androidVersion;

	/**
	 * Get Android API version. Possible values:
	 * http://developer.android.com/reference/android/os/Build.VERSION_CODES.htm
	 * 
	 * @return Android API version
	 */
	public int getAndroidVersion()
	{
		if (androidVersion == null)
		{
			androidVersion = Build.VERSION.SDK_INT;
		}
		return androidVersion.intValue();
	}

	/** Cached flag for xml support */
	protected Boolean hasXmlSupport;

	/**
	 * Checks whether the Platform has the necessary classes to provide XML
	 * encoding and decoding support.
	 * 
	 * @return true, if platform supports xml
	 */
	public boolean hasXmlSupport()
	{
		if (hasXmlSupport == null)
		{
			try
			{
				this.getClass().getClassLoader().loadClass("jadex.xml.reader.XMLReaderFactoryAndroid");
				this.getClass().getClassLoader().loadClass("jadex.xml.writer.XMLWriterFactoryAndroid");
				hasXmlSupport = true;
			}
			catch (ClassNotFoundException e)
			{
				hasXmlSupport = false;
			}
		}
		return hasXmlSupport.booleanValue();
	}

	/**
	 * Looks up the ClassLoader Hierarchy and tries to find a
	 * JadexDexClassLoader in it.
	 * 
	 * @param cl
	 * @return {@link JadexDexClassLoader} or <code>null</code>, if none found.
	 */
	public JadexDexClassLoader findJadexDexClassLoader(ClassLoader cl)
	{
		if (cl instanceof JadexDexClassLoader || cl == null)
		{
			return (JadexDexClassLoader) cl;
		}
		else if (cl instanceof ISimpleDelegationClassLoader)
		{
			ClassLoader delegate = (ClassLoader) ((ISimpleDelegationClassLoader) cl).getDelegate();
			if (delegate != null)
			{
				return findJadexDexClassLoader(delegate);
			}
			else
			{
				return findJadexDexClassLoader(cl.getParent());
			}
		}
		else
		{
			return findJadexDexClassLoader(cl.getParent());
		}
	}

	@Override
	public Collection<? extends URL> collectDexPathUrls(ClassLoader classloader)
	{
		Set<URL> ret = new LinkedHashSet<URL>();
		collectDexPathUrls(classloader, ret);
		return ret;
	}

	private void collectDexPathUrls(ClassLoader classloader, Set<URL> ret)
	{
		JadexDexClassLoader dexCl = findJadexDexClassLoader(classloader);
		if (dexCl != null)
		{

			URL url = urlFromApkPath0(dexCl.getDexPath());
			ret.add(url);
			ClassLoader parent = dexCl.getParent();
			if (parent != null)
			{
				collectDexPathUrls(parent, ret);
				if (parent instanceof DexClassLoader && !(parent instanceof JadexDexClassLoader))
				{
					// we have the main application classloader now
					String string = parent.toString();
					int begin = string.indexOf('[');
					int end = string.indexOf(']');
					String dexPath = string.substring(begin, end);
					System.out.println(dexPath);
				}
			}
		}
	}

	public URL urlFromApkPath0(String apkPath)
	{
		try
		{
			return urlFromApkPath(apkPath);
		}
		catch (MalformedURLException e)
		{
			return null;
		}
	}

	@Override
	public URL urlFromApkPath(String apkPath) throws MalformedURLException
	{
		return new URL("file", "localhost,", apkPath);
	}

}
