package jadex.bdi.testcases.plans;

import jadex.bdi.runtime.Plan;

/**
 *  The activity plan waits for a long time
 *  and then prints something again.
 */
public class ActivityPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public ActivityPlan()
	{
		getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		long time = 10000;
		getLogger().info("Doing some work for "+(time/1000)+" seconds.");
		waitFor(time);
		getLogger().info("Hi I am still alive :-(");
	}
}
