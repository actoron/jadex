package jadex.bdi.testcases.semiautomatic;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Plan to continuously wait for a condition.
 */
public class ConditionPlan extends Plan
{
	public void body()
	{
		while(true)
		{
			waitForCondition("five_seconds");
			System.out.println("woken up: "+getClock().getTime());
			
			// Avoid condition being still true.
			waitFor(1000);
		} 
	}
}
