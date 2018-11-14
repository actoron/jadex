package jadex.android.commons;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import jadex.bridge.service.types.library.ISimpleDelegationClassLoader;
import jadex.commons.SUtil.AndroidUtils;
import android.os.Build;
import android.os.Looper;

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
		String dexPathFromJadexLoader = getDexPathFromJadexLoader(classloader);
		if (dexPathFromJadexLoader != null) {
			// JadexDexClassLoader provides dex Path directly, add this
			URL url = urlFromApkPath0(dexPathFromJadexLoader);
			ret.add(url);
		} else if (classloader instanceof ISimpleDelegationClassLoader) {
			// check the delegate
			ClassLoader delegate = (ClassLoader) ((ISimpleDelegationClassLoader) classloader).getDelegate();
			if (delegate != null)
			{
				collectDexPathUrls(delegate, ret);
			}
		} else {
			if ((classloader instanceof DexClassLoader)
					|| classloader instanceof PathClassLoader)
			{
				// we have the main application classloader now
				String string = classloader.toString();
				int begin = string.indexOf('[');
				int end = string.indexOf(']');
				String[] urls = string.substring(begin+1, end).split(":");
				for (int i = 0; i < urls.length; i++)
				{
					String dexPath = urls[i];
					URL url = urlFromApkPath0(dexPath);
					ret.add(url);
				}
			}
		}
		
		// and always go up the hierarchy
		ClassLoader parent = classloader.getParent();
		if (parent != null)
		{
			collectDexPathUrls(parent, ret);
		}
	}

	private String getDexPathFromJadexLoader(ClassLoader classloader)
	{
		String result = null;
		if (classloader instanceof DexClassLoader) {
			if (classloader instanceof JadexDexClassLoader) {
				result = ((JadexDexClassLoader) classloader).getDexPath();
//			} else {
//				try
//				{
//					Method method = classloader.getClass().getMethod("getDexPath");
//					Object path = method.invoke(classloader);
//					result = (String) path;
//					System.out.println("Found JadexDexClassLoader with reflection");
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
			}
		}
		return result;
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
	
	

	@Override
	public String apkPathFromUrl(URL url)
	{
		String path = url.getPath();
		if (path.toLowerCase().endsWith("apk")) {
			return path;
		} else {
			throw new IllegalArgumentException("Not an Android APK ressource.");
		}
	}

	@Override
	public Enumeration<String> getDexEntries(File dexFile) throws IOException
	{
		return new DexFile(dexFile).entries();
	}

	@Override
	public boolean runningOnUiThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}
}
