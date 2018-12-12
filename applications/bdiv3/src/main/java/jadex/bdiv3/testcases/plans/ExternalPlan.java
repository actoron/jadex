package jadex.bdiv3.testcases.plans;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.Trigger;

/**
 * 
 */
@Plan(trigger=@Trigger(goals=ExternalPlanBDI.MyGoal.class))
public class ExternalPlan
{
	@PlanBody
	public void body()
	{
		System.out.println("body");
	}
}
