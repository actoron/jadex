package jadex.backup.job;

import jadex.backup.resource.FileInfo;

/**
 * 
 */
public class SyncEntry
{
	/** The file info. */
	protected FileInfo fileinfo;
	
	/** The type. */
	protected String type;
	
	/** The flag if it should be done. */
	protected boolean accepted;

	/**
	 * 
	 */
	public SyncEntry()
	{
	}
	
	/**
	 * 
	 */
	public SyncEntry(FileInfo fi, String type)
	{
		this.fileinfo = fi;
		this.type = type;
	}
	
	/**
	 *  Get the fileInfo.
	 *  @return The fileInfo.
	 */
	public FileInfo getFileInfo()
	{
		return fileinfo;
	}

	/**
	 *  Set the fileInfo.
	 *  @param fileInfo The fileInfo to set.
	 */
	public void setFileInfo(FileInfo fileInfo)
	{
		this.fileinfo = fileInfo;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the accepted.
	 *  @return The accepted.
	 */
	public boolean isAccepted()
	{
		return accepted;
	}

	/**
	 *  Set the accepted.
	 *  @param accepted The accepted to set.
	 */
	public void setAccepted(boolean accepted)
	{
		this.accepted = accepted;
	}
}