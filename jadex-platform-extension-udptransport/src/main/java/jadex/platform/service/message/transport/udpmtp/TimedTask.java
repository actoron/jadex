package jadex.platform.service.message.transport.udpmtp;

/**
 *  A task scheduled for execution in the future.
 *
 */
public abstract class TimedTask implements Runnable
{
	/** The scheduled execution time. */
	protected long executiontime;
	
	/** An optional key for identifying the task. */
	protected Object key;
	
	/**
	 *  Creates the task.
	 *  @param executiontime The scheduled execution time.
	 */
	public TimedTask(long executiontime)
	{
		this(null, executiontime);
	}
	
	/**
	 *  Creates the task.
	 *  @param executiontime The scheduled execution time.
	 */
	public TimedTask(Object key, long executiontime)
	{
		this.key = key;
		this.executiontime = executiontime;
	}

	/**
	 *  Gets the execution time.
	 *
	 *  @return The execution time.
	 */
	public long getExecutionTime()
	{
		return executiontime;
	}

	/**
	 *  Gets the key.
	 *
	 *  @return The key.
	 */
	public Object getKey()
	{
		return key;
	}
}
