package deco4mas.examples.agentNegotiation.sma;

import jadex.bdi.runtime.Plan;

/**
 * Decite if service is acceptable
 */
public class ServiceDecitePlan extends Plan
{
	public void body()
	{
		getParameter("accept").setValue(Boolean.TRUE);
	}
}
