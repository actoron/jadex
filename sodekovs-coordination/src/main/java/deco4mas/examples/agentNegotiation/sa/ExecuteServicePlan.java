package deco4mas.examples.agentNegotiation.sa;

import jadex.bdi.runtime.Plan;

/**
 * Execute a service
 */
public class ExecuteServicePlan extends Plan
{
	public void body()
	{
		Integer waitlength = (Integer) getBeliefbase().getBelief("serviceLength").getFact();
		waitFor(waitlength);
		System.out.println(this.getComponentIdentifier().getLocalName() + ": SERVICE ("
			+ getBeliefbase().getBelief("providedService").getFact() + ")" + "[" + waitlength + "] " + "EXECUTED!");
//		System.out.println();

		getParameter("result").setValue(Boolean.TRUE);
//		getParameter("result").setValue(Boolean.FALSE);
	}
}
