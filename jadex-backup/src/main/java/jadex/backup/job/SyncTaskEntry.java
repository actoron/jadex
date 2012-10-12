package jadex.backup.job;

import jadex.backup.resource.FileInfo;

/**
 * 
 */
public class SyncTaskEntry
{
	/** The file info. */
	protected FileInfo fileinfo;
	
	/** The type. */
	protected String type;
	
	/** The flag if it is included. */
	protected boolean included;

	/** The progress done. */
	protected double done;
	
	/**
	 * 
	 */
	public SyncTaskEntry()
	{
	}
	
	/**
	 * 
	 */
	public SyncTaskEntry(FileInfo fi, String type)
	{
		this.fileinfo = fi;
		this.type = type;
		this.included = true;
	}
	
	/**
	 * 
	 */
	public SyncTaskEntry(SyncTaskEntry se)
	{
		this.fileinfo = se.getFileInfo();
		this.type = se.getType();
		this.included = se.isIncluded();
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
	 *  Get the included.
	 *  @return The included.
	 */
	public boolean isIncluded()
	{
		return included;
	}

	/**
	 *  Set the included.
	 *  @param included The included to set.
	 */
	public void setIncluded(boolean included)
	{
		this.included = included;
	}

	/**
	 *  Get the done.
	 *  @return The done.
	 */
	public double getDone()
	{
		return done;
	}

	/**
	 *  Set the done.
	 *  @param done The done to set.
	 */
	public void setDone(double done)
	{
		this.done = done;
	}
}