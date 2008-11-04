package jadex.bdi.amsagent;

import jadex.adapter.base.fipa.AMSDestroyAgent;
import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  Destroy an agent.
 */
public class AMSDestroyAgentPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		AMSDestroyAgent da = (AMSDestroyAgent)getParameter("action").getValue();

		IGoal dag = createGoal("ams_destroy_agent");
		dag.getParameter("agentidentifier").setValue(da.getAgentIdentifier());
		dispatchSubgoalAndWait(dag);

		getParameter("result").setValue(new Done(da));
	}
}
