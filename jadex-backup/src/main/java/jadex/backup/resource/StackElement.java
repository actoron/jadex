package jadex.backup.resource;

import java.util.List;

/**
 *  Helper class holding information on a file or directory during sync operations.
 */
public class StackElement
{
	//-------- attributes --------
	
	/** The remote file info. */
	protected FileInfo	fi;
	
	/** The list of children to be processed (for directories). */
	protected List<StackElement>	subfiles;
	
	/** The index of the next subfile to be processed. */
	protected int	index;
	
	//-------- constructors --------
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(FileInfo fi)
	{
		this.fi	= fi;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the file info.
	 */
	public FileInfo getFileInfo()
	{
		return fi;
	}
	
	/**
	 *  Get the subfiles.
	 *  @return The subfiles or null, if not yet set.
	 */
	public List<StackElement> getSubfiles()
	{
		return subfiles;
	}
	
	/**
	 *  Set the subfiles.
	 */
	public void setSubfiles(List<StackElement> subfiles)
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
	 *  Get the next sub file if any and increment the index.
	 *  @return null when all sub elements have been fetched.
	 */
	public StackElement getNextSubfile()
	{
		return index<subfiles.size() ? subfiles.get(index++) : null;
	}

	/**
	 *  Get a string representation.
	 */
	public String toString()
	{
		return fi.getLocation();
	}
}
