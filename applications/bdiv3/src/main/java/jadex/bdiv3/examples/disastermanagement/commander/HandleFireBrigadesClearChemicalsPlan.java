package jadex.bdiv3.examples.disastermanagement.commander;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;

/**
 *  Allocate fire brigades to disasters for clearing chemicals.
 */
@Plan
public class HandleFireBrigadesClearChemicalsPlan extends HandleForcesPlan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	@PlanBody
	public void	body()
	{
		allocateForces("clearchemicalsservices", "chemicals");
	}
}
