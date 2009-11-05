package com.daimler.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/** This class is based on an example found at
 *  http://forum.java.sun.com/thread.jspa?threadID=300557
 *  
 *  It extends the systems classpath at runtime with a given resource. 
 *  
 * @author cwiech8, antony_miguel
 *
 */

public class ClassPathModifier
{
	 
	private static final Class[] parameters = new Class[]{URL.class};
	 
	public static void addFile(String newResource) throws IOException {
		File f = new File(newResource);
		addFile(f);
	}
	
	public static void addFile(File newResource) throws IOException {
		addURL(newResource.toURL());
	}
	 
	public static void addURL(URL newResource) throws IOException {
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;
	 
		try {
			Method method = sysclass.getDeclaredMethod("addURL",parameters);
			method.setAccessible(true);
			method.invoke(sysloader,new Object[]{ newResource });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}
			
	}
	 
	public static boolean classPathContains(String resource) throws IOException
	{
		File f = new File(resource);
		return classPathContains(f);
	}
	
	public static boolean classPathContains(File resource) throws IOException
	{
		return classPathContains(resource.toURL());
	}
	
	public static boolean classPathContains(URL resource)
	{
		URL[] urls = ((URLClassLoader)ClassLoader.getSystemClassLoader()).getURLs();
		if (urls == null) return false;
		return Arrays.asList(urls).contains(resource);
	}
	
}
