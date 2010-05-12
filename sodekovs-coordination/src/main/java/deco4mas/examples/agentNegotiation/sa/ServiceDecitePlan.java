package deco4mas.examples.agentNegotiation.sa;

import jadex.bdi.runtime.Plan;

/**
 * Decite if service is acceptable
 */
public class ServiceDecitePlan extends Plan
{
	public void body()
	{
		String myService = (String) getBeliefbase().getBelief("providedService").getFact();
		String actionService = (String) getParameter("action").getValue();
		if (!myService.equals(actionService) || (Boolean)getBeliefbase().getBelief("blackout").getFact())
		{
			getParameter("accept").setValue(Boolean.FALSE);
		} else
		{
			getParameter("accept").setValue(Boolean.TRUE);
		}
	}
}
