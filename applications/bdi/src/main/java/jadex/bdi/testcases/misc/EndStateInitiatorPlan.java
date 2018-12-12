package jadex.bdi.testcases.misc;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Check correct operation of end states.
 */
public class EndStateInitiatorPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		// Set belief to true to trigger creation of goal.
		// Will be checked in worker plan.
		getBeliefbase().getBelief("trigger").setFact(Boolean.TRUE);
		
		waitFor(300);

		// Kill agent to start end state.
		killAgent();
	}
}
