package jadex.bdi.examples.hunterprey_classic;

/**
 *  The task struct.
 */
public class TaskInfo
{
	//-------- attributes --------

	/** The action to perform. */
	protected Object action;

	/** The asking thread. */
	protected Thread thread;

	/** The result. */
	protected Object result;

	//-------- constructors --------

	/**
	 *  Create a new task.
	 */
	public TaskInfo(Object action)
	{
		this.action = action;
		this.thread = Thread.currentThread();
	}

	//-------- methods --------

	/**
	 *  Get the action.
	 *  @return The action.
	 */
	public Object getAction()
	{
		return action;
	}

	/**
	 *  Get the thread.
	 *  @return The thread.
	 */
	public Thread getThread()
	{
		return thread;
	}

	/**
	 *  Get the result.
	 *  @return The result.
	 */
	public Object getResult()
	{
		return result;
	}

	/**
	 *  Set the result.
	 *  @param result The result.
	 */
	public void setResult(Object result)
	{
		this.result = result;
	}
}

