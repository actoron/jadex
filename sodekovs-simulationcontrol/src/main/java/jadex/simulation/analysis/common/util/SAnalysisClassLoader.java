package jadex.simulation.analysis.common.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Static class to create a Classloader which adds the \\sodekovs-simulationcontrol\\target\\classes\\
 * as a url
 * 
 * @author 5haubeck
 */
public class SAnalysisClassLoader
{
	private static ClassLoader classloader;
	
	public static ClassLoader getClassLoader()
	{
		if (classloader != null)
		{
			try
			{
				URL url = new URL(new File("..").getCanonicalPath() + "\\sodekovs-simulationcontrol\\target\\classes\\");
				URL[] urls = {url};
				classloader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return classloader;
	}

}
