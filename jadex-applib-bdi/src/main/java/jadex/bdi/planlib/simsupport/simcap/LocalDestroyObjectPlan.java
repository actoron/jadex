package jadex.bdi.planlib.simsupport.simcap;

import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;


/**
 * Plan for destroying simulation objects on a local simulation engine.
 */
public class LocalDestroyObjectPlan extends Plan
{
	/**
	 * Destroys a simulation object on the local simulation engine.
	 */
	public void body()
	{
		Integer objectId = (Integer)getParameter("object_id").getValue();
		IBeliefbase b = getBeliefbase();
		ISimulationEngine engine = (ISimulationEngine)b.getBelief(
				"local_simulation_engine").getFact();
		engine.destroySimObject(objectId);
	}
}
