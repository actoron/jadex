package jadex.platform.service.message.transport.udpmtp;

/**
 *  A task scheduled for execution in the future.
 *
 */
public abstract class TimedTask implements Runnable
{
	/** The scheduled execution time. */
	protected long executiontime;
	
	/**
	 *  Creates the task.
	 *  @param executiontime The scheduled execution time.
	 */
	public TimedTask(long executiontime)
	{
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
	
	
}
