package jadex.bdi.testcases.semiautomatic;

import jadex.bdiv3x.runtime.Plan;

/**
 *  This plan performs an illegal action. 
 */
public class EndStateAbortPlan extends Plan
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
		while(true)
			waitFor(1000);
	}
}
