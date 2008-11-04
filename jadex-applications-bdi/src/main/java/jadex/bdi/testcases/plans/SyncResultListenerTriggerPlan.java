package jadex.bdi.testcases.plans;

import jadex.bdi.runtime.Plan;

/**
 *  Testing waiting for sync results.
 */
public class SyncResultListenerTriggerPlan extends Plan
{
	/**
	 *  The body method.
	 */
	public void body()
	{
		// Make sure other plan is executed first. 
		waitFor(10);

		// Set result of listener.
		SyncResultListener	listener	= (SyncResultListener)getBeliefbase().getBelief("listener").getFact();
		listener.resultAvailable("success");
	}
}
