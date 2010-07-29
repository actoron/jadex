package jadex.service.library;

import jadex.commons.IFuture;
import jadex.service.BasicService;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  Library service for loading classpath elements.
 */
public class LibraryService extends BasicService implements ILibraryService
{
	//-------- attributes --------
	
	/** LibraryService listeners. */
	private Set listeners;

	/** The initial parent ClassLoader. */
	private ClassLoader basecl;
	
	/** The urls. */
	private List urls;

	/** Current ClassLoader. */
	private ClassLoader	libcl;

	//-------- constructors --------
	
	/** 
	 *  Creates a new LibraryService.
	 */ 
	public LibraryService(Object providerid)
	{
		this(null, providerid);
	}
	
	/** 
	 *  Creates a new LibraryService.
	 *  @param urls	Urls may be specified as java.net.URLs, java.io.Files or java.lang.Strings.
	 *  	Strings are interpreted as relative files (relative to current directory),
	 *  	absolute files or URLs (whatever can be found). 
	 */ 
	public LibraryService(Object[] urls, Object providerid)
	{
		super(BasicService.createServiceIdentifier(providerid, LibraryService.class));
		
		basecl = Thread.currentThread().getContextClassLoader();
		libcl = basecl;
		
		listeners	= Collections.synchronizedSet(new HashSet());
		synchronized(this)
		{
			this.urls	= new ArrayList();
			if(urls!=null)
			{
				for(int i=0; i<urls.length; i++)
				{
					addURL(toURL(urls[i]));
				}
			}
		}
	}
	
	/**
	 *  Add a path.
	 *  @param path The path.
	 */
	public void addPath(String path)
	{
		addURL(toURL(path));
	}

	//-------- methods --------

	/**
	 *  Add a new url.
	 *  @param url The url.
	 */
	public void addURL(URL url)
	{
		ILibraryServiceListener[] lis;
		synchronized(this)
		{
			urls.add(url);
			libcl = new URLClassLoader((URL[]) urls.toArray(new URL[urls.size()]), basecl);
			lis = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
		}
		
		// Do not notify listeners with lock held!
		for(int i=0; i<lis.length; i++)
		{
			lis[i].urlAdded(url);
		}
		
//		fireURLAdded(url);
	}
	
	/**
	 *  Remove a url.
	 *  @param url The url.
	 */
	public synchronized void removeURL(URL url)
	{
		ILibraryServiceListener[] lis;
		synchronized(this)
		{
			urls.remove(url);
			libcl = new URLClassLoader((URL[]) urls.toArray(new URL[urls.size()]), basecl);
			lis = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
		}
		
		// Do not notify listeners with lock held!
		for(int i=0; i<lis.length; i++)
		{
			lis[i].urlRemoved(url);
		}
		
//		fireURLRemoved(url);
	}
	
	/**
	 *  Get all managed entries as URLs.
	 *  @return url The urls.
	 */
	public synchronized List getURLs()
	{
		List ret = new ArrayList();
		ret.addAll(urls);
		return ret;
	}

	/** 
	 *  Returns the current ClassLoader
	 *  @return the current ClassLoader
	 */
	public ClassLoader getClassLoader()
	{
		return libcl;
	}

	/**
	 *  Start the service.
	 * /
	public synchronized IFuture	startService()
	{
		return super.startService();
	}*/

	/** 
	 *  Shutdown the service.
	 *  Releases all cached resources and shuts down the library service.
	 *  @param listener The listener.
	 */
	public synchronized IFuture	shutdownService()
	{
		basecl = null;
		libcl = null;
		listeners.clear();

		return super.shutdownService();
	}

	/**
	 *  Add an Library Service listener.
	 *  The listener is registered for changes in the loaded library states.
	 *  @param listener The listener to be added.
	 */
	public void addLibraryServiceListener(ILibraryServiceListener listener)
	{
		listeners.add(listener);
	}

	/**
	 *  Remove an Library Service listener.
	 *  @param listener  The listener to be removed.
	 */
	public void removeLibraryServiceListener(ILibraryServiceListener listener)
	{
		listeners.remove(listener);
	}

	/** 
	 *  Helper method for validating jar-files
	 *  @param file the jar-file
	 * /
	private boolean checkJar(File file)
	{
		try
		{
			JarFile jarFile = new JarFile(file);
		}
		catch(IOException e)
		{
			return false;
		}

		return true;
	}*/

	/** 
	 *  Fires the class-path-added event
	 *  @param path the new class path
	 * /
	protected synchronized void fireURLAdded(URL url)
	{
//		System.out.println("listeners: "+listeners);
		for(Iterator it = listeners.iterator(); it.hasNext();)
		{
			ILibraryServiceListener listener = (ILibraryServiceListener)it.next();
			listener.urlAdded(url);
		}
	}*/

	/** 
	 *  Fires the class-path-removed event
	 *  @param path the removed class path
	 * /
	protected synchronized void fireURLRemoved(URL url)
	{
		for(Iterator it = listeners.iterator(); it.hasNext();)
		{
			ILibraryServiceListener listener = (ILibraryServiceListener)it.next();
			listener.urlRemoved(url);
		}
	}*/
	
	/**
	 *  Convert a file/string/url.
	 */
	public static URL toURL(Object url)
	{
		URL	ret	= null;
		if(url instanceof String)
		{
			String	string	= (String) url;
			File	file	= new File(string);
			if(file.exists())
			{
				url	= file;
			}
			else
			{
				file	= new File(System.getProperty("user.dir"), string);
				if(file.exists())
				{
					url	= file;
				}
				else
				{
					try
					{
						url	= new URL(string);
					}
					catch (MalformedURLException e)
					{
						throw new RuntimeException(e);
					}
				}
			}
		}
		
		if(url instanceof URL)
		{
			ret	= (URL)url;
		}
		else if(url instanceof File)
		{
			try
			{
				ret	= ((File)url).toURI().toURL();
			}
			catch (MalformedURLException e)
			{
				throw new RuntimeException(e);
			}
		}
		return ret;
	}
}
