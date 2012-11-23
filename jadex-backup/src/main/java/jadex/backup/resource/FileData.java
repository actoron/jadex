package jadex.backup.resource;


/**
 *  Generic file data replacement
 */
public class FileData //implements IFile
{
	//-------- attributes --------
	
	/** The file location relative to the resource root (using '/' as separator char). */
	protected String	path;
	
	/** True, if the file is a directory. */
	protected boolean	directory;
	
	/** True, if the file or directory exists. */
	protected boolean	exists;
	
	/** The file size. */
	protected long	size;
	
	/** The last modified date from underlying file. */
	protected long lastmodified;
	
	/**
	 * 
	 */
	public FileData()
	{
	}
	
	/**
	 * 
	 */
	public FileData(String path)
	{
		this.path = path;
	}
	
	/**
	 * 
	 */
	public FileData(String path, boolean directory, boolean exists, long size, long lastmodified)
	{
		this.path = path;
		this.directory = directory;
		this.exists = exists;
		this.size = size;
		this.lastmodified = lastmodified;
	}

	/**
	 *  Get the path.
	 *  @return The path.
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 *  Set the path.
	 *  @param path The path to set.
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 *  Get the directory.
	 *  @return The directory.
	 */
	public boolean isDirectory()
	{
		return directory;
	}

	/**
	 *  Set the directory.
	 *  @param directory The directory to set.
	 */
	public void setDirectory(boolean directory)
	{
		this.directory = directory;
	}

	/**
	 *  Get the exists.
	 *  @return The exists.
	 */
	public boolean isExisting()
	{
		return exists;
	}

	/**
	 *  Set the exists.
	 *  @param exists The exists to set.
	 */
	public void setExisting(boolean exists)
	{
		this.exists = exists;
	}

	/**
	 *  Get the size.
	 *  @return The size.
	 */
	public long getSize()
	{
		return size;
	}

	/**
	 *  Set the size.
	 *  @param size The size to set.
	 */
	public void setSize(long size)
	{
		this.size = size;
	}

	/**
	 *  Get the lastmodified.
	 *  @return The lastmodified.
	 */
	public long getLastModified()
	{
		return lastmodified;
	}

	/**
	 *  Set the lastmodified.
	 *  @param lastmodified The lastmodified to set.
	 */
	public void setLastModified(long lastmodified)
	{
		this.lastmodified = lastmodified;
	}
	
}
