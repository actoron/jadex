package jadex.commons;

import jadex.commons.collection.LRU;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  Loader for managing models, loaded from disc and kept in cache.
 */
public abstract class AbstractModelLoader
{
	//-------- attributes --------
	
	/** The supported file extensions (if any). */
	protected String[]	extensions;
	
	/** The model cache (filename -> loaded model). */
	protected LRU modelcache;
	
	/** The registered models (filename -> loaded model). */
	protected Map registered;
	
	//-------- constructors --------
	
	/**
	 *  Create a model loader.
	 *  @param extensions	The supported file extensions by order of importance.
	 */
	public AbstractModelLoader(String[] extensions)
	{
		this(extensions, 450);
	}
	
	/**
	 *  Create a model loader.
	 *  @param extensions	The supported file extensions by order of importance.
	 */
	public AbstractModelLoader(String[] extensions, int cachesize)
	{
		this.extensions	= extensions.clone();
		this.modelcache	= new LRU(cachesize);
		this.registered	= new LinkedHashMap();
	}

	//-------- helper methods --------
	
	/**
	 *  Load a model.
	 *  @param name	The original name (i.e. not filename).
	 *  @param info	The resource info.
	 */
	protected abstract ICacheableModel	doLoadModel(String name, String[] imports, ResourceInfo info, ClassLoader classloader, Object context) throws Exception;
	
	/**
	 *  Find the file for a given name using any supported extension.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 *  @return The resource info identifying the file.
	 *  @throws	Exception when the file could not be found.
	 */
	protected ResourceInfo	getResourceInfo(String name, String[] imports, ClassLoader classloader) throws Exception
	{
		ResourceInfo ret;
		if(registered.containsKey(name))
		{
			// Hack!!! ignore file handling for registered models.
			ICacheableModel	model	= (ICacheableModel)registered.get(name);
			ret	= new ResourceInfo(name, null, model.getLastModified());
		}
		else
		{
			ret = getResourceInfo0(name, imports, classloader);
	
			if(ret==null || ret.getInputStream()==null)
				throw new IOException("File "+name+" not found in imports.");//: "+SUtil.arrayToString(imports));
		}

		return ret;
	}

	/**
	 *  Find the file for a given name using any supported extension.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 *  @return The resource info identifying the file or null.
	 */
	protected ResourceInfo getResourceInfo0(String name, String[] imports, ClassLoader classloader)
	{
		// Try to find directly as absolute path.
		ResourceInfo ret = SUtil.getResourceInfo0(name, classloader);

		for(int ex=0; (ret==null || ret.getInputStream()==null) && ex<extensions.length; ex++)
		{
			// Strip extension if present.
			String	tname	= name.endsWith(extensions[ex]) ? name.substring(0, name.length()-extensions[ex].length()) : name;
			// Fully qualified package name? Can also be full package name with empty package ;-)
			String resstr	= SUtil.replace(tname, ".", "/") + extensions[ex];
			ret	= SUtil.getResourceInfo0(resstr, classloader);

			// Try to find in imports.
			for(int i=0; (ret==null || ret.getInputStream()==null) && imports!=null && i<imports.length; i++)
			{
				// Package import
				if(imports[i].endsWith(".*"))
				{
					resstr = SUtil.replace(imports[i].substring(0,
						imports[i].length()-1), ".", "/") + tname + extensions[ex];
					ret	= SUtil.getResourceInfo0(resstr, classloader);
				}
				// Direct import
				else if(imports[i].endsWith(tname))
				{
					resstr = SUtil.replace(imports[i], ".", "/") + extensions[ex];
					ret	= SUtil.getResourceInfo0(resstr, classloader);
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Find the file for a given name.
	 *  @param name	The filename or logical name (resolved via imports and extension).
	 *  @param extension	The required extension.
	 *  @param imports	The imports, if any.
	 *  @return The resource info identifying the file.
	 */
	protected ResourceInfo	getResourceInfo(String name, String extension, String[] imports, ClassLoader classloader) throws Exception
	{
		ResourceInfo ret;
		if(registered.containsKey(name))
		{
			// Hack!!! ignore file handling for registered models.
			ICacheableModel	model	= (ICacheableModel)registered.get(name);
			ret	= new ResourceInfo(name, null, model.getLastModified());
		}
		else
		{
			// Try to find directly as absolute path.
			String resstr = name;
			ret = SUtil.getResourceInfo0(resstr, classloader);
			if(ret!=null && !ret.getFilename().endsWith(extension))
				ret	= null;
			
			if(name.endsWith(extension))
				name	= name.substring(0, name.length()-extension.length());
	
			if(ret==null || ret.getInputStream()==null)
			{
				// Fully qualified package name? Can also be full package name with empty package ;-)
				resstr	= SUtil.replace(name, ".", "/") + extension;
				ret	= SUtil.getResourceInfo0(resstr, classloader);
	
				// Try to find in imports.
				for(int i=0; (ret==null || ret.getInputStream()==null) && imports!=null && i<imports.length; i++)
				{
					// Package import
					if(imports[i].endsWith(".*"))
					{
						resstr = SUtil.replace(imports[i].substring(0,
							imports[i].length()-1), ".", "/") + name + extension;
						ret	= SUtil.getResourceInfo0(resstr, classloader);
					}
					// Direct import
					else if(imports[i].endsWith(name))
					{
						resstr = SUtil.replace(imports[i], ".", "/") + extension;
						ret	= SUtil.getResourceInfo0(resstr, classloader);
					}
				}
			}
	
			if(ret==null || ret.getInputStream()==null)
				throw new IOException("File "+name+" not found in imports");//: "+SUtil.arrayToString(imports));
		}
		return ret;
	}
	
	// todo: synchronize modelcache!
	
	/**
	 *  Set the class loader.
	 *  @param classloader The class loader.
	 * /
	public synchronized void setClassLoader(ClassLoader classloader)
	{
//		System.out.println("Classloader set: "+this+" "+classloader);
		this.classloader = classloader;
		modelcache.clear();
	}*/

	//-------- methods --------

	/**
	 *  Load a model.
	 *  @param name	The name of the model (file name or logical name).
	 *  @param imports	The imports to use when resolving logical names.
	 */
	public synchronized ICacheableModel	loadModel(String name, String[] imports, ClassLoader classloader, Object context) throws Exception
	{
		return loadModel(name, null, imports, classloader, context);
	}

	/**
	 *  Load a model with a required extension.
	 *  @param name	The name of the model (file name or logical name).
	 *  @param extension	The specific extension to look for.
	 *  @param imports	The imports to use when resolving logical names.
	 */
	public synchronized ICacheableModel	loadModel(String name, String extension, String[] imports, ClassLoader classloader, Object context) throws Exception
	{
//		System.out.println("filename: "+name);
		
		// Lookup cache by name/extension/imports
		ICacheableModel cached = null;
		Object[] keys	= imports!=null? new Object[imports.length+3]: new Object[3];
		keys[0]	= name;
		keys[1]	= extension;
		keys[2] = classloader;
		if(imports!=null)
			System.arraycopy(imports, 0, keys, 3, imports.length);
		Tuple	keytuple	= new Tuple(keys);
		
		ResourceInfo	info	= null;
		//		synchronized(modelcache)
//		{
			cached	= getCachedModel(keytuple);
//			System.out.println("hit: "+name+" "+cached);
			// If model is in cache, check at most every second if file on disc is newer.
			if(cached!=null && cached.getLastChecked()+1000<System.currentTimeMillis())
			{
				info	= extension!=null ? getResourceInfo(name, extension, imports, classloader) : getResourceInfo(name, imports, classloader);
				if(cached.getLastModified()<info.getLastModified())
				{
					cached	= null;
				}
				else
				{
					cached.setLastChecked(System.currentTimeMillis());
					info.cleanup();
				}
			}
//		}

		if(cached==null && info==null)
		{
			// Lookup cache by resolved filename.
//			synchronized(modelcache)
//			{
				info	= extension!=null ? getResourceInfo(name, extension, imports, classloader) : getResourceInfo(name, imports, classloader);
				cached	= getCachedModel(info.getFilename());
				if(cached!=null)
				{
					if(cached.getLastModified()<info.getLastModified())
					{
						cached	= null;
					}
					else
					{
						cached.setLastChecked(System.currentTimeMillis());
					}

					// Associate cached model to new key (name/extension/imports).
					modelcache.put(keytuple, cached);
				}
//			}
		}
			
		// Not found: load from disc and store in cache.
		if(cached==null)
		{
			try
			{
				cached	= doLoadModel(name, imports, info, classloader, context);
	
				// Store by filename also, to avoid reloading with different imports.
//				modelcache.put(info.getFilename(), cached);
				modelcache.put(cached.getFilename(), cached);
//				System.out.println("cached: "+info.getFilename()+" "+cached.getFilename()+" "+classloader);
//				System.out.println("cached: "+cached.getFilename()+" "+classloader);
				
				// Associate cached model to new key (name/extension/imports).
				modelcache.put(keytuple, cached);
			}
			finally
			{
				info.cleanup();
			}
		}

		return cached;
	}
	
	/**
	 *  Test, if a resource is loadable (in principle).
	 *  Tests if the resource is a file that matches the supported file extensions
	 *  or if the resource is a logical name, tests if a corresponding file exist.
	 *  The file is not actually loaded, i.e. the content of the file is not checked.
	 * /
	public boolean	isLoadable(String name, String[] imports)
	{
		boolean	loadable	= false;
		ResourceInfo	rinfo	= getResourceInfo0(name, imports);
		if(rinfo!=null)
		{
			String filename	= rinfo.getFilename();
			for(int i=0; !loadable && i<extensions.length; i++)
			{
				loadable	= filename.endsWith(extensions[i]);
			}
		}
		return loadable;
	}*/

	/**
	 * 
	 * /
	protected String getFilenameExtension(String filename)
	{
		String ret = null;
		for(int i=1; i<extensions.length; i++)	// skip i=0 for no extension.
		{
			if(filename.endsWith(extensions[i]))
				ret = extensions[i];
		}
		if(ret==null)
			throw new RuntimeException("Unknown extension: "+filename);
		return ret;
	}*/
	
	/**
	 *  Register a model.
	 */
	public void	registerModel(Object key, ICacheableModel model)
	{
		registered.put(key, model);
	}
	
	/**
	 *  Deregister a model.
	 */
	public void	deregisterModel(Object key)
	{
		registered.remove(key);
	}
	
	/**
	 *  Clears the model cache.
	 */
	public void clearModelCache()
	{
		modelcache.clear();
	}
	
	/**
	 *  Get a model from cache (if any).
	 */
	protected ICacheableModel	getCachedModel(Object key)
	{
		ICacheableModel	ret	= null;
		if(modelcache.containsKey(key))
		{
			ret	= (ICacheableModel)modelcache.get(key);
		}
		else if(registered.containsKey(key))
		{
			ret	= (ICacheableModel)registered.get(key);
		}
		return ret;
	}
}
