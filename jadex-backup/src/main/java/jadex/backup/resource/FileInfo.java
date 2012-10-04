package jadex.backup.resource;


/**
 *  Public meta information about a file in a resource
 *  used for transfer between resource providers and clients.
 */
public class FileInfo
{
	//-------- attributes --------
	
	/** The file location relative to the resource root (using '/' as separator char). */
	protected String	location;
	
	/** True, if the file is a directory. */
	protected boolean	directory;
	
	/** The vector time ("platform1@time1.platform2@time2..."). */
	protected String	vtime;
	
	//-------- constructors --------
	
	/**
	 *  Create a file info.
	 */
	public FileInfo()
	{
		// bean constructor.
	}
	
	/**
	 *  Create a file info.
	 */
	public FileInfo(String location, boolean directory, String vtime)
	{
		this.location	= location;
		this.directory	= directory;
		this.vtime	= vtime;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the location.
	 *  @return the location.
	 */
	public String getLocation()
	{
		return location;
	}

	/**
	 *  Set the location.
	 *  @param location The location to set.
	 */
	public void setLocation(String location)
	{
		this.location = location;
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
	 *  Get the vector time.
	 */
	public String	getVTime()
	{
		return vtime;
	}
	
	/**
	 *  Set the vector time.
	 */
	public void	setVTime(String vtime)
	{
		this.vtime	= vtime;
	}
}
