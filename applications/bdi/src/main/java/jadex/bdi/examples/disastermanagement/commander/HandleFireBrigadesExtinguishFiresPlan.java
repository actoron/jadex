package jadex.bdi.examples.disastermanagement.commander;

/**
 *  Allocate fire brigades to disasters for extinguishing fires.
 */
public class  HandleFireBrigadesExtinguishFiresPlan extends HandleForcesPlan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		allocateForces("extinguishfireservices", "fire");
	}
}
