package jadex.backup.resource;

import java.util.List;

/**
 *  Helper class holding information on a file or directory during sync operations.
 */
public class StackElement
{
	//-------- attributes --------
	
	/** The file location. */
	protected String	location;
	
	/** The remote file info. */
	protected FileInfo	fi;
	
	/** The list of children to be processed (for directories). */
	protected List<String>	subfiles;
	
	/** The index of the next subfile to be processed. */
	protected int	index;
	
	//-------- constructors --------
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(String location)
	{
		this.location	= location;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the location.
	 */
	public String getLocation()
	{
		return location;
	}
	
	/**
	 *  Get the file info.
	 *  @return The file info or null, if not yet set.
	 */
	public FileInfo getFileInfo()
	{
		return fi;
	}
	
	/**
	 *  Set the file info.
	 */
	public void setFileInfo(FileInfo fi)
	{
		this.fi = fi;
	}
	
	/**
	 *  Get the subfiles.
	 *  @return The subfiles or null, if not yet set.
	 */
	public List<String> getSubfiles()
	{
		return subfiles;
	}
	
	/**
	 *  Set the subfiles.
	 */
	public void setSubfiles(List<String> subfiles)
	{
		this.subfiles = subfiles;
	}
	
	/**
	 *  Get the index of the next sub file.
	 */
	public int getIndex()
	{
		return index;
	}
	
	/**
	 *  Get the full path of the next sub file and increment the index.
	 */
	public String getNextSubfile()
	{
		String	file	= subfiles.get(index++);
		return "/".equals(location) ? location + file : location + "/" + file;
	}

	/**
	 *  Get a string representation.
	 */
	public String toString()
	{
		return location+": "+subfiles;
	}
}
