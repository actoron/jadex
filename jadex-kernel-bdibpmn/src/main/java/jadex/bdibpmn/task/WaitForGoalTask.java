package jadex.bdibpmn.task;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.commons.concurrent.IResultListener;

/**
 *  Dispatch a goal and by default wait for the result.
 */
public class WaitForGoalTask	implements ITask
{
	/**
	 *  Execute the task.
	 */
	public void execute(final ITaskContext context, BpmnInterpreter instance, final IResultListener listener)
	{
		try
		{
			final IGoal	goal	= (IGoal)context.getParameterValue("goal");
			
			if(!goal.isFinished())
			{
				goal.addGoalListener(new IGoalListener()
				{
					public void goalFinished(AgentEvent ae)
					{
						goal.removeGoalListener(this);
						if(goal.isSucceeded())
						{
							listener.resultAvailable(WaitForGoalTask.this, null);
						}
						else
						{
							Exception	e	= new GoalFailureException();
							e.fillInStackTrace();
							listener.exceptionOccurred(WaitForGoalTask.this, e);
						}
					}
					
					public void goalAdded(AgentEvent ae)
					{
					}
				});
			}
			else if(goal.isSucceeded())
			{
				listener.resultAvailable(this, null);
			}
			else
			{
				Exception	e	= new GoalFailureException();
				e.fillInStackTrace();
				listener.exceptionOccurred(this, e);
			}
		}
		catch(Exception e)
		{
			listener.exceptionOccurred(this, e);
		}
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The wait for goal task can be used to wait for an existing goal.";
		
		ParameterMetaInfo goalmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			IGoal.class, "goal", null, "The goal parameter identifies the goal to be waited for.");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{goalmi}); 
	}
}
