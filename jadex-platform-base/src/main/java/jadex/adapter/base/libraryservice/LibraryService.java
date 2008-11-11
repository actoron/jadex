package jadex.adapter.base.libraryservice;

import jadex.bridge.ILibraryService;
import jadex.bridge.ILibraryServiceListener;
import jadex.bridge.IPlatformService;
import jadex.commons.concurrent.IResultListener;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 *  Library service for loading classpath elements.
 */
public class LibraryService implements IPlatformService, ILibraryService
{
	//-------- attributes --------
	
	/** LibraryService listeners. */
	private Set listeners;

	/** The initial parent ClassLoader. */
	private ClassLoader baseClassLoader;
	
	/** The urls. */
	private List	urls;

	/** Current ClassLoader. */
	private ClassLoader	libraryClassLoader;

	/** Current ClassLoader. * /
	private DynamicMemoryClassLoader libraryClassLoader;*/

	//-------- constructors --------
	
	/** 
	 *  Creates a new LibraryService.
	 */ 
	public LibraryService()
	{
		this(null);
	}
	
	/** 
	 *  Creates a new LibraryService.
	 *  @param urls	Urls may be specified as java.net.URLs, java.io.Files or java.lang.Strings.
	 *  	Strings are interpreted as relative files (relative to current directory),
	 *  	absolute files or URLs (whatever can be found). 
	 */ 
	public LibraryService(Object[] urls)
	{
		baseClassLoader = Thread.currentThread().getContextClassLoader();
		libraryClassLoader = baseClassLoader;
		
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

	//-------- methods --------
	
	/**
	 *  Convert a file/string/url.
	 */
	private URL toURL(Object url)
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
	 *  Add a new url.
	 *  @param url The url.
	 */
	public synchronized void addURL(URL url)
	{
		urls.add(url);
		libraryClassLoader = new URLClassLoader((URL[]) urls.toArray(new URL[urls.size()]), baseClassLoader);
		fireURLAdded(url);

//		String path = url.getFile();
//		File file = new File(path);
//		if(checkJar(file))
//			addJar(path);
//		else
//			addPath(path);
	}
	
	/**
	 *  Remove a url.
	 *  @param url The url.
	 */
	public synchronized void removeURL(URL url)
	{
		urls.remove(url);
		libraryClassLoader = new URLClassLoader((URL[]) urls.toArray(new URL[urls.size()]), baseClassLoader);
		fireURLRemoved(url);

//		String path = url.getFile();
//		File file = new File(path);
//		if(checkJar(file))
//			removeJar(path);
//		else
//			removePath(path);
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
//		List jars = libraryClassLoader.getJars();
//		for(Iterator it=jars.iterator(); it.hasNext(); )
//		{
//			String entry = (String)it.next();
//			try
//			{
//				ret.add(new URL("file:///"+entry));
//			}
//			catch(MalformedURLException e)
//			{
//				// Maybe invalid classpath entries --> just ignore.
//				// Hack!!! Print warning?
//				//e.printStackTrace();
//				System.out.println("Warning, invalid classpath entry: "+entry);
//			}
//		}
//		
//		List paths = libraryClassLoader.getPaths();
//		for(Iterator it=paths.iterator(); it.hasNext(); )
//		{
//			String entry = (String)it.next();
//			try
//			{
//				File file = new File(entry);
//				if(file.isDirectory() && !entry.endsWith(System.getProperty("file.separator")))
//				{
//					// Normalize, that directories end with "/".
//					entry	+= System.getProperty("file.separator");
//				}
//				ret.add(new URL("file:///"+entry));
//			}
//			catch(MalformedURLException e)
//			{
//				// Maybe invalid classpath entries --> just ignore.
//				// Hack!!! Print warning?
//				//e.printStackTrace();
//				System.out.println("Warning, invalid classpath entry: "+entry);
//			}
//		}
//		
//		return ret;
	}
	
	/** 
	 *  Adds a .jar-file
	 *  @param path path to the .jar-file
	 * /
	public synchronized void addJar(String path)
	{
		File jarFile = new File(path);

		if(!checkJar(jarFile))
		{
			throw new RuntimeException("Attempted to add invalid .jar-file: " + path);
		}

		List jars = libraryClassLoader.getJars();
		jars.add(path);

		libraryClassLoader = new DynamicMemoryClassLoader(baseClassLoader, jars, libraryClassLoader.getPaths());
		fireJarAdded(path);
	}

	/** 
	 *  Removes a .jar-file.
	 *  @param path path to the .jar-file
	 * /
	public synchronized void removeJar(String path)
	{
		List jars = libraryClassLoader.getJars();
		if(!jars.remove(path))
			throw new RuntimeException("Jar not handled by library service: "+path);
			
		libraryClassLoader = new DynamicMemoryClassLoader(baseClassLoader, jars, libraryClassLoader.getPaths());
		fireJarRemoved(path);
	}

	/** 
	 *  Adds a path to the ClassLoader class path.
	 *  @param path new path
	 * /
	public synchronized void addPath(String path)
	{
//		if(!path.endsWith(File.separator))
//			path = path + File.separator;
		// Always URL syntax -> "/"
		if(!path.endsWith("/"))
			path = path + "/";

		List paths = libraryClassLoader.getPaths();
		paths.add(path);

		libraryClassLoader = new DynamicMemoryClassLoader(baseClassLoader, libraryClassLoader.getJars(), paths);
		firePathAdded(path);
	}

	/** 
	 *  Removes a path from the ClassLoader class path.
	 *  @param path path that should be removed
	 * /
	public synchronized void removePath(String path)
	{
//		if(!path.endsWith(File.separator))
//			path = path + File.separator;
		// Always URL syntax -> "/"
		if(!path.endsWith("/"))
			path = path + "/";

		List paths = libraryClassLoader.getPaths();
		if(!paths.remove(path))
			throw new RuntimeException("Path not handled by library service: "+path);
		
		libraryClassLoader = new DynamicMemoryClassLoader(baseClassLoader, libraryClassLoader.getJars(), paths);
		firePathRemoved(path);
	}*/

	/** 
	 *  Returns the currently loaded .jar-files.
	 *  @return currently loaded .jar-files
	 * /
	public List getLoadedJars()
	{
		return libraryClassLoader.getJars();
	}

	/** 
	 *  Returns the currently loaded class paths.
	 *  @return currently loaded class paths
	 * /
	public List getLoadedPaths()
	{
		return libraryClassLoader.getPaths();
	}*/

	/** 
	 *  Returns the current ClassLoader
	 *  @return the current ClassLoader
	 */
	public ClassLoader getClassLoader()
	{
		return libraryClassLoader;
	}

	/**
	 *  Start the service.
	 */
	public void start()
	{
//		baseClassLoader = Thread.currentThread().getContextClassLoader();
//		libraryClassLoader = new DynamicMemoryClassLoader(baseClassLoader, null, null);
	}

	/** 
	 *  Shutdown the service.
	 *  Releases all cached resources and shuts down the library service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
		baseClassLoader = null;
		libraryClassLoader = null;
		listeners.clear();

		if(listener != null)
		{
			listener.resultAvailable(null);
		}
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
	 */
	private synchronized void fireURLAdded(URL url)
	{
		for(Iterator it = listeners.iterator(); it.hasNext();)
		{
			ILibraryServiceListener listener = (ILibraryServiceListener)it.next();
			listener.urlAdded(url);
		}
	}

	/** 
	 *  Fires the class-path-removed event
	 *  @param path the removed class path
	 */
	private synchronized void fireURLRemoved(URL url)
	{
		for(Iterator it = listeners.iterator(); it.hasNext();)
		{
			ILibraryServiceListener listener = (ILibraryServiceListener)it.next();
			listener.urlRemoved(url);
		}
	}

	/** 
	 *  Fires the jar-added event
	 *  @param path path to new .jar-file
	 * /
	private synchronized void fireJarAdded(String path)
	{
		for(Iterator it = listeners.iterator(); it.hasNext();)
		{
			ILibraryServiceListener listener = (ILibraryServiceListener)it.next();
			listener.jarAdded(path);
		}
	}

	/** 
	 *  Fires the jar-removed event
	 *  @param path path to new .jar-file
	 * /
	private synchronized void fireJarRemoved(String path)
	{
		for(Iterator it = listeners.iterator(); it.hasNext();)
		{
			ILibraryServiceListener listener = (ILibraryServiceListener)it.next();
			listener.jarRemoved(path);
		}
	}

	/** 
	 *  Fires the class-path-added event
	 *  @param path the new class path
	 * /
	private synchronized void firePathAdded(String path)
	{
		for(Iterator it = listeners.iterator(); it.hasNext();)
		{
			ILibraryServiceListener listener = (ILibraryServiceListener)it.next();
			listener.pathAdded(path);
		}
	}

	/** 
	 *  Fires the class-path-removed event
	 *  @param path the removed class path
	 * /
	private synchronized void firePathRemoved(String path)
	{
		for(Iterator it = listeners.iterator(); it.hasNext();)
		{
			ILibraryServiceListener listener = (ILibraryServiceListener)it.next();
			listener.pathRemoved(path);
		}
	}*/

//	private class DynamicMemoryClassLoader extends ClassLoader
//	{
//		/** Cache for previously loaded classes. */
//		private Map classCache;
//
//		/** Cache for previously loaded resources. */
//		private Map resourceCache;
//
//		/** List of .jar-files used for class loading. */
////		private Set jars;
//		private List jars;
//		
//		/** List of class paths used for class loading.*/
////		private Set paths;
//		private List paths;
//
//		/** 
//		 * Creates a new DynamicClassLoader
//		 * @param parent parent ClassLoader
//		 * @param jars jar files in memory
//		 */
//		public DynamicMemoryClassLoader(ClassLoader parent, List jars, List paths)
//		{
//			super(parent);
//			classCache = Collections.synchronizedMap(new HashMap());
//			resourceCache = Collections.synchronizedMap(new HashMap());
////			this.jars = Collections.synchronizedSet(new HashSet(jars));
////			this.paths = Collections.synchronizedSet(new HashSet(paths));
//			this.jars = jars==null?  Collections.synchronizedList(new ArrayList()): Collections.synchronizedList(new ArrayList(jars));
//			this.paths = paths==null?  Collections.synchronizedList(new ArrayList()): Collections.synchronizedList(new ArrayList(paths));
//		}
//
//		protected Class findClass(String name) throws ClassNotFoundException
//		{
//			Class clazz = null;
//
//			if((clazz = (Class)classCache.get(name)) != null)
//			{
//				return clazz;
//			}
//
//			String classFilePath = name.replace('.', File.separatorChar) + ".class";
//			byte[] classFile = loadFile(classFilePath);
//
//			if(classFile != null)
//			{
//				try
//				{
//					clazz = defineClass(name, classFile, 0, classFile.length);
//					classCache.put(name, clazz);
//					return clazz;
//				}
//				catch(ClassFormatError e)
//				{
//				}
//			}
//
//			throw new ClassNotFoundException();
//		}
//
//		protected URL findResource(String name)
//		{
//			URL ret = super.findResource(name);
//			if(ret != null)
//			{
//				return ret;
//			}
//
//			byte[] resource = null;
//
//			if((resource = (byte[])resourceCache.get(name)) != null)
//			{
//				try
//				{
//					return new URL("", "", 0, name, new MemoryURLStreamHandler(resource));
//				}
//				catch(MalformedURLException e)
//				{
//				}
//			}
//
//			resource = loadFile(name);
//
//			if(resource != null)
//			{
//				resourceCache.put(name, resource);
//				try
//				{
//					return new URL("", "", 0, name, new MemoryURLStreamHandler(resource));
//				}
//				catch(MalformedURLException e)
//				{
//				}
//			}
//
//			return null;
//		}
//
//		protected Enumeration findResources(String name) throws IOException
//		{
//			Vector resources = new Vector();
//
//			Iterator it = jars.iterator();
//			while(it.hasNext())
//			{
//				String jarPath = (String)it.next();
//				byte[] file = loadFileFromJar(name, jarPath);
//				if(file != null)
//				{
//					resources.add(new MemoryURLStreamHandler(file));
//				}
//			}
//
//			it = paths.iterator();
//			while(it.hasNext())
//			{
//				String filePath = (String)it.next() + name;
//				byte[] file = loadFileFromPath(filePath);
//				if(file != null)
//				{
//					resources.add(new MemoryURLStreamHandler(file));
//				}
//			}
//
//			return resources.elements();
//		}
//
//		public InputStream getResourceAsStream(String name)
//		{
//			InputStream ret = super.getResourceAsStream(name);
//			if(ret != null)
//			{
//				return ret;
//			}
//
//			URL resource = findResource(name);
//			if(resource != null)
//			{
//				try
//				{
//					ret = resource.openStream();
//				}
//				catch(IOException e)
//				{
//				}
//			}
//			return ret;
//		}
//
//		/** 
//		 *  Returns the set of jars used in this ClassLoader.
//		 *  @return set of .jar-files
//		 */
//		public List getJars()
//		{
//			return Collections.synchronizedList(new ArrayList(jars));
//		}
//
//		/** 
//		 *  Returns the set of class paths used in this ClassLoader.
//		 *  @return set of class paths
//		 */
//		public List getPaths()
//		{
//			return Collections.synchronizedList(new ArrayList(paths));
//		}
//
//		/** 
//		 *  Attempts to load a file from the jars or paths
//		 *  @param name path of the file
//		 *  @return the file loaded into memory or null if file is not found
//		 */
//		private byte[] loadFile(String path)
//		{
//			//Attempt to load the file from the .jar-files
//			byte[] ret = loadFileFromJars(path);
//
//			if(ret == null)
//			{
//				// If loading from .jar-files failed, attempt to load from paths
//				ret = loadFileFromPaths(path);
//			}
//
//			return ret;
//		}
//
//		/** 
//		 *  Attempts to load a file from the jar-files
//		 *  @param path path of the file
//		 *  @return the file loaded into memory or null if file is not found
//		 */
//		private byte[] loadFileFromJars(String path)
//		{
//			byte[] ret = null;
//			
//			Iterator it = jars.iterator();
//			while(it.hasNext())
//			{
//				String jarPath = (String)it.next();
//				byte[] file = loadFileFromJar(path, jarPath);
//				if(file != null)
//				{
//					ret = file;
//					break;
//				}
//			}
//
//			return ret;
//		}
//
//		/** 
//		 *  Attempts to load a file from a jar-file
//		 *  @param path path of the file
//		 *  @param jarPath path of the .jar-file
//		 *  @return the file loaded into memory or null if file is not found
//		 */
//		private byte[] loadFileFromJar(String path, String jarPath)
//		{
//			try
//			{
//				JarFile jarFile = new JarFile(jarPath);
//
//				JarEntry entry = jarFile.getJarEntry(path);
//				if(entry != null)
//				{
//					InputStream is = jarFile.getInputStream(entry);
//					ByteArrayOutputStream os = new ByteArrayOutputStream();
//					byte[] buf = new byte[4096];
//					int len = 0;
//					while((len = is.read(buf)) != -1)
//					{
//						os.write(buf, 0, len);
//					}
//					is.close();
//					os.close();
//
//					return os.toByteArray();
//				}
//			}
//			catch(IOException ioe)
//			{
//			}
//
//			// File not found
//			return null;
//		}
//
//		/** 
//		 *  Attempts to load a file from a paths
//		 *  @param path path of the file
//		 *  @return the file loaded into memory or null if file is not found
//		 */
//		private byte[] loadFileFromPaths(String path)
//		{
//			Iterator it = paths.iterator();
//			while(it.hasNext())
//			{
//				String filePath = (String)it.next() + path;
//				byte[] file = loadFileFromPath(filePath);
//				if(file != null)
//				{
//					return file;
//				}
//			}
//
//			// File not found
//			return null;
//		}
//
//		/** 
//		 *  Attempts to load a file using a path
//		 *  @param path path of the file
//		 *  @return the file loaded into memory or null if file is not found
//		 */
//		private byte[] loadFileFromPath(String path)
//		{
//			try
//			{
//				File file = new File(path);
//
//				if(file.exists())
//				{
//					InputStream is = new FileInputStream(file);
//					ByteArrayOutputStream os = new ByteArrayOutputStream();
//					byte[] buf = new byte[4096];
//					int len = 0;
//					while((len = is.read(buf)) != -1)
//					{
//						os.write(buf, 0, len);
//					}
//					is.close();
//					os.close();
//
//					return os.toByteArray();
//				}
//			}
//			catch(IOException ioe)
//			{
//			}
//
//			// File not found
//			return null;
//		}
//
//		private class MemoryURLStreamHandler extends URLStreamHandler
//		{
//			/** Data in memory. */
//			private byte[] data;
//
//			/** 
//			 *  Creates a new MemoryURLStreamHandler
//			 *  @param data data the StreamHandler represents
//			 */
//			public MemoryURLStreamHandler(byte[] data)
//			{
//				this.data = data;
//			}
//
//			protected URLConnection openConnection(URL u) throws IOException
//			{
//				return new MemoryURLConnection(u);
//			}
//
//			private class MemoryURLConnection extends URLConnection
//			{
//				public MemoryURLConnection(URL u)
//				{
//					super(u);
//				}
//
//				public void connect() throws IOException
//				{
//				}
//
//				public InputStream getInputStream() throws IOException
//				{
//					return new ByteArrayInputStream(data);
//				}
//
//				public int getContentLength()
//				{
//					return data.length;
//				}
//			}
//		}
//	}
}
