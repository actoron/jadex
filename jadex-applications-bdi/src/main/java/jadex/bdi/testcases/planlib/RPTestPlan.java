package jadex.bdi.testcases.planlib;

import jadex.base.test.TestReport;
import jadex.bdi.testcases.AbstractMultipleAgentsPlan;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;

import java.util.List;
import java.util.Map;

/**
 *  Test the request protocol execution.
 */
public class RPTestPlan extends AbstractMultipleAgentsPlan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Create 1 participants
		Map[] args = new Map[1];
		List agents = createAgents("/jadex/bdi/testcases/planlib/RPReceiver.agent.xml", args);	

		TestReport tr = new TestReport("#1", "Test request protocol.");
		if(assureTest(tr))
		{
			try
			{
				IGoal request = createGoal("procap.rp_initiate");
				request.getParameter("action").setValue("Request a task.");
				request.getParameter("receiver").setValue(agents.get(0));
				dispatchSubgoalAndWait(request);
				getLogger().info("Request result:"+request.getParameter("result").getValue());
				tr.setSucceeded(true);
			}
			catch(GoalFailureException e)
			{
				tr.setFailed("Exception occurred: "+e);
			}
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
