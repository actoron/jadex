package jadex.bdi.planlib.simsupport.simcap;

import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.simobject.task.ISimObjectTask;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;


/**
 * Plan setting the velocity (direction and speed) of the object.
 */
public class LocalAddTaskPlan extends Plan
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
		ISimObjectTask task = (ISimObjectTask)getParameter("task").getValue();
		engine.getSimulationObject(objectId).addTask(task);
	}
}
