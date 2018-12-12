package jadex.bdi.examples.disastermanagement.commander;

/**
 *  Allocate ambulances to disasters for treating victims.
 */
public class HandleAmbulancesPlan extends HandleForcesPlan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		allocateForces("treatvictimservices", "victims");
	}
}
