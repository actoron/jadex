package jadex.bdi.testcases.semiautomatic;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

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
		IComponentIdentifier ams = (IComponentIdentifier)getBeliefbase().getBelief("cms").getFact();
		IGoal sd = createGoal("cms_shutdown_platform");
		sd.getParameter("cms").setValue(ams);
		try
		{
			dispatchSubgoalAndWait(sd);
			System.out.println("Remote platform successfully shutdowned.");
		}
		catch(GoalFailureException e)
		{
			e.printStackTrace();
		}
	}

}
