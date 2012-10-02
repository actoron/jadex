package jadex.backup.resource;

import java.io.File;
import java.io.IOException;


/**
 *  Information about a file in a resource.
 */
public class FileInfo
{
	//-------- attributes --------
	
	/** The file location relative to the resource root (using '/' as separator char). */
	protected String	location;
	
	/** True, if the file is a directory. */
	protected boolean	directory;
	
	/** The last modification date. */
	protected long	timestamp;
	
	//-------- constructors --------
	
	/**
	 *  Create a file info.
	 */
	public FileInfo()
	{
		// bean constructor.
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
	 *  Get the time stamp.
	 *  @return the time stamp.
	 */
	public long getTimeStamp()
	{
		return timestamp;
	}

	/**
	 *  Set the time stamp.
	 *  @param timestamp The time stamp to set.
	 */
	public void setTimeStamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the file for a file info.
	 *  @param root	The resource root.
	 *  @return The local file.
	 */
	public File	toFile(File root)
	{
		return new File(root, location.replace('/', File.separatorChar));
	}
	
	/**
	 *  Get the file info for a local file.
	 *  @param root	The resource root.
	 *  @param file The local file.
	 *  @return The file info.
	 */
	public static FileInfo	fromFile(File root, File file)
	{
		try
		{
			String	rpath	= root.getCanonicalPath();
			String	fpath	= file.getCanonicalPath();
			if(!fpath.startsWith(rpath))
			{
				throw new IllegalArgumentException("File '"+fpath+"' must be contained in resource root '"+rpath+"'.");
			}
			
			FileInfo	ret	= new FileInfo();
			ret.setLocation(fpath.substring(rpath.length()+1).replace(File.separatorChar, '/'));
			ret.setDirectory(file.isDirectory());
			ret.setTimeStamp(file.lastModified());
			
			return ret;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
