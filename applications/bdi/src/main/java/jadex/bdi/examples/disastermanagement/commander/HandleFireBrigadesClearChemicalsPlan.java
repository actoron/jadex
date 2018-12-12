package jadex.bdi.examples.disastermanagement.commander;

/**
 *  Allocate fire brigades to disasters for clearing chemicals.
 */
public class HandleFireBrigadesClearChemicalsPlan extends HandleForcesPlan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		allocateForces("clearchemicalsservices", "chemicals");
	}
}
