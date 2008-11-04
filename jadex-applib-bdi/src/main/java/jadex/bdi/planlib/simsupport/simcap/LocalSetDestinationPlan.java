package jadex.bdi.planlib.simsupport.simcap;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.SimObject;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;

/** Plan setting the velocity (direction and speed) of the object.
 */
public class LocalSetDestinationPlan extends Plan
{
	/** Sets the new velocity of the object.
	 */
	public void body()
	{
		Integer objectId = (Integer) getParameter("object_id").getValue();
		IBeliefbase b = getBeliefbase();
		ISimulationEngine engine =
			(ISimulationEngine) b.getBelief("local_simulation_engine").getFact();
		IVector2 destination = (IVector2) getParameter("destination").getValue();
		IVector1 speed = (IVector1) getParameter("speed").getValue();
		IVector1 tolerance = (IVector1) getParameter("tolerance").getValue();
		SimObject obj = engine.getSimulationObject(objectId);
		obj.setDestination(destination, speed, tolerance);
	}
}
