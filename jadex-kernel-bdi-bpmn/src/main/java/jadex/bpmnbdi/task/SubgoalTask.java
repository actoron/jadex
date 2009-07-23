package jadex.bpmnbdi.task;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmnbdi.BpmnPlanBodyInstance;
import jadex.commons.concurrent.IResultListener;

import java.util.List;

/**
 *  Dispatch a subgoal and wait for the result.
 */
public class SubgoalTask	implements ITask
{
	/**
	 *  Execute the task.
	 */
	public void execute(final ITaskContext context, IProcessInstance instance, final IResultListener listener)
	{
		try
		{
			BpmnPlanBodyInstance	plan	= (BpmnPlanBodyInstance)instance;
			String type = (String)context.getPropertyValue("type");
			if(type==null)
				throw new RuntimeException("Type property for internal event not specified: "+this);

			final IGoal	subgoal	= plan.createGoal(type);
			List	params	= context.getModelElement().getParameters();
			for(int i=0; params!=null && i<params.size(); i++)
			{
				MParameter	param	= (MParameter)params.get(i);
				if(!param.getDirection().equals(MParameter.DIRECTION_OUT) && context.hasParameterValue(param.getName()))
				{
					subgoal.getParameter(param.getName()).setValue(context.getParameterValue(param.getName()));
				}
			}
			subgoal.addGoalListener(new IGoalListener()
			{
				public void goalFinished(AgentEvent ae)
				{
					subgoal.removeGoalListener(this);
					if(subgoal.isSucceeded())
					{
						List	params	= context.getModelElement().getParameters();
						for(int i=0; params!=null && i<params.size(); i++)
						{
							MParameter	param	= (MParameter)params.get(i);
							if(!param.getDirection().equals(MParameter.DIRECTION_IN))
							{
								context.setParameterValue(param.getName(), subgoal.getParameter(param.getName()).getValue());
							}
						}
						listener.resultAvailable(null);
					}
					else
					{
						Exception	e	= new GoalFailureException();
						e.fillInStackTrace();
						listener.exceptionOccurred(e);
					}
				}
				
				public void goalAdded(AgentEvent ae)
				{
				}
			});
			
			plan.dispatchSubgoal(subgoal);
		}
		catch(Exception e)
		{
			listener.exceptionOccurred(e);
		}
	}
}
