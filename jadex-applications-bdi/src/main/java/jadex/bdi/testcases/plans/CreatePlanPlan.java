package jadex.bdi.testcases.plans;

import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Plan that programmatically creates a plan.
 */
public class CreatePlanPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		IMPlan mplan = ((IMPlanbase)getPlanbase().getModelElement()).getPlan("startplan");
		IPlan plan = getPlanbase().createPlan(mplan);
		plan.startPlan();
	}
}

