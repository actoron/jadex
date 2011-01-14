package jadex.bdi.examples.disastermanagement.commander;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.examples.disastermanagement.ITreatVictimsService;
import jadex.bdi.runtime.Plan;

/**
 * 
 */
public class TreatVictimsPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		ISpaceObject disaster = (ISpaceObject)getParameter("disaster").getValue();
		ITreatVictimsService force = (ITreatVictimsService)getParameter("rescueforce").getValue();
		force.treatVictims(disaster).get(this);
	}
}
