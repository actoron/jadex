package jadex.gpmn.plan;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  Plan for parallel goal activation in GPMN processes.
 *
 */
public class ParallelActivationPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void body()
	{
		String[] goalnames = (String[]) getParameter("goals").getValue();
		IGoal[] goals = new IGoal[goalnames.length];
		for (int i = 0; i < goalnames.length; ++i)
		{
			goals[i] = createGoal(goalnames[i]);
			dispatchSubgoal(goals[i]);
		}
		
		for (int i = 0; i < goals.length; ++i)
		{
			waitForGoal(goals[i]);
			if (!goals[i].isSucceeded())
			{
				for (int j = 0; j < goals.length; ++j)
				{
					goals[j].drop();
				}
				fail(goals[i].getException());
			}
		}
	}
}
