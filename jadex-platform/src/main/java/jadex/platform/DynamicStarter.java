package jadex.platform;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/** 
 *  Starter class for distributions which dynamically loads the .jar-files
 *  from the lib/ directory.
 */
public class DynamicStarter
{
	public static void main(String[] args)
	{
		// Dynamic reload of lib/ folder if possible.
		Class<?> starter = null;
		Class<?> platformconf = null;
		Object cfg = null;
		URL url = DynamicStarter.class.getProtectionDomain().getCodeSource().getLocation();
		try
		{
			String jarpath = URLDecoder.decode(url.getFile(), "UTF-8");
			if (jarpath != null && jarpath.toLowerCase().endsWith(".jar"))
			{
				File jardir = (new File(jarpath)).getParentFile();
				System.out.println(jardir);
				
				List<URL> clurls = new ArrayList<URL>();
				for (File jarfile : jardir.listFiles())
				{
					try
					{
						clurls.add(jarfile.toURI().toURL());
					}
					catch (MalformedURLException e)
					{
					}
				}
				URLClassLoader newcl = new URLClassLoader(clurls.toArray(new URL[clurls.size()]), null);
				Thread.currentThread().setContextClassLoader(newcl);
				starter = Thread.currentThread().getContextClassLoader().loadClass("jadex.base.Starter");
				platformconf = Thread.currentThread().getContextClassLoader().loadClass("jadex.base.IPlatformConfiguration");
//				cfg = platformconf.getMethod("processArgs", String[].class).invoke(null, ((Object) args));
				cfg = starter.getMethod("processArgs", String[].class).invoke(null, ((Object) args));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Jadex dynamic load failed.");
			System.exit(1);
		}
		
		try
		{
			starter.getMethod("createPlatform", platformconf).invoke(null, cfg);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
	}
}
