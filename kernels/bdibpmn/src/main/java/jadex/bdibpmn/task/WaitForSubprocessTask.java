package jadex.bdibpmn.task;

import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Wait for the subprocess result.
 */
@Task(description="The wait for subprocess task can be used to wait for an existing subprocess to finish.",
	parameters=@TaskParameter(name="subprocess", clazz=IResultFuture.class, direction=TaskParameter.DIRECTION_IN))
public class WaitForSubprocessTask	implements ITask
{
	/**
	 *  Execute the task.
	 */
	public IFuture<Void> execute(ITaskContext context, IInternalAccess instance)
	{
		// todo: ???
		
		IResultFuture	rf = (IResultFuture)context.getParameterValue("resultfuture");
//		listener.resultAvailable(this, rf.getResults());
		return new Future(rf.getResults());
	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture<Void> cancel(final IInternalAccess instance)
	{
		return IFuture.DONE;
	}
	
	//-------- static methods --------
	
//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static TaskMetaInfo getMetaInfo()
//	{
//		String desc = "The wait for subprocess task can be used to wait for an existing subprocess to finish.";
//		
//		ParameterMetaInfo goalmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			IResultFuture.class, "subprocess", null, "The subprocess parameter identifies the subprocess to be waited for.");
//
//		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{goalmi}); 
//	}
}
