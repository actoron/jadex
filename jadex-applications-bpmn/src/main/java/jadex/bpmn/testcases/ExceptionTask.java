package jadex.bpmn.testcases;

import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

@Task(description="Throw the specified exception", parameters={
		@TaskParameter(name="exceptionclass", clazz=Class.class, direction=TaskParameter.DIRECTION_IN)
	})
public class ExceptionTask implements ITask
{
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @return	To be notified, when the task has completed.
	 */
	public IFuture<Void> execute(ITaskContext context, IInternalAccess process)
	{
		Class<?> exclass = (Class<?>) context.getParameterValue("exceptionclass");
		Exception ex = null;
		try
		{
			ex = (Exception) exclass.newInstance();
		}
		catch (Exception e)
		{
			ex = e;
		}
		return new Future<Void>(ex);
	}
	
	/**
	 *  Cleanup in case the task is cancelled.
	 *  @return	A future to indicate when cancellation has completed.
	 */
	public IFuture<Void> cancel(IInternalAccess instance)
	{
		return IFuture.DONE;
	}
}
