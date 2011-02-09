package jadex.base.gui.filetree;

/**
 * 
 */
public class RemoteFile
{
	/** The file name. */
	protected String filename;
	
	/** The path. */
	protected String path;
	
	/** The boolean for directory. */
	protected boolean directory;
	
	/** The display name. */
	protected String displayname;

	/**
	 * 
	 */
	public RemoteFile()
	{
	}

	/**
	 * 
	 */
	public RemoteFile(String filename, String path, boolean directory, String displayname)
	{
		this.filename = filename;
		this.path = path;
		this.directory = directory;
		this.displayname = displayname;
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

	/**
	 *  Get the displayname.
	 *  @return the displayname.
	 */
	public String getDisplayName()
	{
		return displayname;
	}

	/**
	 *  Set the displayname.
	 *  @param displayname The displayname to set.
	 */
	public void setDisplayName(String displayname)
	{
		this.displayname = displayname;
	}
	
}
