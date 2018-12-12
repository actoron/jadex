package jadex.commons;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import jadex.commons.collection.SCollection;

/**
 *  A cache for CachedObjects that reflect files.
 *  A cached object is loaded per filename
 * 	(not the filename of the held object in the cache).
 */
public class ObjectCache implements Serializable
{
	//-------- attributes --------

	/** The persist strategy (always or only on demand by manually calling persist). */
	protected boolean persist_always;

	/** The flag indicating if cached objects are stored on disk themselves. */
	protected boolean persist_single;

	/** The filename of the cache. */
	protected String filename;

	/** The expression caches for filename. */
	// todo: are not cleaned up currently, only when outdated
	protected Map cache;

	//-------- constructors --------

	/**
	 *  Create a new cache.
	 */
	public ObjectCache(String filename)
	{
		this(filename, false, false);
	}

	/**
	 *  Create a new cache.
	 */
	public ObjectCache(String filename, boolean persist_always, boolean persist_single)
	{
		this(filename, persist_always, persist_single, 25);
	}
	
	/**
	 *  Create a new cache.
	 */
	public ObjectCache(String filename, boolean persist_always, boolean persist_single, int max)
	{
		if(!persist_single && filename==null)
			throw new IllegalArgumentException("Filename must not null when saving to single file.");
		this.persist_always = persist_always;
		this.persist_single = persist_single;
		this.filename = filename;
		//this.cache = SCollection.createHashMap();
		this.cache = SCollection.createLRU(max);
	}

	//-------- methods --------

	/**
	 *  Load the cached file.
	 *  @param filename The filename of the cached object.
	 *  @param lastmodified The last modified date important for the up-to-date check.
	 * 	 -1 for do not check.
	 */
	public synchronized CachedObject loadCachedObject(String filename, long lastmodified)
	{
		assert filename!=null;

		// Try to retrieve from caches for cache ;-)
		CachedObject ret = (CachedObject)cache.get(filename);

		// Otherwise try to load file/url.
		if(ret==null && persist_single)
		{
			InputStream fis = null;
			ObjectInputStream ois = null;
			try
			{
				if(filename.startsWith("jar:") || filename.startsWith("http:") || filename.startsWith("ftp:"))
					fis	= new URL(filename).openStream();
				else
					fis = new FileInputStream(filename);
				ois = new ObjectInputStream(fis);
				ret = (CachedObject)ois.readObject();
				cache.put(filename, ret);
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				//System.out.println("Could not load expression cache: "+filename);
			}
			try{if(fis!=null)fis.close();}catch(IOException e){e.printStackTrace();}
			try{if(ois!=null)ois.close();}catch(IOException e){e.printStackTrace();}
		}

		// Check consistency of expression cache.
		if(ret!=null)
		{
			// Check cached date.
			if(lastmodified!=-1 && lastmodified!=ret.getLastModified()
				// For jar entries use zip format precision of 2 seconds.
				&& !(filename.startsWith("jar:") && (Math.abs(lastmodified-ret.getLastModified())<2000)))
			{
//				System.out.println("Cache outdated: "+filename);
				ret = null;
				cache.remove(filename);
			}
		}

		return ret;
	}

	/**
	 *  Add a cached object.
	 *  @param co The new object.
	 */
	public synchronized void add(CachedObject co)
	{
		// Do nothing if object is already contained in cache.
		if(cache.containsKey(co.getFilename()))
			return; //throw new RuntimeException("Cache already contains object: "+co.getFilename());

		cache.put(co.getFilename(), co);

		if(persist_always)
		{
			if(!persist_single)
			{
				try
				{
					persist();
				}
				catch(IOException e)
				{
					throw new RuntimeException("Could not persist object cache: "+filename);
				}
			}
			// Only for performance here only the new object is saved. 
			else
			{
				try
				{
					co.persist();
				}
				catch(IOException e)
				{
					throw new RuntimeException("Could not persist cached object: "+filename);
				}
			}
		}
	}

	/**
	 *  Add a cached object.
	 *  @param co The new object.
	 * /
	public void remove(CachedObject co)
	{
		cache.remove(co.getFilename());
	}*/

	/**
	 *  Add a cached object.
	 *  @param filename The filename.
	 */
	public synchronized void remove(String filename)
	{
		cache.remove(filename);
	}

	/**
	 *  Presist the cached object.
	 */
	public synchronized void persist() throws IOException
	{
		if(!persist_single)
		{
			if(!filename.startsWith("jar:"))
			{
				try
				{
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
					oos.writeObject(this);
					oos.close();
					//System.out.println("Wrote cache to disk: "+filename);
				}
				catch(FileNotFoundException e)
				{
					//e.printStackTrace();
					throw new IOException("Could not write cache to disk: "+filename);
				}
			}
		}
		else
		{
			CachedObject[] cos = (CachedObject[])cache.values().toArray(new CachedObject[cache.size()]);
			for(int i=0; i<cos.length; i++)
			{
				cos[i].persist();
			}
		}
	}

	/**
	 *  Clear the cache.
	 */
	public synchronized void clear()
	{
		cache.clear();
	}

	//-------- static methods --------

	/**
	 *  Load the model cache.
	 *  @param filename The filename.
	 *  @return The object cache.
	 */
	public static synchronized ObjectCache loadObjectCache(String filename)
	{
		ObjectCache ret = null;
		InputStream fis = null;
		ObjectInputStream ois = null;
		try
		{
			if(filename.startsWith("jar:") || filename.startsWith("http:") || filename.startsWith("ftp:"))
				fis	= new URL(filename).openStream();
			else
				fis = new FileInputStream(filename);
			ois = new ObjectInputStream(fis);
			ret = (ObjectCache)ois.readObject();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			System.out.println("Could not load models cache: "+filename);
		}
		try{if(fis!=null)fis.close();}catch(IOException e){e.printStackTrace();}
		try{if(ois!=null)ois.close();}catch(IOException e){e.printStackTrace();}
		return ret;
	}
}
