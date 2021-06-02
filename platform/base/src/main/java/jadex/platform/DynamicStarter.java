package jadex.platform;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/** 
 *  Starter class for distributions which dynamically loads the .jar-files
 *  from the lib/ directory.
 */
public class DynamicStarter
{
	public static void main(String[] args) throws Exception
	{
		String jarpath = null;
		File jardir = null;
		
		for(int i=0; i<args.length; i++)
		{
			if(args[i].toLowerCase().equals("-jarpath") && i<args.length)
			{
				jarpath = args[i+1];
				if(jarpath.startsWith("file:///"))
					jarpath = jarpath.substring(8);
				jardir = new File(jarpath);
				break;
			}
		}
		
		// Dynamic reload of lib/ folder if possible.
		Class<?> starter = null;
		Class<?> platformconf = null;
		Object cfg = null;
		
		try
		{
			//String jarpath = URLDecoder.decode(url.getFile(), "UTF-8");
			if(jarpath==null)
			{
				URL url = DynamicStarter.class.getProtectionDomain().getCodeSource().getLocation();
				jarpath = url.getFile(); // URLDecode is not ok because it remove special chars like +
				if(jarpath != null && jarpath.toLowerCase().endsWith(".jar"))
					jardir = (new File(jarpath)).getParentFile();
			}
			
			if(jardir!=null)
			{
				System.out.println("Using jarpath: "+jardir);
				
				List<URL> clurls = new ArrayList<URL>();
				for(File jarfile : jardir.listFiles())
				{
					try
					{
						clurls.add(jarfile.toURI().toURL());
					}
					catch (MalformedURLException e)
					{
					}
				}
				ClassLoader parent = null;
				try
				{
					Method m = ClassLoader.class.getMethod("getPlatformClassLoader");
					parent = (ClassLoader) m.invoke(null);
				}
				catch (Exception e)
				{
				}
				URLClassLoader newcl = new URLClassLoader(clurls.toArray(new URL[clurls.size()]), parent);
				Thread.currentThread().setContextClassLoader(newcl);
				starter = Thread.currentThread().getContextClassLoader().loadClass("jadex.base.Starter");
				platformconf = Thread.currentThread().getContextClassLoader().loadClass("jadex.base.IPlatformConfiguration");
//				cfg = platformconf.getMethod("processArgs", String[].class).invoke(null, ((Object) args));
//				cfg = starter.getMethod("processArgs", String[].class).invoke(null, ((Object) args));
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
			starter.getMethod("createPlatform", platformconf, String[].class).invoke(null, cfg, args);
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
