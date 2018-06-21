package jadex.gpmn.plan;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Plan for sequential goal activation in GPMN processes.
 *
 */
public class SequentialActivationPlan extends Plan
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
			dispatchSubgoalAndWait(goals[i]);
			if (!goals[i].isSucceeded())
			{
				fail(goals[i].getException());
			}
		}
	}
}
