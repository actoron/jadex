package jadex.bdi.testcases.beliefs;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Just acts facts to a beliefset.
 *  This plan is used by the WaitForFactAdded testcase.
 */
public class FactAdderPlan extends Plan
{
	public void body()
	{
		String instancename = (String)getParameter("instance").getValue();

		waitFor(100); // wait for the listening plan to start
		for(int i=0; i < 5; i++)
		{
			String fact = instancename + " " + i;
			getBeliefbase().getBeliefSet("beliefSetToAddFacts").addFact(fact);
			getLogger().info("added fact: " + fact);
			waitFor(50);
		}
	}
}
