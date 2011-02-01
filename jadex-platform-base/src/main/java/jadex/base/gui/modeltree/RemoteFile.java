package jadex.base.gui.modeltree;

/**
 * 
 */
public class RemoteFile
{
	/** The file name. */
	protected String filename;
	
	/** The path. */
	protected String path;
	
	/** the boolean for directory. */
	protected boolean directory;

	/**
	 * 
	 */
	public RemoteFile()
	{
	}

	/**
	 * 
	 */
	public RemoteFile(String filename, String path, boolean directory)
	{
		this.filename = filename;
		this.path = path;
		this.directory = directory;
	}

	/**
	 *  Get the filename.
	 *  @return the filename.
	 */
	public String getFilename()
	{
		return filename;
	}

	/**
	 *  Set the filename.
	 *  @param filename The filename to set.
	 */
	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	/**
	 *  Get the path.
	 *  @return the path.
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
	 *  @return the directory.
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
	
}
