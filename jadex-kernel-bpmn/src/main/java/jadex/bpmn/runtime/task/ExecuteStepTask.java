package jadex.bpmn.runtime.task;

import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Execute an external step.
 */
@Task(description="Task is for executing an externally scheduled action.\nThis task should not be used directly.",
	parameters=@TaskParameter(name="step", clazz=Object[].class, direction=TaskParameter.DIRECTION_IN,
		description="The component step (step, future) that should be executed.")
)
public class ExecuteStepTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		Object[] step = (Object[])context.getParameterValue("step");
		Future ret = (Future)step[1];
		Object res = ((IComponentStep)step[0]).execute(instance); 
		if(res instanceof IFuture)
		{
			((IFuture)res).addResultListener(new DelegationResultListener(((Future)step[1])));
		}
		else
		{
			ret.setResult(res);
		}
	}
	
	//-------- static methods --------
	
//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static TaskMetaInfo getMetaInfo()
//	{
//		String desc = "Task is for executing an externally scheduled action.\nThis task should not be used directly.";
//		
//		return new TaskMetaInfo(desc, new ParameterMetaInfo[0]); 
//	}
}