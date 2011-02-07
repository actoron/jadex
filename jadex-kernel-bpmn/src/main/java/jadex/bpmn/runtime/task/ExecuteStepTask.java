package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.IComponentStep;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Execute an external step.
 */
public class ExecuteStepTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		Object[] step = (Object[])context.getParameterValue("step");
		Future ret = (Future)step[1];
		try
		{
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
		catch(Exception e)
		{
			ret.setException(e);
		}
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Task is for executing an externally scheduled action.\nThis task should not be used directly.";
		
		return new TaskMetaInfo(desc, new ParameterMetaInfo[0]); 
	}
}