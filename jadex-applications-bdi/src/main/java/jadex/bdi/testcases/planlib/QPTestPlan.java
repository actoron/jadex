package jadex.bdi.testcases.planlib;

import java.util.List;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.bdi.testcases.AbstractMultipleAgentsPlan;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;

/**
 *  Test the request protocol execution.
 */
public class QPTestPlan extends AbstractMultipleAgentsPlan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Create 1 participants
		Map[] args = new Map[1];
		List agents = createAgents("/jadex/bdi/testcases/planlib/QPReceiver.agent.xml", args);	

		TestReport tr = new TestReport("#1", "Test query protocol.");
		if(assureTest(tr))
		{
			try
			{
				IGoal query = createGoal("procap.qp_initiate");
				query.getParameter("action").setValue("Query a value");
				query.getParameter("receiver").setValue(agents.get(0));
				dispatchSubgoalAndWait(query);
				getLogger().info("Query result:"+query.getParameter("result").getValue());
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
