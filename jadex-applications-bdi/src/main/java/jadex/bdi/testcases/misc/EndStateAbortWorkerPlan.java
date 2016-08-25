package jadex.bdi.testcases.misc;

import jadex.bdiv3x.runtime.Plan;

/**
 *  This plan performs an illegal cleanup action. 
 */
public class EndStateAbortWorkerPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Do nothing...
	}
	
	/**
	 *  Passed never ends...
	 */
	public void passed()
	{
		try
		{
			System.out.println("before wait");
			waitForEver();
			System.out.println("after wait");
		}
		finally
		{
			System.out.println("finally wait");
			EndStateAbortPlan.TERMINATED.add(getComponentIdentifier());
		}
	}
}
