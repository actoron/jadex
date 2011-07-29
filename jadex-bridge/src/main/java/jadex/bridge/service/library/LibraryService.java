package jadex.bridge.service.library;

import jadex.bridge.ISettingsService;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Library service for loading classpath elements.
 */
public class LibraryService extends BasicService implements ILibraryService, IPropertiesProvider
{
	//-------- attributes --------
	
	/** The provider. */
	protected IServiceProvider provider;
	
	/** LibraryService listeners. */
	protected Set listeners;

	/** The initial parent ClassLoader. */
	//protected ClassLoader basecl;
	
	/** The urls. */
	//protected List urls;

	/** Current ClassLoader. */
	protected DelegationClassLoader	libcl;
	
	/** URL reference count */
	protected Map urlrefcount;

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
		super(provider.getId(), ILibraryService.class, properties);
		
		this.provider = provider;
		/* $if !android $ */
		this.libcl = new DelegationClassLoader(ClassLoader.getSystemClassLoader(), urls);
		/* $else $
		this.libcl = new DelegationClassLoader(LibraryService.class.getClassLoader(), urls);
		$endif $ */
		
		listeners	= Collections.synchronizedSet(new HashSet());
		urlrefcount = Collections.synchronizedMap(new HashMap());
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
		ILibraryServiceListener[] lis = null;
		synchronized(this)
		{
			Integer refcount = (Integer) urlrefcount.get(url);
			if(refcount != null)
			{
				urlrefcount.put(url, new Integer(refcount.intValue() + 1));
			}
			else
			{
				urlrefcount.put(url, new Integer(1));
				Map delegates = libcl.getDelegates();
				delegates.put(url, new URLClassLoader(new URL[] {url}));
				libcl = new DelegationClassLoader(ClassLoader.getSystemClassLoader(), delegates);
				lis = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
			}
		}
		
		// Do not notify listeners with lock held!
		if(lis != null)
		{
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
		}
	}
	
	/**
	 *  Remove a url.
	 *  @param url The url.
	 */
	public void removeURL(URL url)
	{
		ILibraryServiceListener[] lis = null;
		synchronized(this)
		{
			Integer refcount = (Integer)urlrefcount.get(url);
			if(refcount == null)
				throw new RuntimeException("Unknown URL: "+url);
			refcount = new Integer(refcount.intValue() - 1);
			urlrefcount.put(url, refcount);
			if(refcount.intValue() < 1)
			{
				Map delegates = libcl.getDelegates();
				delegates.remove(url);
				libcl = new DelegationClassLoader(ClassLoader.getSystemClassLoader(), delegates);
				lis = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
			}
		}
		
		// Do not notify listeners with lock held!
		if(lis != null)
		{
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
		}
	}
	
	/**
	 *  Remove a url completely (all references).
	 *  @param url The url.
	 */
	public void removeURLCompletely(URL url)
	{
		ILibraryServiceListener[] lis = null;
		synchronized(this)
		{
			urlrefcount.remove(url);
			Map delegates = libcl.getDelegates();
			delegates.remove(url);
			libcl = new DelegationClassLoader(ClassLoader.getSystemClassLoader(), delegates);
			lis = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
		}
		
		// Do not notify listeners with lock held!
		if(lis != null)
		{
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
		}
	}
	
	/**
	 *  Get all managed entries as URLs.
	 *  @return url The urls.
	 */
	public synchronized IFuture getURLs()
	{
		return new Future(new ArrayList(libcl.getDelegates().keySet()));
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
		ret.addAll(libcl.getDelegates().keySet());
		ret.addAll(SUtil.getClasspathURLs(libcl));
		ret.addAll(SUtil.getClasspathURLs(null));
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
	 */
	public IFuture	startService()
	{
		final Future	ret	= new Future();
		super.startService().addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				SServiceProvider.getService(provider,ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						ISettingsService	settings	= (ISettingsService)result;
						settings.registerPropertiesProvider(LIBRARY_SERVICE, LibraryService.this)
							.addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								super.customResultAvailable(getServiceIdentifier());
							}
						});
					}
					public void exceptionOccurred(Exception exception)
					{
						// No settings service: ignore
						ret.setResult(getServiceIdentifier());
					}
				});
			}
		});
		return ret;
	}

	/** 
	 *  Shutdown the service.
	 *  Releases all cached resources and shuts down the library service.
	 *  @param listener The listener.
	 */
	public IFuture	shutdownService()
	{
		final Future	saved	= new Future();
		SServiceProvider.getService(provider,ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DelegationResultListener(saved)
		{
			public void customResultAvailable(Object result)
			{
				ISettingsService	settings	= (ISettingsService)result;
				settings.deregisterPropertiesProvider(LIBRARY_SERVICE)
					.addResultListener(new DelegationResultListener(saved));
			}
			public void exceptionOccurred(Exception exception)
			{
				// No settings service: ignore
				saved.setResult(null);
			}
		});
		
		final Future	ret	= new Future();
		saved.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				synchronized(this)
				{
					libcl = null;
					listeners.clear();
					LibraryService.super.shutdownService().addResultListener(new DelegationResultListener(ret));
				}
			}
		});
			
		return ret;
	}

	/**
	 *  Add an Library Service listener.
	 *  The listener is registered for changes in the loaded library states.
	 *  @param listener The listener to be added.
	 */
	public void addLibraryServiceListener(ILibraryServiceListener listener)
	{
		if(listener==null)
			System.out.println("here");
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
			if(string.startsWith("file:"))
			{
				try
				{
					string	= URLDecoder.decode(string, "UTF-8");
				}
				catch(UnsupportedEncodingException e)
				{
				}
			}
			
			url	= string.startsWith("file:") ? new File(string.substring(5)) : null;
			if(url==null)
			{
				File file	= new File(string);
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
		}
		
		if(url instanceof URL)
		{
			ret	= (URL)url;
		}
		else if(url instanceof File)
		{
			try
			{
				ret	= (new File(System.getProperty("user.dir"), SUtil.convertPathToRelative(((File)url).getAbsolutePath()))).toURI().toURL();
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
	
	//-------- IPropertiesProvider interface --------
	
	/**
	 *  Update from given properties.
	 */
	public IFuture setProperties(Properties props)
	{
		// Remove existing urls
		libcl = new DelegationClassLoader(ClassLoader.getSystemClassLoader());
		
		// Add new urls.
		Property[]	entries	= props.getProperties("entry");
		for(int i=0; i<entries.length; i++)
		{
			addPath(entries[i].getValue());
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture getProperties()
	{
		String[]	entries;
		if(libcl != null)
		{
			synchronized(this)
			{
				List urls = new ArrayList(libcl.getDelegates().keySet());
				entries	= new String[urls.size()];
				for(int i=0; i<entries.length; i++)
				{
					
					String	url	= urls.get(i).toString();
					File	file	= urlToFile(url.toString());
					if(file!=null)
					{
						String	filename	= SUtil.convertPathToRelative(file.getAbsolutePath());
						String fileurl;
						try
						{
							// URI wouldn't allow relative names, so pretend its an absolute path.
							fileurl = "file:"+new URI("file", null, "/"+filename.replace('\\', '/'), null).toURL().toString().substring(6);
						}
						catch(Exception e)
						{
							fileurl	= "file:"+filename.replace('\\', '/');
						}
						if(url.startsWith("jar:file:"))
						{
							entries[i]	= "jar:" + fileurl;
						}
						else
						{
							entries[i]	= fileurl;
						}
						if(url.endsWith("!/") && entries[i].endsWith("!"))
							entries[i]	+= "/";	// Stripped by new File(...).
					}
					else
					{
						entries[i]	= url;
					}
				}
			}
		}
		else
		{
			entries = new String[0];
		}
		
		Properties props	= new Properties();
		for(int i=0; i<entries.length; i++)
		{
			props.addProperty(new Property("entry", entries[i]));
		}
		return new Future(props);		
	}


	/**
	 *  Test if a file name is contained.
	 */
	public static int indexOfFilename(String url, List urlstrings)
	{
		int ret = -1;
		try
		{
			File file = urlToFile(url);
			if(file==null)
				file	= new File(url);
			for(int i=0; file!=null && i<urlstrings.size() && ret==-1; i++)
			{
				String	totest	= (String)urlstrings.get(i);
				File test = urlToFile(totest);
				if(test==null)
					test	= new File(totest);
				if(test!=null && file.getCanonicalPath().equals(test.getCanonicalPath()))
					ret = i;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 *  Convert an URL to a file.
	 *  @return null, if the URL is neither 'file:' nor 'jar:file:' URL. 
	 */
	public static File urlToFile(String url)
	{
		File	file	= null;
		if(url.startsWith("file:"))
		{
			try
			{
				url	= URLDecoder.decode(url, "UTF-8");
			}
			catch(UnsupportedEncodingException uee)
			{
			}
			file	= new File(url.substring(5));
		}
		else if(url.startsWith("jar:file:"))
		{
			try
			{
				url	= URLDecoder.decode(url, "UTF-8");
			}
			catch(UnsupportedEncodingException uee)
			{
			}
			file	= new File(url.substring(9));
		}
		return file;
	}	
}

