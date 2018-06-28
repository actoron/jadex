package jadex.bdi.testcases.goals;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.TimeoutException;

/**
 *
 */
public class SubgoalTimeoutPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		IGoal sg = createGoal("work_goal");
		try
		{
			dispatchSubgoalAndWait(sg, 1000);
		}
		catch(TimeoutException e)
		{
			getLogger().info("Timeout: "+e);
		}
		getLogger().info("Result is: "+sg.getParameter("result").getValue());
		waitFor(3000);
		getLogger().info("End of timeout plan.");
	}
}
