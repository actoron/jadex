package jadex.bridge.service.library;

import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Library service for loading classpath elements.
 */
public class LibraryService extends BasicService implements ILibraryService
{
	//-------- attributes --------
	
	/** The provider. */
	protected IServiceProvider provider;
	
	/** LibraryService listeners. */
	protected Set listeners;

	/** The initial parent ClassLoader. */
	protected ClassLoader basecl;
	
	/** The urls. */
	protected List urls;

	/** Current ClassLoader. */
	protected ClassLoader	libcl;

	//-------- constructors --------
	
	/** 
	 *  Creates a new LibraryService.
	 */ 
	public LibraryService(IServiceProvider provider)
	{
		this(null, provider);
	}
	
	/** 
	 *  Creates a new LibraryService.
	 *  @param urls	Urls may be specified as java.net.URLs, java.io.Files or java.lang.Strings.
	 *  	Strings are interpreted as relative files (relative to current directory),
	 *  	absolute files or URLs (whatever can be found). 
	 */ 
	public LibraryService(Object[] urls, IServiceProvider provider)
	{
		this(urls, provider, null);
	}
	
	/** 
	 *  Creates a new LibraryService.
	 *  @param urls	Urls may be specified as java.net.URLs, java.io.Files or java.lang.Strings.
	 *  	Strings are interpreted as relative files (relative to current directory),
	 *  	absolute files or URLs (whatever can be found). 
	 */ 
	public LibraryService(Object[] urls, IServiceProvider provider, Map properties)
	{
		// Hack!!! Should not reference gui???
		super(provider.getId(), ILibraryService.class, properties);
		
		this.provider = provider;
		this.basecl = Thread.currentThread().getContextClassLoader();
		this.libcl = basecl;
		
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
//		System.out.println("add "+url);
		
		ILibraryServiceListener[] lis;
		synchronized(this)
		{
			urls.add(url);
			libcl = new URLClassLoader((URL[])urls.toArray(new URL[urls.size()]), basecl);
//			libcl = new LibraryClassLoader((URL[])urls.toArray(new URL[urls.size()]), basecl, provider);
			lis = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
		}
		
		// Do not notify listeners with lock held!
		for(int i=0; i<lis.length; i++)
		{
			final ILibraryServiceListener liscopy = lis[i];
			lis[i].urlAdded(url).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
					removeLibraryServiceListener(liscopy);
				}
			});
		}
		
//		fireURLAdded(url);
	}
	
	/**
	 *  Remove a url.
	 *  @param url The url.
	 */
	public void removeURL(URL url)
	{
		ILibraryServiceListener[] lis;
		synchronized(this)
		{
			if(!urls.remove(url))
				throw new RuntimeException("Unknown URL: "+url);
			
			libcl = new URLClassLoader((URL[])urls.toArray(new URL[urls.size()]), basecl);
//			libcl = new LibraryClassLoader((URL[])urls.toArray(new URL[urls.size()]), basecl, provider);
			lis = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
		}
		
		// Do not notify listeners with lock held!
		for(int i=0; i<lis.length; i++)
		{
			final ILibraryServiceListener liscopy = lis[i];
			lis[i].urlRemoved(url).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
					removeLibraryServiceListener(liscopy);
				}
			});
		}
		
//		fireURLRemoved(url);
	}
	
	/**
	 *  Get all managed entries as URLs.
	 *  @return url The urls.
	 */
	public synchronized IFuture getURLs()
	{
		List ret = new ArrayList();
		ret.addAll(urls);
		return new Future(ret);
	}

	/**
	 *  Get other contained (but not directly managed) URLs.
	 *  @return The list of urls.
	 */
	public synchronized IFuture getNonManagedURLs()
	{
		return new Future(SUtil.getClasspathURLs(libcl));	
	}
	
	/**
	 *  Get all urls (managed and non-managed).
	 *  @return The list of urls.
	 */
	public IFuture getAllURLs()
	{
		List ret = new ArrayList();
		ret.addAll(urls);
		ret.addAll(SUtil.getClasspathURLs(libcl));
		return new Future(ret);
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
	
	/**
	 *  Get the non-managed classpath entries as strings.
	 *  @return Classpath entries as a list of strings.
	 */
	public IFuture getURLStrings()
	{
		final Future ret = new Future();
		
		getURLs().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				List urls = (List)result;
				// TODO Auto-generated method stub
				List tmp = new ArrayList();
				// todo: hack!!!
				
				for(Iterator it=urls.iterator(); it.hasNext(); )
				{
					URL	url	= (URL)it.next();
					tmp.add(url.toString());
					
//					String file = url.getFile();
//					File f = new File(file);
//					
//					// Hack!!! Above code doesnt handle relative url paths. 
//					if(!f.exists())
//					{
//						File	newfile	= new File(new File("."), file);
//						if(newfile.exists())
//						{
//							f	= newfile;
//						}
//					}
//					ret.add(f.getAbsolutePath());
				}
				
				ret.setResult(tmp);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the non-managed classpath entries.
	 *  @return Classpath entries as a list of strings.
	 */
	public IFuture getNonManagedURLStrings()
	{
		final Future ret = new Future();
		
		getNonManagedURLs().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				List urls = (List)result;
				List tmp = new ArrayList();
				// todo: hack!!!
				
				for(Iterator it=urls.iterator(); it.hasNext(); )
				{
					URL	url	= (URL)it.next();
					tmp.add(url.toString());
					
//					String file = url.getFile();
//					File f = new File(file);
//					
//					// Hack!!! Above code doesnt handle relative url paths. 
//					if(!f.exists())
//					{
//						File	newfile	= new File(new File("."), file);
//						if(newfile.exists())
//						{
//							f	= newfile;
//						}
//					}
//					ret.add(f.getAbsolutePath());
				}
				
				ret.setResult(tmp);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
		
//		java.util.List	ret	= new ArrayList();
//
////		ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class);
//		// todo: hack
////		ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class).get(new ThreadSuspendable());
//		ClassLoader	cl	= ls.getClassLoader();
//		
//		List cps = SUtil.getClasspathURLs(cl!=null ? cl.getParent() : null);	// todo: classpath?
//		for(int i=0; i<cps.size(); i++)
//		{
//			URL	url	= (URL)cps.get(i);
//			ret.add(url.toString());
//			
////			String file = url.getFile();
////			File f = new File(file);
////			
////			// Hack!!! Above code doesnt handle relative url paths. 
////			if(!f.exists())
////			{
////				File	newfile	= new File(new File("."), file);
////				if(newfile.exists())
////				{
////					f	= newfile;
////				}
////			}
////			ret.add(f.getAbsolutePath());
//		}
//		
//		return ret;
	}
	
	/**
	 *  Get a class definition.
	 *  @param name The class name.
	 *  @return The class definition as byte array.
	 */
	public IFuture getClassDefinition(String name)
	{
		Future ret = new Future();
		
		Class clazz = SReflect.findClass0(name, null, libcl);
		if(clazz!=null)
		{
			try
			{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(clazz);
				oos.close();
				bos.close();
				byte[] data = bos.toByteArray();
				ret.setResult(data);
			}
			catch(Exception e)
			{
				ret.setResult(null);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
}

