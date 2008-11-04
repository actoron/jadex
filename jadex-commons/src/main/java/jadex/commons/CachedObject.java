package jadex.commons;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *  A cache for an object that was loaded from a file or url.
 *  The cache can be made persistent on disk (uses the filename).
 *  For a new cache the object to be cached can be set.
 */
public class CachedObject implements Serializable
{
	//-------- attributes --------

	/** The cached object. */
	protected Serializable cachedobject;

	/** The filename of the cache. */
	protected String filename;

	/** The last modified date. */
	protected long lastmodified;

	//-------- construction --------

	/**
	 *  Create a new file cache.
	 */
	public CachedObject(String filename, long lastmodified, Serializable cachedobject)
	{
		this.filename = filename;
		this.lastmodified = lastmodified;
		this.cachedobject = cachedobject;
	}

	//-------- methods --------

	/**
	 *  Set the cached object.
	 *  @param cachedobject The cached object.
	 */
	public void setObject(Serializable cachedobject)
	{
		this.cachedobject = cachedobject;
	}

	/**
	 *  Get the cached object
	 *  @return The cached object.
	 */
	public Serializable getObject()
	{
		return cachedobject;
	}

	/**
	 *  Get the last modified date.
	 *  @return The last modified date.
	 */
	public long getLastModified()
	{
		return lastmodified;
	}

	/**
	 *  Set the last modified date.
	 *  @param lastmodified The last modified date.
	 */
	public void setLastModified(long lastmodified)
	{
		this.lastmodified = lastmodified;
	}

	/**
	 *  Get the filename.
	 *  @return The file name.
	 */
	public String getFilename()
	{
		return filename;
	}

	/**
	 * Set the filename.
	 * @param filename The file name.
	 */
	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	/**
	 *  Persist the cached object.
	 */
	public synchronized void persist() throws IOException
	{
		if(filename==null)
			throw new IOException("Filename nulls.");
		if(filename.startsWith("jar:"))
			return;	// Hack!!! persist() should not be called for jar files?

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
