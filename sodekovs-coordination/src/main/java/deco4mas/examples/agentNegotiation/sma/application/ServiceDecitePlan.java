package deco4mas.examples.agentNegotiation.sma.application;

import jadex.bdi.runtime.Plan;

/**
 * Decite if service is acceptable
 */
public class ServiceDecitePlan extends Plan
{
	public void body()
	{
		try
		{
			getParameter("accept").setValue(Boolean.TRUE);
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
