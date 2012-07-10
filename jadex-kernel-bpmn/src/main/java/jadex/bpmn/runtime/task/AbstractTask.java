package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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
	public IFuture<Void> execute(ITaskContext context, BpmnInterpreter instance)
	{
		Future<Void> ret = new Future<Void>();
		
		try
		{
			doExecute(context, instance);
			ret.setResult(null);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Cleanup in case the task is cancelled.
	 *  @return	A future to indicate when cancellation has completed.
	 */
	public IFuture<Void> cancel(final BpmnInterpreter instance)
	{
		return IFuture.DONE;
	}

	/**
	 *  Execute the task.
	 *  Implement this method for synchroneous tasks, which are
	 *  finished, when the method returns.
	 *  @param context	The accessible values.
	 *  @param instance	The process instance executing the task.
	 *  @throws	Exception When task execution fails.
	 */
	public abstract void doExecute(ITaskContext context, BpmnInterpreter instance)	throws Exception;
}
