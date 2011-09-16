package jadex.base;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 *  Starter-Adapter needed because Java is too stupid
 *  to maintain a proper classpath documentation
 *  (classpaths from manifest-files are "silently"
 *   loaded and not documented). Grr... 
 */
public class ManifestStarter
{
	
	public static void main(String[] args)
	{
		URLClassLoader initialcl = ((URLClassLoader) ManifestStarter.class.getClassLoader());
		URL[] urls = initialcl.getURLs();
		File initialjarfile = null;
		for (int i = 0; i < urls.length; ++i)
		{
			if (urls[i].getPath().contains("jadex-platform-standalone-launch"))
			{
				String urlPath = urls[i].getPath();
				
				initialjarfile = new File(urlPath);
				if (!initialjarfile.exists())
				{
					System.err.println("Start .jar-file not found: " + urlPath);
					System.exit(1);
				}
				break;
			}
		}
		
		Set manifestclasspath = new LinkedHashSet();

        try 
        {
            JarFile jarfile = new JarFile(initialjarfile);

            final Manifest manifest = jarfile.getManifest();
            if (manifest != null)
            {
                final String classpath = manifest.getMainAttributes().getValue(new Attributes.Name("Class-Path"));
                if (classpath != null)
                {
                	StringTokenizer tok = new StringTokenizer(classpath, " ");
                	try
                	{
                		while (tok.hasMoreElements())
                		{
                			String path = tok.nextToken();
                			File urlfile = new File(path);
                			if (urlfile.exists())
                			{
                				URL url = urlfile.toURI().toURL();
                				manifestclasspath.add(url);
                			}
                		}
                	}
                	catch (Exception e)
                	{
                		e.printStackTrace();
                	}
                }
            }
        }
        catch (IOException e)
        {
        	System.err.println("Failed to load start .jar-file: " + initialjarfile);
			System.exit(1);
        }
        
        
        // addUrl is protected - thanks a lot.
		try
		{
//			System.out.println("Manifest Classpath: "+manifestclasspath);
			Method addurl = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
			addurl.setAccessible(true);
			for (Iterator it = manifestclasspath.iterator(); it.hasNext(); )
				addurl.invoke(initialcl, it.next());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

        Starter.main(args);
	}
}
