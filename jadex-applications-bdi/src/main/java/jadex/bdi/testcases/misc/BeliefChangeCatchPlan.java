package jadex.bdi.testcases.misc;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Test if plan can be invoked when trigger is added dynamically at runtime.
 */
public class BeliefChangeCatchPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		getLogger().info("Plan invoked.");
		getBeliefbase().getBelief("invoked").setFact(Boolean.TRUE);
	}
}
