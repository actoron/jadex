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
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Dispatch a goal and by default wait for the result.
 */
public class WaitForGoalTask	implements ITask
{
	/**
	 *  Execute the task.
	 */
	public IFuture execute(final ITaskContext context, BpmnInterpreter instance)
	{
		final Future ret = new Future();
		
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
							ret.setResult(null);
//							listener.resultAvailable(WaitForGoalTask.this, null);
						}
						else
						{
							Exception	e	= new GoalFailureException();
							e.fillInStackTrace();
							ret.setException(e);
//							listener.exceptionOccurred(WaitForGoalTask.this, e);
						}
					}
					
					public void goalAdded(AgentEvent ae)
					{
					}
				});
			}
			else if(goal.isSucceeded())
			{
				ret.setResult(null);
//				listener.resultAvailable(this, null);
			}
			else
			{
				Exception	e	= new GoalFailureException();
				e.fillInStackTrace();
				ret.setException(e);
//				listener.exceptionOccurred(this, e);
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
//			listener.exceptionOccurred(this, e);
		}
		
		return ret;
	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture cancel(final BpmnInterpreter instance)
	{
		return IFuture.DONE;
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
