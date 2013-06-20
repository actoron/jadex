package jadex.bdi.testcases.semiautomatic;

import jadex.bdi.runtime.Plan;

/**
 *  Reason about an incoming service call.
 */
public class ServiceReasonPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		if(Math.random()>0.5)
		{
			System.out.println("reason true");
			getParameter("execute").setValue(true);
		}
		else
		{
			System.out.println("reason false");
			getParameter("execute").setValue(false);
		}
	}
}
