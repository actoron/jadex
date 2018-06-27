package jadex.platform.service.library;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 */
public class ChangeableURLClassLoader extends URLClassLoader
{
	/** The url list. */
	protected List<URL> urls;

	/**
	 * 	Create a new classloader.
	 */
	public ChangeableURLClassLoader(URL[] urls)
	{
		this(urls, null);
	}
	
	/**
	 * 	Create a new classloader.
	 */
	public ChangeableURLClassLoader(URL[] urls, ClassLoader parent)
	{
		super(urls==null? new URL[0]: urls, parent);
		this.urls = Collections.synchronizedList(new ArrayList<URL>());
	}

	/**
	 *  Add a url.
	 *  @param url The url.
	 */
	public void addURL(URL url) 
	{
		urls.add(url);
		super.addURL(url);
	}
	
	/**
	 *  Remove a url.
	 *  @param url The url.
	 */
	public void removeURL(URL url)
	{
		urls.remove(url);
	}

	/**
	 *  Get the urls.
	 *  @return The urls.
	 */
	public URL[] getURLs()
	{
		return (URL[])urls.toArray(new URL[urls.size()]);
	}
	
	/**
	 * 
	 */
	public String toString()
	{
		String ret = super.toString();
		ClassLoader pa = getParent();
		while(pa!=null)
		{
			ret += " "+pa.toString();
			pa = pa.getParent();
		}
		return ret;
	}
}
