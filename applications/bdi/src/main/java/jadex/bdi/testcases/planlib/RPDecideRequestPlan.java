package jadex.bdi.testcases.planlib;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Decide to agree/refuse executing a task.
 */
public class RPDecideRequestPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		getParameter("accept").setValue(Boolean.TRUE);
	}
}

