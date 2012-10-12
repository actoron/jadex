package jadex.backup.job.processing;


/**
 * 
 */
public abstract class AJobProcessingEvent
{
	/** Task was added. */
	public static final String INITIAL = "initial";
	
	/** Task was added. */
	public static final String TASK_ADDED = "task_added";
	
	/** Task was removed. */
	public static final String TASK_REMOVED = "task_removed";

	/** Task has changed. */
	public static final String TASK_CHANGED = "task_changed";

	//-------- attributes --------

	/** The event type. */
	protected String type;
	
	//-------- constructors --------

	/**
	 *  Create a new job event.
	 */
	public AJobProcessingEvent()
	{
	}
	
	/**
	 *  Create a new job event.
	 */
	public AJobProcessingEvent(String type)
	{
		this.type = type;
	}

	//-------- methods --------

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
	 *  Get the jobid.
	 *  @return The jobid.
	 */
	public abstract String getJobId();
}

