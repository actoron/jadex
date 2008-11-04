package jadex.bdi.testcases.semiautomatic;

import jadex.bdi.runtime.Plan;

/**
 *  Plan to continuously wait for a condition.
 */
public class ConditionPlan extends Plan
{
	public void body()
	{
		while(true)
		{
			System.out.println("waiting...");
			long start = getClock().getTime();
			waitForCondition("five_seconds");
			long end = getClock().getTime();
			
			System.out.println("woken up: "+(end-start));
		} 
	}
}
