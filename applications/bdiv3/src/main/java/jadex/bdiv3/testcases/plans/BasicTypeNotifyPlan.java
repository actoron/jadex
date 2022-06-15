package jadex.bdiv3.testcases.plans;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;

@Plan
public class BasicTypeNotifyPlan 
{
	@PlanCapability
	protected BasicTypeConditionBDI agent;

	@PlanAPI
	protected IPlan plan;
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		agent.notify(plan);
	}
}
