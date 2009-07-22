package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;

/**
 *  Simple task implementation with basic result and exception handling.
 */
public abstract class AbstractTask implements ITask
{
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param instance	The process instance executing the task.
	 *  @param listener	To be notified, when the task has completed.
	 */
	public void	execute(ITaskContext context, IProcessInstance instance, IResultListener listener)
	{
		try
		{
			doExecute(context, instance);
			listener.resultAvailable(null);
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
	 *  @param instance	The process instance executing the task.
	 *  @throws	Exception When task execution fails.
	 */
	public abstract void	doExecute(ITaskContext context, IProcessInstance instance)	throws Exception;
}
