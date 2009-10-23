package jadex.bdi.amsagent;

import jadex.adapter.base.fipa.AMSCreateAgent;
import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

/**
 *  Create an agent.
 */
public class AMSCreateAgentPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{		
		AMSCreateAgent ca = (AMSCreateAgent)getParameter("action").getValue();

		IGoal cag = createGoal("ams_create_agent");
		cag.getParameter("name").setValue(ca.getName());
		cag.getParameter("type").setValue(ca.getType());
		cag.getParameter("configuration").setValue(ca.getConfiguration());
		cag.getParameter("arguments").setValue(ca.getArguments());
		dispatchSubgoalAndWait(cag);

		ca.setAgentIdentifier((IComponentIdentifier)cag.getParameter("agentidentifier").getValue());
		getParameter("result").setValue(new Done(ca));
	}
}
