package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IResultCommand;
import jadex.commons.concurrent.DelegationResultListener;

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
			Object res = ((IResultCommand)step[0]).execute(instance); 
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
			
		}
	}
	
	//-------- static methods --------
	
//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static TaskMetaInfo getMetaInfo()
//	{
//		String desc = "Task is for executing an externally scheduled action.";
//		ParameterMetaInfo textmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			String.class, "text", null, "The text parameter should contain the text to be printed.");
//		
//		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{textmi}); 
//	}
}