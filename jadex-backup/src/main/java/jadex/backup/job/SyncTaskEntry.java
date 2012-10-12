package jadex.backup.job;

import jadex.backup.resource.FileInfo;
import jadex.commons.SUtil;

/**
 * 
 */
public class SyncTaskEntry
{
	/** The id. */
	protected String id;
	
	/** The task id. */
	protected String taskid;
	
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
	public SyncTaskEntry(Task task, FileInfo fi, String type)
	{
		this.taskid = task.getId();
		this.id = SUtil.createUniqueId("entry");
		this.fileinfo = fi;
		this.type = type;
		this.included = true;
	}
	
	/**
	 * 
	 */
	public SyncTaskEntry(SyncTaskEntry se)
	{
		this.taskid = se.getTaskId();
		this.id = se.getId();
		this.fileinfo = se.getFileInfo();
		this.type = se.getType();
		this.included = se.isIncluded();
	}
	
	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
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
	
	/**
	 *  Get the taskid.
	 *  @return The taskid.
	 */
	public String getTaskId()
	{
		return taskid;
	}

	/**
	 *  Set the taskid.
	 *  @param taskid The taskid to set.
	 */
	public void setTaskId(String taskid)
	{
		this.taskid = taskid;
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return 31 + ((id == null) ? 0 : id.hashCode());
	}

	/**
	 *  Test for equality.
	 *  @param obj The object.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof SyncTaskEntry && ((SyncTaskEntry)obj).getId().equals(getId()); 
	}
	
}