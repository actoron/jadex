package jadex.bdi.planlib.test;

import jadex.bdi.runtime.Plan;

/**
 *  Stop the test agent.
 */
public class FinishedPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		killComponent();
	}
}
