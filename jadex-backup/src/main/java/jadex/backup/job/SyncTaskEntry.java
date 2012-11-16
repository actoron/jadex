package jadex.backup.job;

import jadex.backup.resource.FileInfo;
import jadex.commons.SUtil;

/**
 * 
 */
public class SyncTaskEntry
{
	//-------- attributes --------
	
	/** The id. */
	protected String id;
	
	/** The task id. */
	protected String taskid;
	
	/** The local file info. */
	protected FileInfo localfi;
	
	/** The remote file info. */
	protected FileInfo remotefi;
	
	/** The type. */
	protected String type;
	
	/** The action to perform. */
	protected String action;

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
	public SyncTaskEntry(Task task, FileInfo localfi, FileInfo remotefi, String type, String action)
	{
		this.taskid = task.getId();
		this.id = SUtil.createUniqueId("entry");
		this.localfi = localfi;
		this.remotefi = remotefi;
		this.type = type;
		this.action	= action;
	}
	
	/**
	 * 
	 */
	public SyncTaskEntry(SyncTaskEntry se)
	{
		this.taskid = se.getTaskId();
		this.id = se.getId();
		this.localfi = se.getLocalFileInfo();
		this.remotefi = se.getRemoteFileInfo();
		this.type = se.getType();
		this.action = se.getAction();
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
	 *  Get the local file info.
	 *  @return The file info.
	 */
	public FileInfo getLocalFileInfo()
	{
		return localfi;
	}

	/**
	 *  Set the local file info.
	 *  @param fi The file info to set.
	 */
	public void setLocalFileInfo(FileInfo fi)
	{
		this.localfi = fi;
	}

	/**
	 *  Get the remote file info.
	 *  @return The file info.
	 */
	public FileInfo getRemoteFileInfo()
	{
		return remotefi;
	}

	/**
	 *  Set the remote file info.
	 *  @param fi The file info to set.
	 */
	public void setRemoteFileInfo(FileInfo fi)
	{
		this.localfi = fi;
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
	 *  Get the action.
	 *  @return The included.
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 *  Set the action.
	 *  @param action The action to set.
	 */
	public void setAction(String action)
	{
		this.action = action;
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