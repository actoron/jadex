package jadex.bdi.testcases.planlib;

import jadex.base.test.TestReport;
import jadex.bdi.testcases.AbstractMultipleAgentsPlan;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;

import java.util.List;
import java.util.Map;

/**
 * Test the ping capability.
 */
public class PingTestPlan extends AbstractMultipleAgentsPlan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Create 1 participants
		Map[] args = new Map[1];
		List agents = createAgents("/jadex/bdi/testcases/planlib/PingReceiver.agent.xml", args);	

		TestReport tr = new TestReport("#1", "Test single ping message.");
		if(assureTest(tr))
		{
			try
			{
				IGoal ping = createGoal("pingcap.ping");
				ping.getParameter("receiver").setValue(agents.get(0));
				dispatchSubgoalAndWait(ping);
				getLogger().info("Ping result:"+ping.getParameter("result").getValue());
				tr.setSucceeded(true);
			}
			catch(GoalFailureException e)
			{
				tr.setFailed("Exception occurred: "+e);
			}
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		tr = new TestReport("#2", "Test pinging.");
		if(assureTest(tr))
		{
			IGoal pinging = createGoal("pingcap.pinging");
			pinging.getParameter("receiver").setValue(agents.get(0));
			try
			{
				dispatchSubgoalAndWait(pinging, 2000);
			}
			catch(TimeoutException e)
			{
				int triescnt = ((Integer)pinging.getParameter("missed_cnt").getValue()).intValue();
				getLogger().info("Pinging triescnt:"+triescnt);
				if(triescnt==0)
					tr.setSucceeded(true);
				else
					tr.setFailed("Triescnt is not 0:"+triescnt);
			}
			catch(GoalFailureException e)
			{
				tr.setFailed("Exception occurred: "+e);
			}
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}

