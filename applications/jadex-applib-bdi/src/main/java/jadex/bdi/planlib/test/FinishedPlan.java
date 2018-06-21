package jadex.bdi.planlib.test;

import jadex.bdiv3x.runtime.Plan;

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
//		System.out.println("killing "+getComponentName()+" due to "+getReason());
		killAgent();
	}
}
