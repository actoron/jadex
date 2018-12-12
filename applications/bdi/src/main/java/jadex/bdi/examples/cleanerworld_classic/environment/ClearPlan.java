package jadex.bdi.examples.cleanerworld_classic.environment;

import jadex.bdi.examples.cleanerworld_classic.Environment;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Delete the singleton environment to start the
 *  application next time with a new one. 
 */
public class ClearPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		Environment.clearInstance();
	}
}
