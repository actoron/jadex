package maventest;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ISettingsService;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.library.ILibraryService;
import jadex.bridge.service.library.ILibraryServiceListener;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *  Library service for loading classpath elements.
 */
public class MavenLibraryService extends BasicService implements ILibraryService, IPropertiesProvider
{
	//-------- attributes --------
	
	/** The provider. */
	protected IServiceProvider provider;
	
	/** LibraryService listeners. */
	protected Set listeners;

	/** The init urls. */
	protected Object[] initurls;

	/** Current ClassLoader. */
	protected DelegationURLClassLoader	libcl;
	
	/** The map of managed resources (url (or artifact id?) -> delegate loader. */
	protected Map classloaders;
	
	/** URL reference count */
	protected Map urlrefcount;
	
	//-------- constructors --------
	
	/** 
	 *  Creates a new LibraryService.
	 */ 
	public MavenLibraryService(IServiceProvider provider)
	{
		this(null, provider);
	}
	
	/** 
	 *  Creates a new LibraryService.
	 *  @param urls	Urls may be specified as java.net.URLs, java.io.Files or java.lang.Strings.
	 *  	Strings are interpreted as relative files (relative to current directory),
	 *  	absolute files or URLs (whatever can be found). 
	 */ 
	public MavenLibraryService(Object[] urls, IServiceProvider provider)
	{
		this(urls, provider, null);
	}
	
	/** 
	 *  Creates a new LibraryService.
	 *  @param urls	Urls may be specified as java.net.URLs, java.io.Files or java.lang.Strings.
	 *  	Strings are interpreted as relative files (relative to current directory),
	 *  	absolute files or URLs (whatever can be found). 
	 */ 
	public MavenLibraryService(Object[] urls, IServiceProvider provider, Map properties)
	{
		super(provider.getId(), ILibraryService.class, properties);
		
		this.classloaders = new HashMap();
		this.initurls = urls;
		this.provider = provider;

		if(urls!=null)
		{
			for(int i=0; i<urls.length; i++)
			{
				getClassLoader(toURL(urls[i]));
			}
		}
		
//		updateGlobalClassLoader();
		
		listeners	= Collections.synchronizedSet(new HashSet());
		urlrefcount = Collections.synchronizedMap(new HashMap());
	}
	
//	/**
//	 *  Update the global class loader.
//	 *  
//	 *  hack: should be removed
//	 */
//	public void updateGlobalClassLoader()
//	{
//		DelegationURLClassLoader[] delegates = (DelegationURLClassLoader[])classloaders.values().toArray(new DelegationURLClassLoader[classloaders.size()]);
//		
//		/* $if !android $ */
//		this.libcl = new DelegationURLClassLoader(ClassLoader.getSystemClassLoader(), delegates);
//		/* $else $
//		this.libcl = new DelegationClassLoader(LibraryService.class.getClassLoader(), urls);
//		$endif $ */
//		
//		System.out.println("update global: "+delegates.length);
//	}

	/**
	 *  Get or create a classloader for an url.
	 */
	public IFuture<DelegationURLClassLoader> getClassLoader(final URL url)
	{
		System.out.println("getClassLoader(): "+url);
		
		Future<DelegationURLClassLoader> ret = new Future<DelegationURLClassLoader>();
		DelegationURLClassLoader cl = (DelegationURLClassLoader)classloaders.get(url);
		
		if(cl==null)
		{
			InputStream pom = null;
			
			if(url.getProtocol().equals("jar"))
			{
				try
				{
					JarURLConnection con = (JarURLConnection)url.openConnection();
					JarFile jarfile = con.getJarFile();
					
					for(Enumeration<JarEntry> files =jarfile.entries(); files.hasMoreElements(); )
					{
						JarEntry entry = files.nextElement();
						String name = entry.getName();
						if(name.endsWith("pom.xml"))
						{
							pom = jarfile.getInputStream(entry);
							break;
						}
					}
				}
				catch(Exception e)
				{
				}
			}
			else
			{
				// move up from classes to root (root/target/classes)
				try
				{
					File dir = urlToFile(url.toString());
					dir = dir.getParentFile();
					dir = dir.getParentFile();
					File pomfile = new File(dir, "pom.xml");
					pom = new FileInputStream(pomfile);
				}
				catch(Exception e)
				{
				}
			}
			
			createClassLoader(url, pom).addResultListener(new DelegationResultListener<DelegationURLClassLoader>(ret));

			if(pom==null)
			{
				System.out.println("No pom found in: "+url);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a class loader based on pom dependencies.
	 */
	public IFuture<DelegationURLClassLoader> createClassLoader(final URL url, InputStream pom)
	{
		final Future<DelegationURLClassLoader> ret = new Future<DelegationURLClassLoader>();
		
		if(pom==null)
		{
			DelegationURLClassLoader cl = new DelegationURLClassLoader(url, ClassLoader.getSystemClassLoader(), null);
			classloaders.put(url, cl);
//			updateGlobalClassLoader();
			ret.setResult(cl);
		}
		else
		{
			getDependencies(pom).addResultListener(
				new ExceptionDelegationResultListener<URL[], DelegationURLClassLoader>(ret)
			{
				public void customResultAvailable(URL[] deps)
				{
					CollectionResultListener<DelegationURLClassLoader> lis = new CollectionResultListener<DelegationURLClassLoader>
						(deps.length, true, new DefaultResultListener<Collection<DelegationURLClassLoader>>()
					{
						public void resultAvailable(Collection<DelegationURLClassLoader> result) 
						{
							DelegationURLClassLoader cl = new DelegationURLClassLoader(url, 
								ClassLoader.getSystemClassLoader(), result.toArray(new DelegationURLClassLoader[result.size()]));
							classloaders.put(url, cl);
//							updateGlobalClassLoader();
							ret.setResult(cl);
						}
					});
					for(int i=0; i<deps.length; i++)
					{
						getClassLoader(deps[i]).addResultListener(lis);
					}
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Get the dependent urls.
	 */
	public IFuture<URL[]> getDependencies(InputStream pomstream)
	{
		URL[] res = new URL[0];
		try
		{
			MavenBuilder	mb	= new MavenBuilder();
			mb.loadDependenciesFromPom(pomstream, null);
			File[]	files	= mb.resolveAsFiles();
			res = new URL[files.length];
			for(int i=0; i<files.length; i++)
			{
				System.out.println("resolved: "+files[i]);
				res[i] = toURL("jar:file:"+files[i].getAbsolutePath()+"!/");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return new Future<URL[]>(res);
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
	public void addURL(final URL url)
	{
//		System.out.println("add "+url);
		ILibraryServiceListener[] tmp = null;
		
		synchronized(this)
		{
			Integer refcount = (Integer)urlrefcount.get(url);
			if(refcount != null)
			{
				urlrefcount.put(url, new Integer(refcount.intValue() + 1));
			}
			else
			{
				urlrefcount.put(url, new Integer(1));
				tmp = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
			}
		}
		
		if(tmp != null)
		{
			final ILibraryServiceListener[] lis = tmp;
			
			getClassLoader(url).addResultListener(new DefaultResultListener<DelegationURLClassLoader>()
			{
				public void resultAvailable(DelegationURLClassLoader result)
				{
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
								// todo: how to handle timeouts?! allow manual retry?
//								exception.printStackTrace();
								removeLibraryServiceListener(liscopy);
							}
						});
					}
				}
			});
		}
	}
	
	/**
	 *  Remove a url.
	 *  @param url The url.
	 */
	public void removeURL(final URL url)
	{
//		System.out.println("remove "+url);
		ILibraryServiceListener[] tmp = null;
		
		synchronized(this)
		{
			Integer refcount = (Integer)urlrefcount.get(url);
			if(refcount == null)
				throw new RuntimeException("Unknown URL: "+url);
			refcount = new Integer(refcount.intValue() - 1);
			urlrefcount.put(url, refcount);
			if(refcount.intValue() < 1)
			{
				classloaders.remove(url);
				tmp = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
			}
		}
		
		// Do not notify listeners with lock held!
		
		if(tmp != null)
		{
			final ILibraryServiceListener[] lis = tmp;
			
			getClassLoader(url).addResultListener(new DefaultResultListener<DelegationURLClassLoader>()
			{
				public void resultAvailable(DelegationURLClassLoader result)
				{
//					updateGlobalClassLoader();
					
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
								// todo: how to handle timeouts?! allow manual retry?
		//						exception.printStackTrace();
								removeLibraryServiceListener(liscopy);
							}
						});
					}
				}
			});
		}
	}
	
	/**
	 *  Remove a url completely (all references).
	 *  @param url The url.
	 */
	public void removeURLCompletely(URL url)
	{
//		System.out.println("rem all "+url);
		ILibraryServiceListener[] lis = null;
		synchronized(this)
		{
			urlrefcount.remove(url);
			classloaders.remove(url);
//			updateGlobalClassLoader();
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
						// todo: how to handle timeouts?! allow manual retry?
//						exception.printStackTrace();
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
	public synchronized IFuture<List<URL>> getURLs()
	{
		return new Future<List<URL>>(new ArrayList<URL>(libcl.getAllURLs()));
	}

	/**
	 *  Get other contained (but not directly managed) URLs.
	 *  @return The list of urls.
	 */
	public synchronized IFuture<List<URL>> getNonManagedURLs()
	{
		return new Future<List<URL>>(SUtil.getClasspathURLs(libcl));	
	}
	
	/**
	 *  Get all urls (managed and non-managed).
	 *  @return The list of urls.
	 */
	public IFuture<List<URL>> getAllURLs()
	{
		List<URL> ret = new ArrayList<URL>();
		ret.addAll(libcl.getAllURLs());
		ret.addAll(SUtil.getClasspathURLs(libcl));
		ret.addAll(SUtil.getClasspathURLs(null));
		return new Future<List<URL>>(ret);
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
	public IFuture<Void>	startService()
	{
		final Future<Void>	ret	= new Future<Void>();
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
						settings.registerPropertiesProvider(LIBRARY_SERVICE, MavenLibraryService.this)
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
						ret.setResult(null);
//						ret.setResult(getServiceIdentifier());
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
	public IFuture<Void>	shutdownService()
	{
//		System.out.println("shut");
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
		
		final Future<Void>	ret	= new Future<Void>();
		saved.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				synchronized(this)
				{
					libcl = null;
					listeners.clear();
					MavenLibraryService.super.shutdownService().addResultListener(new DelegationResultListener(ret));
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
		boolean	jar	= false;
		if(url instanceof String)
		{
			String	string	= (String) url;
			if(string.startsWith("file:") || string.startsWith("jar:file:"))
			{
				try
				{
					string	= URLDecoder.decode(string, "UTF-8");
				}
				catch(UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}
			}
			
			jar	= string.startsWith("jar:file:");
			url	= jar ? new File(string.substring(9))
				: string.startsWith("file:") ? new File(string.substring(5)) : null;
			
			
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
				String	abs	= ((File)url).getAbsolutePath();
				String	rel	= SUtil.convertPathToRelative(abs);
				ret	= abs.equals(rel) ? new File(abs).toURI().toURL()
					: new File(System.getProperty("user.dir"), rel).toURI().toURL();
				if(jar)
				{
					if(ret.toString().endsWith("!"))
						ret	= new URL("jar:"+ret.toString()+"/");	// Add missing slash in jar url.
					else
						ret	= new URL("jar:"+ret.toString());						
				}
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
	public IFuture<List<String>> getURLStrings()
	{
		final Future<List<String>> ret = new Future<List<String>>();
		
		getURLs().addResultListener(new IResultListener<List<URL>>()
		{
			public void resultAvailable(List<URL> result)
			{
//				List urls = (List)result;
				// TODO Auto-generated method stub
				List<String> tmp = new ArrayList<String>();
				// todo: hack!!!
				
				for(Iterator<URL> it=result.iterator(); it.hasNext(); )
				{
					URL	url	= it.next();
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
	public IFuture<List<String>> getNonManagedURLStrings()
	{
		final Future<List<String>> ret = new Future<List<String>>();
		
		getNonManagedURLs().addResultListener(new IResultListener<List<URL>>()
		{
			public void resultAvailable(List<URL> result)
			{
//				List urls = (List)result;
				List<String> tmp = new ArrayList<String>();
				// todo: hack!!!
				
				for(Iterator<URL> it=result.iterator(); it.hasNext(); )
				{
					URL	url	= it.next();
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
	 *  Returns the current ClassLoader.
	 *  @return the current ClassLoader
	 */
	@Excluded()
	public ClassLoader getClassLoader(IResourceIdentifier rid)
	{
		ClassLoader ret = null;
		
		if(rid==null)
		{
			DelegationURLClassLoader[] delegates = (DelegationURLClassLoader[])classloaders.values().toArray(new DelegationURLClassLoader[classloaders.size()]);
			ret = new DelegationURLClassLoader(ClassLoader.getSystemClassLoader(), delegates);
		}
		else
		{
			Tuple2<IComponentIdentifier, URL> lid = rid.getLocalIdentifier();
			
			// Local case
			IComponentIdentifier root = ((IComponentIdentifier)provider.getId()).getRoot();
			if(root.equals(lid.getFirstEntity()))
			{
				ret = (ClassLoader)classloaders.get(lid.getSecondEntity());
				
				if(ret==null)
				{
					URL[] urls = (URL[])classloaders.keySet().toArray(new URL[classloaders.size()]);
					
					for(int i=0; ret==null && i<urls.length; i++)
					{
						File path = urlToFile(urls[i].toString());
						File file = urlToFile(lid.getSecondEntity().toString());
						File tmp = file;
						while(tmp!=null && ret==null)
						{
							if(file.equals(path))
							{
								ret = (ClassLoader)classloaders.get(urls[i]);
							}
							else
							{
								tmp = tmp.getParentFile();
							}
						}
					}
				}
			}
			
			// Global case
			// todo:
		}
		
		if(ret==null)
			throw new RuntimeException("No classloader responsible for: "+rid);
		
		return ret;
	}
	
	/**
	 *  Get a class definition.
	 *  @param name The class name.
	 *  @return The class definition as byte array.
	 */
	public IFuture<byte[]> getClassDefinition(String name)
	{
		Future<byte[]> ret = new Future<byte[]>();
		
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
	public IFuture<Void> setProperties(Properties props)
	{
		// Do not remove existing urls?
		// todo: treat arguments and 
		// Remove existing urls
//		libcl = new DelegationURLClassLoader(ClassLoader.getSystemClassLoader(), initurls);
		
		// todo: fix me / does not work getClassLoader is async
		if(initurls!=null)
		{
			for(int i=0; i<initurls.length; i++)
			{
				getClassLoader(toURL(initurls[i]));
			}
		}
//		updateGlobalClassLoader();
		
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
	public IFuture<Properties> getProperties()
	{
		String[]	entries;
		if(libcl != null)
		{
			synchronized(this)
			{
				List urls = new ArrayList(libcl.getAllURLs());
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
		return new Future<Properties>(props);		
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
	 *  @return null, if the URL is neither 'file:' nor 'jar:file:' URL and no path point to an existing file.
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
		else
		{
			file	= new File(url);
			if(!file.exists())
			{
				file	= null;
			}
		}
		return file;
	}	
}

