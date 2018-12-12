package jadex.bdibpmn.task;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Dispatch a goal and by default wait for the result.
 */
@Task(description="The wait for goal task can be used to wait for an existing goal.",
parameters={
	@TaskParameter(name="goal", clazz=IGoal.class, direction=TaskParameter.DIRECTION_IN, 
		description="The goal parameter identifies the goal to be waited for.")
})
public class WaitForGoalTask	implements ITask
{
	/**
	 *  Execute the task.
	 */
	public IFuture<Void> execute(final ITaskContext context, IInternalAccess instance)
	{
		final Future<Void> ret = new Future<Void>();
		
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
	public IFuture<Void> cancel(final IInternalAccess instance)
	{
		return IFuture.DONE;
	}
	
//	//-------- static methods --------
//	
//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static TaskMetaInfo getMetaInfo()
//	{
//		String desc = "The wait for goal task can be used to wait for an existing goal.";
//		
//		ParameterMetaInfo goalmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			IGoal.class, "goal", null, "The goal parameter identifies the goal to be waited for.");
//
//		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{goalmi}); 
//	}
}
