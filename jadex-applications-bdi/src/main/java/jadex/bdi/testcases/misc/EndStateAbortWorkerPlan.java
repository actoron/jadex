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
			while(true)
			{
				System.out.println("before wait");
				waitFor(100);
				System.out.println("after wait");
			}
		}
		finally
		{
			System.out.println("no more wait");
			EndStateAbortPlan.TERMINATED.add(getComponentIdentifier());
		}
	}
}
