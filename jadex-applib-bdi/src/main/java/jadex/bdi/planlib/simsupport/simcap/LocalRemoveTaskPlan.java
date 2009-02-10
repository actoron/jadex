package jadex.bdi.planlib.simsupport.simcap;

import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;


/**
 * Plan setting the velocity (direction and speed) of the object.
 */
public class LocalRemoveTaskPlan extends Plan
{
	/**
	 * Sets the new velocity of the object.
	 */
	public void body()
	{
		Integer objectId = (Integer)getParameter("object_id").getValue();
		IBeliefbase b = getBeliefbase();
		ISimulationEngine engine = (ISimulationEngine)b.getBelief(
				"local_simulation_engine").getFact();
		String taskName = (String)getParameter("task_name").getValue();
		// TODO: Don't use direct SimObject access
		engine.getSimulationObject(objectId).removeTask(taskName);
	}
}
