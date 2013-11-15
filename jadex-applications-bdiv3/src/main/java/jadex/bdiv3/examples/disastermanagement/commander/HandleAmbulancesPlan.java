package jadex.bdiv3.examples.disastermanagement.commander;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;

/**
 *  Allocate ambulances to disasters for treating victims.
 */
@Plan
public class HandleAmbulancesPlan extends HandleForcesPlan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	@PlanBody
	public void	body()
	{
		allocateForces("treatvictimservices", "victims");
	}
}
