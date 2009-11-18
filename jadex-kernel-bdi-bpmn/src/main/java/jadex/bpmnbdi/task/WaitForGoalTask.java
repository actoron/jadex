package jadex.bpmnbdi.task;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
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
			}
			else if(goal.isSucceeded())
			{
				listener.resultAvailable(null);
			}
			else
			{
				Exception	e	= new GoalFailureException();
				e.fillInStackTrace();
				listener.exceptionOccurred(e);
			}
		}
		catch(Exception e)
		{
			listener.exceptionOccurred(e);
		}
	}
}
