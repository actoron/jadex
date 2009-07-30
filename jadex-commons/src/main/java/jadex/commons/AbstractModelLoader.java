package jadex.commons;

import java.io.IOException;
import java.util.HashMap;
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
	protected Map modelcache;
	
	/** The class loader. */
	protected ClassLoader classloader;
	
	//-------- constructors --------
	
	/**
	 *  Create a model loader.
	 *  @param extensions	The supported file extensions by order of importance.
	 */
	public AbstractModelLoader(String[] extensions)
	{
		if(extensions!=null)
		{
			this.extensions	= new String[extensions.length+1];
			this.extensions[0]	= "";
			System.arraycopy(extensions, 0, this.extensions, 1, extensions.length);
		}
		else
		{
			this.extensions	= new String[]{""};
		}
		this.modelcache	= new HashMap();
	}

	//-------- helper methods --------
	
	/**
	 *  Load a model.
	 *  @param name	The original name (i.e. not filename).
	 *  @param info	The resource info.
	 */
	protected abstract ICacheableModel	doLoadModel(String name, ResourceInfo info) throws Exception;
	
	/**
	 *  Find the file for a given name using any supported extension.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 *  @return The resource info identifying the file.
	 */
	protected ResourceInfo	getResourceInfo(String name, String[] imports) throws Exception
	{
		// Try to find directly as absolute path.
		String resstr = name;
		ResourceInfo ret = SUtil.getResourceInfo0(resstr, classloader);

		for(int ex=0; (ret==null || ret.getInputStream()==null) && ex<extensions.length; ex++)
		{
			// Fully qualified package name? Can also be full package name with empty package ;-)
			resstr	= SUtil.replace(name, ".", "/") + extensions[ex];
			ret	= SUtil.getResourceInfo0(resstr, classloader);

			// Try to find in imports.
			for(int i=0; (ret==null || ret.getInputStream()==null) && imports!=null && i<imports.length; i++)
			{
				// Package import
				if(imports[i].endsWith(".*"))
				{
					resstr = SUtil.replace(imports[i].substring(0,
						imports[i].length()-1), ".", "/") + name + extensions[ex];
					ret	= SUtil.getResourceInfo0(resstr, classloader);
				}
				// Direct import
				else if(imports[i].endsWith(name))
				{
					resstr = SUtil.replace(imports[i], ".", "/") + extensions[ex];
					ret	= SUtil.getResourceInfo0(resstr, classloader);
				}
			}
		}

		if(ret==null || ret.getInputStream()==null)
			throw new IOException("File "+name+" not found in imports: "+SUtil.arrayToString(imports));

		return ret;
	}
	
	/**
	 *  Find the file for a given name.
	 *  @param name	The filename or logical name (resolved via imports and extension).
	 *  @param extension	The required extension.
	 *  @param imports	The imports, if any.
	 *  @return The resource info identifying the file.
	 */
	protected ResourceInfo	getResourceInfo(String name, String extension, String[] imports) throws Exception
	{
		// Try to find directly as absolute path.
		String resstr = name;
		ResourceInfo ret = SUtil.getResourceInfo0(resstr, classloader);
		if(ret!=null && !ret.getFilename().endsWith(extension))
			ret	= null;

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
			throw new IOException("File "+name+" not found in imports: "+SUtil.arrayToString(imports));

		return ret;
	}
	
	// todo: synchronize modelcache!
	
	/**
	 *  Set the class loader.
	 *  @param classloader The class loader.
	 */
	public synchronized void setClassLoader(ClassLoader classloader)
	{
		this.classloader = classloader;
		modelcache.clear();
	}

	//-------- methods --------

	/**
	 *  Load a model.
	 */
	public synchronized ICacheableModel	loadModel(String name, String[] imports) throws Exception
	{
		return loadModel(name, null, imports);
	}

	/**
	 *  Load a model with a required extension.
	 */
	public synchronized ICacheableModel	loadModel(String name, String extension, String[] imports) throws Exception
	{
		// Lookup cache by name/extension/imports
		ICacheableModel cached = null;
		Object[] keys	= imports!=null? new Object[imports.length+2]: new Object[2];
		keys[0]	= name;
		keys[1]	= extension;
		if(imports!=null)
			System.arraycopy(imports, 0, keys, 2, imports.length);
		Tuple	keytuple	= new Tuple(keys);
		
		ResourceInfo	info	= null;
		//		synchronized(modelcache)
//		{
			cached	= (ICacheableModel)modelcache.get(keytuple);
			// If model is in cache, check at most every second if file on disc is newer.
			if(cached!=null && cached.getLastChecked()+1000<System.currentTimeMillis())
			{
				info	= extension!=null ? getResourceInfo(name, extension, imports) : getResourceInfo(name, imports);
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
				info	= extension!=null ? getResourceInfo(name, extension, imports) : getResourceInfo(name, imports);
				cached	= (ICacheableModel)modelcache.get(info.getFilename());
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
				cached	= doLoadModel(name, info);
	
				// Store by filename also, to avoid reloading with different imports.
				modelcache.put(info.getFilename(), cached);
				
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
}
