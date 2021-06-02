package jadex.bdi.testcases.semiautomatic;

import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test the shutdown of a platform
 */
public class ShutdownTesterPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		try
		{
			getAgent().getExternalAccess(getComponentIdentifier().getRoot()).killComponent().get();
			System.out.println("Remote platform successfully shutdowned.");
		}
		catch(GoalFailureException e)
		{
			e.printStackTrace();
		}
	}

}
