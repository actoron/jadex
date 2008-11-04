package jadex.bdi.testcases.semiautomatic;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IAgentIdentifier;

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
		IAgentIdentifier ams = (IAgentIdentifier)getBeliefbase().getBelief("ams").getFact();
		IGoal sd = createGoal("ams_shutdown_platform");
		sd.getParameter("ams").setValue(ams);
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
