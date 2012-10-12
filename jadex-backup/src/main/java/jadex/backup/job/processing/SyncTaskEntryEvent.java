package jadex.backup.job.processing;


/**
 * 
 */
public class SyncTaskEntryEvent extends AJobProcessingEvent
{
	public static final String ENTRY_PROGRESS = "entry_progress";
	
	protected String taskid;
	
	protected String entryid;
	
	protected double done;
	
	//-------- constructors --------

	/**
	 *  Create a new event.
	 */
	public SyncTaskEntryEvent()
	{
	}
	
	/**
	 *  Create a new job event.
	 */
	public SyncTaskEntryEvent(String taskid, String entryid, double done)
	{
		super(ENTRY_PROGRESS);
		this.taskid = taskid;
		this.entryid = entryid;
		this.done = done;
	}

	//-------- methods --------

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
	 *  Get the entryid.
	 *  @return The entryid.
	 */
	public String getEntryId()
	{
		return entryid;
	}

	/**
	 *  Set the entryid.
	 *  @param entryid The entryid to set.
	 */
	public void setEntryId(String entryid)
	{
		this.entryid = entryid;
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
	 *  Get the job id.
	 *  @return The jobid.
	 */
	public String getJobId()
	{
		// todo:?
		return null;
	}
}
