package jadex.bdi.examples.garbagecollector_classic;

import jadex.bdi.runtime.Plan;

/**
 *  Delete the singleton environment to start the
 *  application next time with a new one. 
 */
public class ClearPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instatiated plan instance from the scheduler.
	 */
	public void	body()
	{
		Environment.clearInstance();
	}
}
