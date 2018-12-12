package jadex.bdi.testcases.semiautomatic;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Test if the agent can be properly deleted.
 */
public class DeletionPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		getLogger().info("Oki executing body.");
		getLogger().info("Now trying to delete myself.");
		getLogger().info("When no more output test has failed.");

		startAtomic();
		killAgent();
		getLogger().info("Alive while plan is running (When no more outputs, test succeeded).");
		endAtomic();

		getLogger().info("Still alive (TEST FAILED).");
	}
}

