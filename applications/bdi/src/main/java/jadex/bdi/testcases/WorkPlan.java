package jadex.bdi.testcases;

import jadex.bdiv3x.runtime.Plan;

/**
 *  This plan does some work and prints out sth.
 */
public class WorkPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		while(true)
		{
			getLogger().info("Working: "+this);
			waitFor(1000);
		}
	}
	
	/**
	 *  The plan was aborted.
	 */
	public void aborted()
	{
		getLogger().info("Aborted: "+this);
	}
}
