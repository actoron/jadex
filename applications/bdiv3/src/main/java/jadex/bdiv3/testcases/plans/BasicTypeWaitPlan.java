package jadex.bdiv3.testcases.plans;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.runtime.IPlan;

@Plan
public class BasicTypeWaitPlan 
{	
	@PlanAPI
	protected IPlan plan;
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		System.out.println("waiting for notification");
		//plan.waitForFactChanged("mybel").get();
		plan.waitForBeliefChanged("mybel").get();
		//((RPlan)plan).waitForFactX("mybel", new String[]{ChangeEvent.BELIEFCHANGED}, -1, null).get();
		System.out.println("received notification");
	}
}
