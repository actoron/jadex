package jadex.bdi.examples.disasterrescue.commander;

import jadex.bdi.runtime.Plan;

/**
 * 
 */
public class HandleDisasterPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		System.out.println("handle disaster: "+getParameter("disaster").getValue());
	}
}
