package deco4mas.examples.agentNegotiation.sma;

import deco4mas.examples.agentNegotiation.ServiceType;
import jadex.bdi.runtime.Plan;

/**
 * Decite if service is acceptable
 */
public class ServiceDecitePlan extends Plan
{
	public void body()
	{
		ServiceType myService = (ServiceType) getBeliefbase().getBelief("allocatedService").getFact();
		String myServiceName = myService.getName();
		String actionService = (String) getParameter("action").getValue();
		if (!myServiceName.equals(actionService))
		{
			getParameter("accept").setValue(Boolean.FALSE);
		} else
		{
			getParameter("accept").setValue(Boolean.TRUE);
		}
	}
}
