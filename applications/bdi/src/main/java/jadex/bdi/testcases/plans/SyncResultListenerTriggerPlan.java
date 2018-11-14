package jadex.bdi.testcases.plans;

import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.Future;

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
		Future<Object>	listener	= (Future<Object>)getBeliefbase().getBelief("listener").getFact();
		listener.setResult("success");
	}
}
