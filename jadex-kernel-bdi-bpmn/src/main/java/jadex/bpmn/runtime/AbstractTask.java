package jadex.bpmn.runtime;

import jadex.commons.concurrent.IResultListener;

/**
 *  Simple task implementation with basic result and exception handling.
 */
public abstract class AbstractTask implements ITask
{
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @listener	To be notified, when the task has completed.
	 */
	public void	execute(ITaskContext context, IResultListener listener)
	{
		try
		{
			Object	result	= doExecute(context);
			listener.resultAvailable(result);
		}
		catch(Exception e)
		{
			listener.exceptionOccurred(e);
		}
	}

	/**
	 *  Execute the task.
	 *  Implement this method for synchroneous tasks, which are
	 *  finished, when the method returns.
	 *  @param context	The accessible values.
	 *  @return	The result, if any.
	 *  @throws	Exception When task execution fails.
	 */
	public abstract Object	doExecute(ITaskContext context)	throws Exception;
}
