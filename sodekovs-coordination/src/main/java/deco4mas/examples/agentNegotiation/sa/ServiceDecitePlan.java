package deco4mas.examples.agentNegotiation.sa;

import deco4mas.examples.agentNegotiation.ServiceType;
import jadex.bdi.runtime.Plan;

/**
 * Decite if service is acceptable
 */
public class ServiceDecitePlan extends Plan
{
	public void body()
	{
		ServiceType myService = (ServiceType) getBeliefbase().getBelief("providedService").getFact();
		String myServiceName = myService.getName();
		String actionService = (String) getParameter("action").getValue();
		if (!myServiceName.equals(actionService) || (Boolean)getBeliefbase().getBelief("blackout").getFact())
		{
			getParameter("accept").setValue(Boolean.FALSE);
		} else
		{
			getParameter("accept").setValue(Boolean.TRUE);
		}
	}
}
