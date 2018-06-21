package jadex.bpmn.model;

/**
 *  Class representing a task activity.
 *
 */
public class MTask extends MActivity
{
	/** The activity type for tasks. */
	public static final String TASK = "Task";
	
	/** The default unspecified task. */
	public static final String TASK_SUBTYPE_NONE = "none";
	
	/** The task type. */
	protected String tasktype;
	
	public MTask()
	{
		setTasktype(TASK_SUBTYPE_NONE);
	}
	
	/**
	 *  Hard-code the activity type.
	 */
	public String getActivityType()
	{
		return TASK;
	}
	
	/**
	 *  Returns the task type.
	 *  
	 *  @return The task type.
	 */
	public String getTaskType()
	{
		return tasktype;
	}
	
	/**
	 *  Sets the task type.
	 *  
	 *  @param tasktype The task type.
	 */
	public void setTasktype(String tasktype)
	{
		this.tasktype = tasktype;
	}
}
